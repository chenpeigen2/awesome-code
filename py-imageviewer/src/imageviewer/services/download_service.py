"""Download service for downloading images from the internet."""

import asyncio
import uuid
from collections.abc import Callable
from datetime import datetime
from pathlib import Path

import httpx
from PIL import Image

from imageviewer.core.exceptions import DownloadError
from imageviewer.core.models import DownloadTask, ImageItem
from imageviewer.utils.config import get_settings
from imageviewer.utils.logger import get_logger

logger = get_logger()


class DownloadService:
    def __init__(self, max_concurrent: int = 3):
        self.max_concurrent = max_concurrent
        self._semaphore: asyncio.Semaphore | None = None
        self._client: httpx.AsyncClient | None = None
        self._tasks: dict[str, DownloadTask] = {}
        self._progress_callbacks: list[Callable[[DownloadTask], None]] = []

    async def _get_client(self) -> httpx.AsyncClient:
        if self._client is None or self._client.is_closed:
            self._client = httpx.AsyncClient(timeout=60.0, follow_redirects=True)
        return self._client

    async def _get_semaphore(self) -> asyncio.Semaphore:
        if self._semaphore is None:
            self._semaphore = asyncio.Semaphore(self.max_concurrent)
        return self._semaphore

    def add_progress_callback(self, callback: Callable[[DownloadTask], None]) -> None:
        self._progress_callbacks.append(callback)

    def remove_progress_callback(self, callback: Callable[[DownloadTask], None]) -> None:
        if callback in self._progress_callbacks:
            self._progress_callbacks.remove(callback)

    def _notify_progress(self, task: DownloadTask) -> None:
        for callback in self._progress_callbacks:
            try:
                callback(task)
            except Exception as e:
                logger.error(f"Progress callback error: {e}")

    async def download_image(
        self,
        image: ImageItem,
        save_path: Path | None = None,
        progress_callback: Callable[[float], None] | None = None,
    ) -> DownloadTask:
        task_id = str(uuid.uuid4())
        settings = get_settings()
        if save_path is None:
            save_dir = Path(settings.download_dir)
            save_dir.mkdir(parents=True, exist_ok=True)
            ext = ".jpg"
            if image.url:
                url_path = image.url.split("?")[0]
                if "." in url_path:
                    ext = "." + url_path.rsplit(".", 1)[-1]
            save_path = save_dir / f"{image.id}{ext}"
        task = DownloadTask(
            id=task_id,
            image=image,
            save_path=save_path,
            status="pending",
        )
        self._tasks[task_id] = task
        semaphore = await self._get_semaphore()
        async with semaphore:
            return await self._execute_download(task, progress_callback)

    async def _execute_download(
        self,
        task: DownloadTask,
        progress_callback: Callable[[float], None] | None = None,
    ) -> DownloadTask:
        task.status = "downloading"
        task.started_at = datetime.now()
        self._notify_progress(task)
        try:
            url = task.image.download_url or task.image.url
            if not url:
                raise DownloadError("No download URL available")
            client = await self._get_client()
            response = await client.get(url, follow_redirects=True)
            response.raise_for_status()
            total_size = int(response.headers.get("content-length", 0))
            downloaded = 0
            chunks = []
            async for chunk in response.aiter_bytes(chunk_size=8192):
                chunks.append(chunk)
                downloaded += len(chunk)
                if total_size > 0:
                    task.progress = downloaded / total_size
                    if progress_callback:
                        progress_callback(task.progress)
                    self._notify_progress(task)
            task.save_path.parent.mkdir(parents=True, exist_ok=True)
            with open(task.save_path, "wb") as f:
                for chunk in chunks:
                    f.write(chunk)
            task.status = "completed"
            task.progress = 1.0
            task.completed_at = datetime.now()
            logger.info(f"Download completed: {task.save_path}")
        except Exception as e:
            task.status = "failed"
            task.error_message = str(e)
            logger.error(f"Download failed: {e}")
        self._notify_progress(task)
        return task

    async def download_batch(
        self,
        images: list[ImageItem],
        save_dir: Path | None = None,
        progress_callback: Callable[[int, int], None] | None = None,
    ) -> list[DownloadTask]:
        settings = get_settings()
        if save_dir is None:
            save_dir = Path(settings.download_dir)
        save_dir.mkdir(parents=True, exist_ok=True)
        tasks = []
        completed = 0
        for image in images:
            ext = ".jpg"
            if image.url:
                url_path = image.url.split("?")[0]
                if "." in url_path:
                    ext = "." + url_path.rsplit(".", 1)[-1]
            save_path = save_dir / f"{image.id}{ext}"
            task = await self.download_image(image, save_path)
            tasks.append(task)
            completed += 1
            if progress_callback:
                progress_callback(completed, len(images))
        return tasks

    async def download_thumbnail(
        self,
        image: ImageItem,
        size: tuple[int, int] = (200, 200),
    ) -> Image.Image | None:
        url = image.thumbnail_url or image.url
        if not url:
            return None
        try:
            client = await self._get_client()
            response = await client.get(url)
            response.raise_for_status()
            from io import BytesIO
            img = Image.open(BytesIO(response.content))
            img.thumbnail(size, Image.Resampling.LANCZOS)
            return img
        except Exception as e:
            logger.error(f"Failed to download thumbnail: {e}")
            return None

    def get_task(self, task_id: str) -> DownloadTask | None:
        return self._tasks.get(task_id)

    def get_all_tasks(self) -> list[DownloadTask]:
        return list(self._tasks.values())

    def clear_completed_tasks(self) -> None:
        completed_ids = [tid for tid, task in self._tasks.items() if task.status in ("completed", "failed")]
        for tid in completed_ids:
            del self._tasks[tid]
        logger.info(f"Cleared {len(completed_ids)} completed tasks")

    async def close(self) -> None:
        if self._client and not self._client.is_closed:
            await self._client.aclose()
            self._client = None

    async def __aenter__(self) -> "DownloadService":
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb) -> None:
        await self.close()
