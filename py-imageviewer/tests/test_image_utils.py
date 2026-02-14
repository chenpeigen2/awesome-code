"""Tests for image utilities."""

from pathlib import Path

import pytest
from PIL import Image

from imageviewer.core.models import ImageFormat, ImageTransform
from imageviewer.utils.image_utils import (
    apply_transform,
    get_image_format,
    is_supported_format,
    load_image,
    load_thumbnail,
)


class TestImageUtils:
    def test_get_image_format_jpeg(self, temp_image_dir: Path):
        jpg_path = temp_image_dir / "test_image_0.jpg"
        assert get_image_format(jpg_path) == ImageFormat.JPEG

    def test_get_image_format_png(self, temp_image_dir: Path):
        png_path = temp_image_dir / "test_alpha.png"
        assert get_image_format(png_path) == ImageFormat.PNG

    def test_get_image_format_unknown(self):
        path = Path("test.unknown")
        assert get_image_format(path) == ImageFormat.UNKNOWN

    def test_is_supported_format(self, temp_image_dir: Path):
        jpg_path = temp_image_dir / "test_image_0.jpg"
        assert is_supported_format(jpg_path) is True

    def test_is_supported_format_invalid(self):
        path = Path("test.txt")
        assert is_supported_format(path) is False

    def test_load_image(self, temp_image_dir: Path):
        jpg_path = temp_image_dir / "test_image_0.jpg"
        img = load_image(jpg_path)
        assert img is not None
        assert img.width == 100
        assert img.height == 100

    def test_load_image_invalid(self):
        img = load_image(Path("/nonexistent/image.jpg"))
        assert img is None

    def test_load_thumbnail(self, temp_image_dir: Path):
        jpg_path = temp_image_dir / "test_image_0.jpg"
        thumb = load_thumbnail(jpg_path, (50, 50))
        assert thumb is not None
        assert thumb.width <= 50
        assert thumb.height <= 50

    def test_apply_transform_rotation(self, temp_image_dir: Path):
        jpg_path = temp_image_dir / "test_image_0.jpg"
        img = load_image(jpg_path)
        transform = ImageTransform(rotation=90)
        rotated = apply_transform(img, transform)
        assert rotated.width == img.height
        assert rotated.height == img.width

    def test_apply_transform_scale(self, temp_image_dir: Path):
        jpg_path = temp_image_dir / "test_image_0.jpg"
        img = load_image(jpg_path)
        transform = ImageTransform(scale=0.5)
        scaled = apply_transform(img, transform)
        assert scaled.width == int(img.width * 0.5)
        assert scaled.height == int(img.height * 0.5)

    def test_apply_transform_flip(self, temp_image_dir: Path):
        jpg_path = temp_image_dir / "test_image_0.jpg"
        img = load_image(jpg_path)
        transform = ImageTransform(flip_horizontal=True)
        flipped = apply_transform(img, transform)
        assert flipped.size == img.size

    def test_transform_reset(self):
        transform = ImageTransform(rotation=90, scale=2.0, flip_horizontal=True)
        transform.reset()
        assert transform.rotation == 0
        assert transform.scale == 1.0
        assert transform.flip_horizontal is False
