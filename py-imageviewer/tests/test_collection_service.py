"""Tests for Collection service."""

from pathlib import Path

import pytest

from imageviewer.core.models import ImageItem, ImageSource
from imageviewer.services.collection_service import CollectionService, ImageCollection


class TestImageCollection:
    def test_init(self, tmp_path: Path):
        collection = ImageCollection("Test Collection", tmp_path / "test_collection")
        assert collection.name == "Test Collection"
        assert len(collection.images) == 0

    def test_add_image(self, tmp_path: Path):
        collection = ImageCollection("Test Collection", tmp_path / "test_collection")
        image = ImageItem(id="test1", source=ImageSource.ONLINE, title="Test Image")
        collection.add_image(image)
        assert len(collection.images) == 1
        assert collection.images[0].id == "test1"

    def test_remove_image(self, tmp_path: Path):
        collection = ImageCollection("Test Collection", tmp_path / "test_collection")
        image = ImageItem(id="test1", source=ImageSource.ONLINE, title="Test Image")
        collection.add_image(image)
        result = collection.remove_image("test1")
        assert result is True
        assert len(collection.images) == 0

    def test_remove_nonexistent_image(self, tmp_path: Path):
        collection = ImageCollection("Test Collection", tmp_path / "test_collection")
        result = collection.remove_image("nonexistent")
        assert result is False

    def test_export_to_json(self, tmp_path: Path):
        collection = ImageCollection("Test Collection", tmp_path / "test_collection")
        image = ImageItem(id="test1", source=ImageSource.ONLINE, title="Test Image")
        collection.add_image(image)
        output_path = tmp_path / "export.json"
        collection.export_to_json(output_path)
        assert output_path.exists()

    def test_export_to_zip(self, tmp_path: Path):
        collection = ImageCollection("Test Collection", tmp_path / "test_collection")
        image = ImageItem(id="test1", source=ImageSource.ONLINE, title="Test Image")
        collection.add_image(image)
        output_path = tmp_path / "export.zip"
        collection.export_to_zip(output_path, include_images=False)
        assert output_path.exists()


class TestCollectionService:
    def test_init(self, tmp_path: Path, monkeypatch):
        from imageviewer.utils.config import reset_settings
        reset_settings()
        monkeypatch.setenv("IMAGEVIEWER_COLLECTIONS_DIR", str(tmp_path / "collections"))
        service = CollectionService()
        assert service.collections_dir == tmp_path / "collections"

    def test_create_collection(self, tmp_path: Path, monkeypatch):
        monkeypatch.setenv("IMAGEVIEWER_COLLECTIONS_DIR", str(tmp_path / "collections"))
        service = CollectionService()
        collection = service.create_collection("My Collection")
        assert collection.name == "My Collection"
        assert collection.path.exists()

    def test_get_collections_empty(self, tmp_path: Path, monkeypatch):
        monkeypatch.setenv("IMAGEVIEWER_COLLECTIONS_DIR", str(tmp_path / "collections"))
        service = CollectionService()
        collections = service.get_collections()
        assert len(collections) == 0

    def test_get_collection(self, tmp_path: Path, monkeypatch):
        monkeypatch.setenv("IMAGEVIEWER_COLLECTIONS_DIR", str(tmp_path / "collections"))
        service = CollectionService()
        created = service.create_collection("Test")
        retrieved = service.get_collection(created.path.name)
        assert retrieved is not None
        assert retrieved.name == "Test"

    def test_delete_collection(self, tmp_path: Path, monkeypatch):
        monkeypatch.setenv("IMAGEVIEWER_COLLECTIONS_DIR", str(tmp_path / "collections"))
        service = CollectionService()
        collection = service.create_collection("To Delete")
        result = service.delete_collection(collection.path.name)
        assert result is True
        assert not collection.path.exists()

    def test_add_image_to_collection(self, tmp_path: Path, monkeypatch):
        monkeypatch.setenv("IMAGEVIEWER_COLLECTIONS_DIR", str(tmp_path / "collections"))
        service = CollectionService()
        collection = service.create_collection("Test")
        image = ImageItem(id="img1", source=ImageSource.ONLINE, title="Test")
        result = service.add_image_to_collection(collection.path.name, image)
        assert result is True
        updated = service.get_collection(collection.path.name)
        assert len(updated.images) == 1
