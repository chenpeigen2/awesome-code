"""Tests for download service."""

from pathlib import Path

import pytest

from imageviewer.core.models import ImageItem, ImageSource
from imageviewer.services.download_service import DownloadService


class TestDownloadService:
    def test_init(self):
        service = DownloadService()
        assert service.max_concurrent == 3

    def test_init_with_custom_concurrent(self):
        service = DownloadService(max_concurrent=5)
        assert service.max_concurrent == 5

    def test_get_task_empty(self):
        service = DownloadService()
        task = service.get_task("nonexistent")
        assert task is None

    def test_get_all_tasks_empty(self):
        service = DownloadService()
        tasks = service.get_all_tasks()
        assert len(tasks) == 0

    def test_clear_completed_tasks(self):
        service = DownloadService()
        service.clear_completed_tasks()
        assert len(service.get_all_tasks()) == 0

    @pytest.mark.asyncio
    async def test_download_thumbnail_invalid_url(self):
        service = DownloadService()
        image = ImageItem(
            id="test",
            source=ImageSource.ONLINE,
            url="https://invalid.example.com/image.jpg",
        )
        result = await service.download_thumbnail(image)
        assert result is None

    @pytest.mark.asyncio
    async def test_close(self):
        service = DownloadService()
        await service.close()
        assert service._client is None
