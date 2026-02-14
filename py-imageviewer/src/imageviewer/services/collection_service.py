"""Collection management service for saving and organizing images."""

import json
import zipfile
from datetime import datetime
from pathlib import Path
from typing import Any
import shutil

from imageviewer.core.models import ImageItem
from imageviewer.utils.config import get_settings
from imageviewer.utils.logger import get_logger

logger = get_logger()


class ImageCollection:
    def __init__(self, name: str, path: Path):
        self.name = name
        self.path = path
        self.images: list[ImageItem] = []
        self.created_at = datetime.now()
        self.updated_at = datetime.now()
        self.metadata: dict[str, Any] = {}
        self._ensure_directory()

    def _ensure_directory(self) -> None:
        self.path.mkdir(parents=True, exist_ok=True)

    def add_image(self, image: ImageItem) -> None:
        self.images.append(image)
        self.updated_at = datetime.now()
        self._save_metadata()

    def remove_image(self, image_id: str) -> bool:
        for i, img in enumerate(self.images):
            if img.id == image_id:
                self.images.pop(i)
                self.updated_at = datetime.now()
                self._save_metadata()
                return True
        return False

    def _save_metadata(self) -> None:
        metadata_path = self.path / "collection.json"
        data = {
            "name": self.name,
            "created_at": self.created_at.isoformat(),
            "updated_at": self.updated_at.isoformat(),
            "images": [img.model_dump(mode="json") for img in self.images],
            "metadata": self.metadata,
        }
        with open(metadata_path, "w", encoding="utf-8") as f:
            json.dump(data, f, indent=2, default=str, ensure_ascii=False)

    @classmethod
    def load(cls, path: Path) -> "ImageCollection":
        metadata_path = path / "collection.json"
        if not metadata_path.exists():
            return cls(path.name, path)
        with open(metadata_path, encoding="utf-8") as f:
            data = json.load(f)
        collection = cls(data.get("name", path.name), path)
        collection.created_at = datetime.fromisoformat(data.get("created_at", datetime.now().isoformat()))
        collection.updated_at = datetime.fromisoformat(data.get("updated_at", datetime.now().isoformat()))
        collection.metadata = data.get("metadata", {})
        collection.images = [ImageItem(**img) for img in data.get("images", [])]
        return collection

    def export_to_json(self, output_path: Path) -> None:
        data = {
            "name": self.name,
            "exported_at": datetime.now().isoformat(),
            "image_count": len(self.images),
            "images": [img.model_dump(mode="json") for img in self.images],
        }
        with open(output_path, "w", encoding="utf-8") as f:
            json.dump(data, f, indent=2, default=str, ensure_ascii=False)
        logger.info(f"Exported collection to JSON: {output_path}")

    def export_to_zip(self, output_path: Path, include_images: bool = True) -> None:
        with zipfile.ZipFile(output_path, "w", zipfile.ZIP_DEFLATED) as zf:
            metadata_json = json.dumps(
                {
                    "name": self.name,
                    "exported_at": datetime.now().isoformat(),
                    "image_count": len(self.images),
                    "images": [img.model_dump(mode="json") for img in self.images],
                },
                indent=2,
                default=str,
                ensure_ascii=False,
            )
            zf.writestr("collection.json", metadata_json)
            if include_images:
                images_dir = self.path / "images"
                if images_dir.exists():
                    for image_file in images_dir.iterdir():
                        if image_file.is_file():
                            zf.write(image_file, f"images/{image_file.name}")
        logger.info(f"Exported collection to ZIP: {output_path}")


class CollectionService:
    def __init__(self):
        settings = get_settings()
        self.collections_dir = Path(settings.collections_dir)
        self._collections: dict[str, ImageCollection] = {}
        self._ensure_base_directory()

    def _ensure_base_directory(self) -> None:
        self.collections_dir.mkdir(parents=True, exist_ok=True)

    def get_collections(self) -> list[ImageCollection]:
        collections = []
        for item in self.collections_dir.iterdir():
            if item.is_dir() and (item / "collection.json").exists():
                try:
                    collection = ImageCollection.load(item)
                    collections.append(collection)
                    self._collections[item.name] = collection
                except Exception as e:
                    logger.error(f"Failed to load collection {item}: {e}")
        return sorted(collections, key=lambda c: c.updated_at, reverse=True)

    def create_collection(self, name: str) -> ImageCollection:
        safe_name = "".join(c for c in name if c.isalnum() or c in (" ", "-", "_")).strip()
        if not safe_name:
            safe_name = f"collection_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
        collection_path = self.collections_dir / safe_name
        if collection_path.exists():
            counter = 1
            while (self.collections_dir / f"{safe_name}_{counter}").exists():
                counter += 1
            collection_path = self.collections_dir / f"{safe_name}_{counter}"
        collection = ImageCollection(name, collection_path)
        self._collections[collection_path.name] = collection
        logger.info(f"Created collection: {name}")
        return collection

    def get_collection(self, name: str) -> ImageCollection | None:
        if name in self._collections:
            return self._collections[name]
        collection_path = self.collections_dir / name
        if collection_path.exists():
            collection = ImageCollection.load(collection_path)
            self._collections[name] = collection
            return collection
        return None

    def delete_collection(self, name: str) -> bool:
        collection_path = self.collections_dir / name
        if collection_path.exists():
            shutil.rmtree(collection_path)
            if name in self._collections:
                del self._collections[name]
            logger.info(f"Deleted collection: {name}")
            return True
        return False

    def add_image_to_collection(self, collection_name: str, image: ImageItem) -> bool:
        collection = self.get_collection(collection_name)
        if collection:
            collection.add_image(image)
            return True
        return False

    def remove_image_from_collection(self, collection_name: str, image_id: str) -> bool:
        collection = self.get_collection(collection_name)
        if collection:
            return collection.remove_image(image_id)
        return False

    def search_collections(self, query: str) -> list[ImageCollection]:
        query_lower = query.lower()
        results = []
        for collection in self.get_collections():
            if query_lower in collection.name.lower():
                results.append(collection)
            elif any(query_lower in img.title.lower() or query_lower in img.description.lower() for img in collection.images):
                results.append(collection)
        return results

    def export_collection(self, collection_name: str, output_path: Path, format: str = "json") -> bool:
        collection = self.get_collection(collection_name)
        if not collection:
            return False
        if format == "json":
            collection.export_to_json(output_path)
        elif format == "zip":
            collection.export_to_zip(output_path)
        else:
            return False
        return True
