"""Tests for local file service."""

from pathlib import Path

import pytest

from imageviewer.core.models import SortOrder
from imageviewer.services.local_service import LocalFileService


class TestLocalFileService:
    def test_init(self):
        service = LocalFileService()
        assert service.current_directory is None

    def test_set_directory(self, temp_image_dir: Path):
        service = LocalFileService()
        service.set_directory(temp_image_dir)
        assert service.current_directory == temp_image_dir

    def test_set_invalid_directory(self):
        service = LocalFileService()
        with pytest.raises(Exception):
            service.set_directory(Path("/nonexistent/path"))

    def test_scan_images(self, temp_image_dir: Path):
        service = LocalFileService()
        service.set_directory(temp_image_dir)
        images = service.scan_images()
        assert len(images) == 4

    def test_scan_images_sort_by_name(self, temp_image_dir: Path):
        service = LocalFileService()
        service.set_directory(temp_image_dir)
        images = service.scan_images(sort_order=SortOrder.NAME_ASC)
        assert images[0].title <= images[-1].title

    def test_get_drives(self):
        service = LocalFileService()
        drives = service.get_drives()
        assert len(drives) > 0

    def test_get_parent_directory(self, temp_image_dir: Path):
        service = LocalFileService()
        service.set_directory(temp_image_dir)
        parent = service.get_parent_directory()
        assert parent == temp_image_dir.parent

    def test_get_image_by_path(self, temp_image_dir: Path):
        service = LocalFileService()
        service.set_directory(temp_image_dir)
        image_files = list(temp_image_dir.glob("*.jpg"))
        if image_files:
            image = service.get_image_by_path(image_files[0])
            assert image is not None
            assert image.file_path == image_files[0]

    def test_clear_cache(self, temp_image_dir: Path):
        service = LocalFileService()
        service.set_directory(temp_image_dir)
        service.scan_images()
        assert len(service._image_cache) > 0
        service.clear_cache()
        assert len(service._image_cache) == 0
