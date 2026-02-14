"""Image viewer widget with zoom, rotate, and pan capabilities."""

import asyncio
from io import BytesIO
from pathlib import Path

from PIL import Image
from PyQt6.QtCore import Qt, pyqtSignal
from PyQt6.QtGui import QCursor, QImage, QKeyEvent, QMouseEvent, QWheelEvent
from PyQt6.QtWidgets import QLabel, QScrollArea, QVBoxLayout, QWidget

from imageviewer.core.models import ImageItem, ImageTransform
from imageviewer.utils.image_utils import apply_transform, load_image
from imageviewer.utils.logger import get_logger

logger = get_logger()


class ImageViewerWidget(QWidget):
    image_changed = pyqtSignal(object)
    transform_changed = pyqtSignal(object)

    def __init__(self, parent: QWidget | None = None):
        super().__init__(parent)
        self._current_image: Image.Image | None = None
        self._current_item: ImageItem | None = None
        self._transform = ImageTransform()
        self._pan_start: tuple[int, int] | None = None
        self._is_panning = False
        self._min_scale = 0.1
        self._max_scale = 10.0
        self._setup_ui()

    def _setup_ui(self) -> None:
        layout = QVBoxLayout(self)
        layout.setContentsMargins(0, 0, 0, 0)
        self._scroll_area = QScrollArea()
        self._scroll_area.setWidgetResizable(False)
        self._scroll_area.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self._scroll_area.setStyleSheet("background-color: #1a1a1a;")
        self._image_label = QLabel()
        self._image_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self._image_label.setStyleSheet("background-color: #1a1a1a;")
        self._image_label.setMinimumSize(1, 1)
        self._scroll_area.setWidget(self._image_label)
        layout.addWidget(self._scroll_area)
        self._scroll_area.setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAsNeeded)
        self._scroll_area.setVerticalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAsNeeded)
        self._image_label.setMouseTracking(True)
        self._image_label.setCursor(QCursor(Qt.CursorShape.ArrowCursor))

    def load_image_item(self, item: ImageItem) -> bool:
        if item.is_local and item.file_path:
            return self.load_local_image(item.file_path, item)
        elif item.is_online and item.url:
            asyncio.create_task(self._load_online_image(item))
            return True
        return False

    def load_local_image(self, path: Path, item: ImageItem | None = None) -> bool:
        try:
            self._current_image = load_image(path)
            if self._current_image:
                self._current_item = item
                self._transform.reset()
                self._update_display()
                self.image_changed.emit(item)
                logger.info(f"Loaded image: {path}")
                return True
        except Exception as e:
            logger.error(f"Failed to load image {path}: {e}")
        return False

    async def _load_online_image(self, item: ImageItem) -> None:
        import httpx
        try:
            async with httpx.AsyncClient(timeout=30.0) as client:
                response = await client.get(item.url)
                response.raise_for_status()
                self._current_image = Image.open(BytesIO(response.content))
                self._current_item = item
                self._transform.reset()
                self._update_display()
                self.image_changed.emit(item)
                logger.info(f"Loaded online image: {item.id}")
        except Exception as e:
            logger.error(f"Failed to load online image: {e}")

    def _update_display(self) -> None:
        if not self._current_image:
            self._image_label.clear()
            return
        display_image = apply_transform(self._current_image, self._transform)
        if self._transform.scale != 1.0:
            new_width = int(display_image.width * self._transform.scale)
            new_height = int(display_image.height * self._transform.scale)
            display_image = display_image.resize((new_width, new_height), Image.Resampling.LANCZOS)
        qimage = self._pil_to_qimage(display_image)
        self._image_label.setPixmap(qimage)
        self._image_label.resize(qimage.width(), qimage.height())
        self.transform_changed.emit(self._transform)

    def _pil_to_qimage(self, pil_image: Image.Image) -> QImage:
        if pil_image.mode == "RGB":
            pass
        elif pil_image.mode == "RGBA":
            pass
        elif pil_image.mode == "L":
            pil_image = pil_image.convert("RGB")
        else:
            pil_image = pil_image.convert("RGB")
        data = pil_image.tobytes("raw", pil_image.mode)
        qimage = QImage(data, pil_image.width, pil_image.height, QImage.Format.Format_RGB888 if pil_image.mode == "RGB" else QImage.Format.Format_RGBA8888)
        return qimage

    def zoom_in(self) -> None:
        self._transform.scale = min(self._transform.scale * 1.25, self._max_scale)
        self._update_display()

    def zoom_out(self) -> None:
        self._transform.scale = max(self._transform.scale / 1.25, self._min_scale)
        self._update_display()

    def zoom_fit(self) -> None:
        if not self._current_image:
            return
        viewport_size = self._scroll_area.viewport().size()
        img_width = self._current_image.width
        img_height = self._current_image.height
        scale_x = viewport_size.width() / img_width
        scale_y = viewport_size.height() / img_height
        self._transform.scale = min(scale_x, scale_y) * 0.95
        self._update_display()

    def zoom_actual_size(self) -> None:
        self._transform.scale = 1.0
        self._update_display()

    def rotate_left(self) -> None:
        self._transform.rotation = (self._transform.rotation - 90) % 360
        self._update_display()

    def rotate_right(self) -> None:
        self._transform.rotation = (self._transform.rotation + 90) % 360
        self._update_display()

    def flip_horizontal(self) -> None:
        self._transform.flip_horizontal = not self._transform.flip_horizontal
        self._update_display()

    def flip_vertical(self) -> None:
        self._transform.flip_vertical = not self._transform.flip_vertical
        self._update_display()

    def reset_transform(self) -> None:
        self._transform.reset()
        self._update_display()

    def wheelEvent(self, event: QWheelEvent) -> None:
        if event.modifiers() & Qt.KeyboardModifier.ControlModifier:
            if event.angleDelta().y() > 0:
                self.zoom_in()
            else:
                self.zoom_out()
            event.accept()
        else:
            super().wheelEvent(event)

    def mousePressEvent(self, event: QMouseEvent) -> None:
        if event.button() == Qt.MouseButton.MiddleButton:
            self._is_panning = True
            self._pan_start = (event.pos().x(), event.pos().y())
            self._image_label.setCursor(QCursor(Qt.CursorShape.ClosedHandCursor))
            event.accept()
        else:
            super().mousePressEvent(event)

    def mouseMoveEvent(self, event: QMouseEvent) -> None:
        if self._is_panning and self._pan_start:
            delta_x = event.pos().x() - self._pan_start[0]
            delta_y = event.pos().y() - self._pan_start[1]
            h_bar = self._scroll_area.horizontalScrollBar()
            v_bar = self._scroll_area.verticalScrollBar()
            h_bar.setValue(h_bar.value() - delta_x)
            v_bar.setValue(v_bar.value() - delta_y)
            self._pan_start = (event.pos().x(), event.pos().y())
            event.accept()
        else:
            super().mouseMoveEvent(event)

    def mouseReleaseEvent(self, event: QMouseEvent) -> None:
        if event.button() == Qt.MouseButton.MiddleButton:
            self._is_panning = False
            self._pan_start = None
            self._image_label.setCursor(QCursor(Qt.CursorShape.ArrowCursor))
            event.accept()
        else:
            super().mouseReleaseEvent(event)

    def keyPressEvent(self, event: QKeyEvent) -> None:
        key = event.key()
        if key == Qt.Key.Key_Plus or key == Qt.Key.Key_Equal:
            self.zoom_in()
        elif key == Qt.Key.Key_Minus:
            self.zoom_out()
        elif key == Qt.Key.Key_0:
            self.zoom_actual_size()
        elif key == Qt.Key.Key_F:
            self.zoom_fit()
        elif key == Qt.Key.Key_R:
            self.rotate_right()
        elif key == Qt.Key.Key_L:
            self.rotate_left()
        elif key == Qt.Key.Key_H:
            self.flip_horizontal()
        elif key == Qt.Key.Key_V:
            self.flip_vertical()
        elif key == Qt.Key.Key_Escape:
            self.reset_transform()
        else:
            super().keyPressEvent(event)

    @property
    def current_image(self) -> Image.Image | None:
        return self._current_image

    @property
    def current_item(self) -> ImageItem | None:
        return self._current_item

    @property
    def transform(self) -> ImageTransform:
        return self._transform

    def clear(self) -> None:
        self._current_image = None
        self._current_item = None
        self._transform.reset()
        self._image_label.clear()
        self.image_changed.emit(None)
