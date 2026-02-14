"""Online image search panel using Unsplash API."""

import asyncio
from io import BytesIO

from PIL import Image
from PyQt6.QtCore import QSize, Qt, QThread, pyqtSignal
from PyQt6.QtGui import QAction, QIcon, QPixmap
from PyQt6.QtWidgets import (
    QComboBox,
    QHBoxLayout,
    QLabel,
    QLineEdit,
    QListWidget,
    QListWidgetItem,
    QMenu,
    QMessageBox,
    QPushButton,
    QVBoxLayout,
    QWidget,
)

from imageviewer.core.exceptions import APIKeyError
from imageviewer.core.models import ImageItem, SearchQuery, SearchResult
from imageviewer.services.unsplash_service import UnsplashService
from imageviewer.utils.config import get_settings
from imageviewer.utils.logger import get_logger

logger = get_logger()


class ThumbnailDownloader(QThread):
    thumbnail_downloaded = pyqtSignal(str, object)

    def __init__(self, url: str, size: tuple[int, int] = (200, 200)):
        super().__init__()
        self.url = url
        self.size = size

    def run(self) -> None:
        import httpx
        try:
            with httpx.Client(timeout=15.0) as client:
                response = client.get(self.url)
                response.raise_for_status()
                img = Image.open(BytesIO(response.content))
                img.thumbnail(self.size, Image.Resampling.LANCZOS)
                self.thumbnail_downloaded.emit(self.url, img)
        except Exception as e:
            logger.error(f"Failed to download thumbnail: {e}")


class OnlineViewerPanel(QWidget):
    image_selected = pyqtSignal(object)
    download_requested = pyqtSignal(object)

    def __init__(self, parent: QWidget | None = None):
        super().__init__(parent)
        self._service = UnsplashService()
        self._current_result: SearchResult | None = None
        self._current_query = ""
        self._thumbnail_downloaders: list[ThumbnailDownloader] = []
        self._setup_ui()
        self._connect_signals()

    def _setup_ui(self) -> None:
        layout = QHBoxLayout(self)
        layout.setContentsMargins(0, 0, 0, 0)
        sidebar = QWidget()
        sidebar.setFixedWidth(250)
        sidebar_layout = QVBoxLayout(sidebar)
        search_group = QWidget()
        search_layout = QVBoxLayout(search_group)
        self._search_edit = QLineEdit()
        self._search_edit.setPlaceholderText("搜索图片...")
        self._search_edit.setFixedHeight(36)
        search_layout.addWidget(self._search_edit)
        search_row = QHBoxLayout()
        self._search_btn = QPushButton("搜索")
        self._search_btn.setFixedHeight(36)
        search_row.addWidget(self._search_btn)
        self._random_btn = QPushButton("随机")
        self._random_btn.setFixedHeight(36)
        search_row.addWidget(self._random_btn)
        search_layout.addLayout(search_row)
        filter_row = QHBoxLayout()
        self._color_combo = QComboBox()
        self._color_combo.addItems(["所有颜色", "黑色", "白色", "黄色", "橙色", "红色", "紫色", "品红", "绿色", "青色", "蓝色"])
        self._color_combo.setFixedHeight(32)
        filter_row.addWidget(QLabel("颜色:"))
        filter_row.addWidget(self._color_combo)
        search_layout.addLayout(filter_row)
        orient_row = QHBoxLayout()
        self._orient_combo = QComboBox()
        self._orient_combo.addItems(["所有方向", "横向", "纵向", "正方形"])
        self._orient_combo.setFixedHeight(32)
        orient_row.addWidget(QLabel("方向:"))
        orient_row.addWidget(self._orient_combo)
        search_layout.addLayout(orient_row)
        sidebar_layout.addWidget(search_group)
        nav_group = QWidget()
        nav_layout = QHBoxLayout(nav_group)
        self._prev_btn = QPushButton("← 上一页")
        self._prev_btn.setEnabled(False)
        self._prev_btn.setFixedHeight(32)
        nav_layout.addWidget(self._prev_btn)
        self._page_label = QLabel("第 1 页")
        self._page_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        nav_layout.addWidget(self._page_label)
        self._next_btn = QPushButton("下一页 →")
        self._next_btn.setEnabled(False)
        self._next_btn.setFixedHeight(32)
        nav_layout.addWidget(self._next_btn)
        sidebar_layout.addWidget(nav_group)
        self._status_label = QLabel("输入关键词搜索图片")
        self._status_label.setWordWrap(True)
        sidebar_layout.addWidget(self._status_label)
        sidebar_layout.addStretch()
        self._api_info = QLabel()
        self._api_info.setWordWrap(True)
        self._api_info.setStyleSheet("color: #888888; font-size: 10px;")
        self._update_api_info()
        sidebar_layout.addWidget(self._api_info)
        layout.addWidget(sidebar)
        self._image_list = QListWidget()
        self._image_list.setViewMode(QListWidget.ViewMode.IconMode)
        self._image_list.setIconSize(QSize(200, 200))
        self._image_list.setResizeMode(QListWidget.ResizeMode.Adjust)
        self._image_list.setSpacing(10)
        self._image_list.setContextMenuPolicy(Qt.ContextMenuPolicy.CustomContextMenu)
        layout.addWidget(self._image_list, 1)

    def _connect_signals(self) -> None:
        self._search_btn.clicked.connect(self._do_search)
        self._search_edit.returnPressed.connect(self._do_search)
        self._random_btn.clicked.connect(self._do_random)
        self._prev_btn.clicked.connect(self._prev_page)
        self._next_btn.clicked.connect(self._next_page)
        self._image_list.itemClicked.connect(self._on_image_clicked)
        self._image_list.itemDoubleClicked.connect(self._on_image_double_clicked)
        self._image_list.customContextMenuRequested.connect(self._show_context_menu)

    def _update_api_info(self) -> None:
        settings = get_settings()
        if settings.unsplash_api_key:
            self._api_info.setText("✓ Unsplash API 已配置")
            self._api_info.setStyleSheet("color: #4CAF50; font-size: 10px;")
        else:
            self._api_info.setText("⚠ 请设置 IMAGEVIEWER_UNSPLASH_API_KEY 环境变量")
            self._api_info.setStyleSheet("color: #FF9800; font-size: 10px;")

    def _get_color_value(self) -> str | None:
        colors = [None, "black", "white", "yellow", "orange", "red", "purple", "magenta", "green", "teal", "blue"]
        return colors[self._color_combo.currentIndex()]

    def _get_orientation_value(self) -> str | None:
        orientations = [None, "landscape", "portrait", "squarish"]
        return orientations[self._orient_combo.currentIndex()]

    def _do_search(self) -> None:
        query = self._search_edit.text().strip()
        if not query:
            return
        self._current_query = query
        self._search(1)

    def _do_random(self) -> None:
        asyncio.create_task(self._fetch_random())

    async def _fetch_random(self) -> None:
        self._status_label.setText("正在获取随机图片...")
        self._search_btn.setEnabled(False)
        try:
            images = await self._service.get_random_photos(12)
            self._display_images(images)
            self._status_label.setText(f"获取到 {len(images)} 张随机图片")
        except APIKeyError as e:
            QMessageBox.warning(self, "API密钥错误", str(e))
            self._status_label.setText("API密钥未配置")
        except Exception as e:
            logger.error(f"Failed to fetch random photos: {e}")
            self._status_label.setText(f"错误: {e}")
        finally:
            self._search_btn.setEnabled(True)

    def _search(self, page: int) -> None:
        asyncio.create_task(self._execute_search(page))

    async def _execute_search(self, page: int) -> None:
        query = SearchQuery(
            query=self._current_query,
            page=page,
            per_page=30,
            color=self._get_color_value(),
            orientation=self._get_orientation_value(),
        )
        self._status_label.setText(f"正在搜索 '{self._current_query}'...")
        self._search_btn.setEnabled(False)
        try:
            result = await self._service.search_photos(query)
            self._current_result = result
            self._display_images(result.images)
            self._update_navigation()
            self._status_label.setText(f"找到 {result.total} 张图片，当前第 {result.current_page} 页")
        except APIKeyError as e:
            QMessageBox.warning(self, "API密钥错误", str(e))
            self._status_label.setText("API密钥未配置")
        except Exception as e:
            logger.error(f"Search failed: {e}")
            self._status_label.setText(f"搜索失败: {e}")
        finally:
            self._search_btn.setEnabled(True)

    def _display_images(self, images: list[ImageItem]) -> None:
        self._image_list.clear()
        for image in images:
            item = QListWidgetItem(image.title or image.id)
            item.setData(Qt.ItemDataRole.UserRole, image)
            tooltip = f"{image.title or image.id}\n作者: {image.author}\n{image.width}x{image.height}"
            item.setToolTip(tooltip)
            self._image_list.addItem(item)
            if image.thumbnail_url:
                self._download_thumbnail_async(image.thumbnail_url, item)

    def _download_thumbnail_async(self, url: str, item: QListWidgetItem) -> None:
        downloader = ThumbnailDownloader(url, (200, 200))
        downloader.thumbnail_downloaded.connect(lambda u, t: self._set_thumbnail(item, t))
        downloader.finished.connect(lambda: self._cleanup_downloader(downloader))
        self._thumbnail_downloaders.append(downloader)
        downloader.start()

    def _set_thumbnail(self, item: QListWidgetItem, thumbnail: Image.Image) -> None:
        if thumbnail.mode != "RGB":
            thumbnail = thumbnail.convert("RGB")
        data = thumbnail.tobytes("raw", "RGB")
        from PyQt6.QtGui import QImage
        qimage = QImage(data, thumbnail.width, thumbnail.height, QImage.Format.Format_RGB888)
        pixmap = QPixmap.fromImage(qimage)
        item.setIcon(QIcon(pixmap))

    def _cleanup_downloader(self, downloader: ThumbnailDownloader) -> None:
        if downloader in self._thumbnail_downloaders:
            self._thumbnail_downloaders.remove(downloader)

    def _update_navigation(self) -> None:
        if self._current_result:
            self._prev_btn.setEnabled(self._current_result.has_previous)
            self._next_btn.setEnabled(self._current_result.has_next)
            self._page_label.setText(f"第 {self._current_result.current_page} / {self._current_result.total_pages} 页")
        else:
            self._prev_btn.setEnabled(False)
            self._next_btn.setEnabled(False)
            self._page_label.setText("第 1 页")

    def _prev_page(self) -> None:
        if self._current_result and self._current_result.has_previous:
            self._search(self._current_result.current_page - 1)

    def _next_page(self) -> None:
        if self._current_result and self._current_result.has_next:
            self._search(self._current_result.current_page + 1)

    def _on_image_clicked(self, item: QListWidgetItem) -> None:
        image = item.data(Qt.ItemDataRole.UserRole)
        self.image_selected.emit(image)

    def _on_image_double_clicked(self, item: QListWidgetItem) -> None:
        image = item.data(Qt.ItemDataRole.UserRole)
        self.image_selected.emit(image)

    def _show_context_menu(self, pos) -> None:
        item = self._image_list.itemAt(pos)
        if not item:
            return
        image = item.data(Qt.ItemDataRole.UserRole)
        menu = QMenu(self)
        view_action = QAction("查看", self)
        view_action.triggered.connect(lambda: self.image_selected.emit(image))
        menu.addAction(view_action)
        download_action = QAction("下载", self)
        download_action.triggered.connect(lambda: self.download_requested.emit(image))
        menu.addAction(download_action)
        menu.exec(self._image_list.mapToGlobal(pos))

    def get_selected_images(self) -> list[ImageItem]:
        images = []
        for item in self._image_list.selectedItems():
            image = item.data(Qt.ItemDataRole.UserRole)
            if image:
                images.append(image)
        return images
