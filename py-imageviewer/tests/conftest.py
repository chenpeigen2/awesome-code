"""Test configuration and fixtures."""

import sys
from pathlib import Path
from typing import Generator

import pytest
from PyQt6.QtWidgets import QApplication

sys.path.insert(0, str(Path(__file__).parent.parent / "src"))


@pytest.fixture(scope="session")
def qapp() -> Generator[QApplication, None, None]:
    app = QApplication.instance()
    if app is None:
        app = QApplication(sys.argv)
    yield app
    app.quit()


@pytest.fixture
def temp_image_dir(tmp_path: Path) -> Path:
    from PIL import Image
    for i in range(3):
        img = Image.new("RGB", (100, 100), color=(i * 50, i * 50, i * 50))
        img.save(tmp_path / f"test_image_{i}.jpg", "JPEG")
    img = Image.new("RGBA", (50, 50), color=(255, 0, 0, 128))
    img.save(tmp_path / "test_alpha.png", "PNG")
    return tmp_path
