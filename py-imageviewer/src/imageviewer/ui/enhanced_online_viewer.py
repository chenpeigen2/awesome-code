"""Enhanced online image search panel with multi-engine support."""

import asyncio
from io import BytesIO
from typing import Any

from PIL import Image
from PyQt6.QtCore import QSize, Qt, QThread, pyqtSignal
from PyQt6.QtGui import QAction, QIcon, QPixmap
from PyQt6.QtWidgets import (
    QCheckBox,
    QComboBox,
    QDialog,
    QDialogButtonBox,
    QFileDialog,
    QFormLayout,
    QGridLayout,
    QHBoxLayout,
    QLabel,
    QLineEdit,
    QListWidget,
    QListWidgetItem,
    QMenu,
    QMessageBox,
    QProgressBar,
    QPushButton,
    QScrollArea,
    QSpinBox,
    QTabWidget,
    QVBoxLayout,
    QWidget,
)

from imageviewer.core.exceptions import APIKeyError
from imageviewer.core.models import ImageItem, SearchResult
from imageviewer.services.base_search_service import (
    ImageColor,
    ImageLayout,
    ImageSize,
    ImageType,
    SafeSearch,
    SearchFilters,
)
from imageviewer.services.bing_service import BingImageService
from imageviewer.services.collection_service import CollectionService
from imageviewer.services.google_service import GoogleImageService
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


class CollectionDialog(QDialog):
    def __init__(self, collection_service: CollectionService, parent: QWidget | None = None):
        super().__init__(parent)
        self.collection_service = collection_service
        self.setWindowTitle("管理收藏夹")
        self.setMinimumSize(400, 300)
        self._setup_ui()
        self._load_collections()

    def _setup_ui(self) -> None:
        layout = QVBoxLayout(self)
        self._collection_list = QListWidget()
        layout.addWidget(self._collection_list)
        btn_layout = QHBoxLayout()
        self._create_btn = QPushButton("新建收藏夹")
        self._create_btn.clicked.connect(self._create_collection)
        btn_layout.addWidget(self._create_btn)
        self._delete_btn = QPushButton("删除")
        self._delete_btn.clicked.connect(self._delete_collection)
        btn_layout.addWidget(self._delete_btn)
        self._export_btn = QPushButton("导出")
        self._export_btn.clicked.connect(self._export_collection)
        btn_layout.addWidget(self._export_btn)
        layout.addLayout(btn_layout)
        buttons = QDialogButtonBox(QDialogButtonBox.StandardButton.Close)
        buttons.rejected.connect(self.reject)
        layout.addWidget(buttons)

    def _load_collections(self) -> None:
        self._collection_list.clear()
        for collection in self.collection_service.get_collections():
            item = QListWidgetItem(f"{collection.name} ({len(collection.images)} 张图片)")
            item.setData(Qt.ItemDataRole.UserRole, collection.name)
            self._collection_list.addItem(item)

    def _create_collection(self) -> None:
        from PyQt6.QtWidgets import QInputDialog
        name, ok = QInputDialog.getText(self, "新建收藏夹", "收藏夹名称:")
        if ok and name:
            self.collection_service.create_collection(name)
            self._load_collections()

    def _delete_collection(self) -> None:
        item = self._collection_list.currentItem()
        if item:
            name = item.data(Qt.ItemDataRole.UserRole)
            reply = QMessageBox.question(self, "确认删除", f"确定要删除收藏夹 '{name}' 吗？")
            if reply == QMessageBox.StandardButton.Yes:
                self.collection_service.delete_collection(name)
                self._load_collections()

    def _export_collection(self) -> None:
        item = self._collection_list.currentItem()
        if not item:
            return
        name = item.data(Qt.ItemDataRole.UserRole)
        file_path, _ = QFileDialog.getSaveFileName(self, "导出收藏夹", f"{name}.zip", "ZIP文件 (*.zip);;JSON文件 (*.json)")
        if file_path:
            fmt = "zip" if file_path.endswith(".zip") else "json"
            self.collection_service.export_collection(name, file_path, fmt)
            QMessageBox.information(self, "导出成功", f"收藏夹已导出到:\n{file_path}")

    def get_selected_collection(self) -> str | None:
        item = self._collection_list.currentItem()
        return item.data(Qt.ItemDataRole.UserRole) if item else None


class EnhancedOnlineViewerPanel(QWidget):
    image_selected = pyqtSignal(object)
    download_requested = pyqtSignal(object)

    def __init__(self, parent: QWidget | None = None):
        super().__init__(parent)
        self._services: dict[str, Any] = {}
        self._current_service_name = "unsplash"
        self._current_result: SearchResult | None = None
        self._current_query = ""
        self._current_filters = SearchFilters()
        self._thumbnail_downloaders: list[ThumbnailDownloader] = []
        self._collection_service = CollectionService()
        self._lazy_load_threshold = 100
        self._setup_services()
        self._setup_ui()
        self._connect_signals()

    def _setup_services(self) -> None:
        settings = get_settings()
        if settings.unsplash_api_key:
            self._services["unsplash"] = UnsplashService(settings.unsplash_api_key)
        if settings.google_api_key and settings.google_search_engine_id:
            self._services["google"] = GoogleImageService(settings.google_api_key, settings.google_search_engine_id)
        if settings.bing_api_key:
            self._services["bing"] = BingImageService(settings.bing_api_key)

    def _setup_ui(self) -> None:
        layout = QHBoxLayout(self)
        layout.setContentsMargins(0, 0, 0, 0)
        sidebar = QWidget()
        sidebar.setFixedWidth(280)
        sidebar_layout = QVBoxLayout(sidebar)
        engine_group = QWidget()
        engine_layout = QVBoxLayout(engine_group)
        engine_layout.addWidget(QLabel("搜索引擎:"))
        self._engine_combo = QComboBox()
        engine_layout.addWidget(self._engine_combo)
        sidebar_layout.addWidget(engine_group)
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
        self._random_btn.setVisible(self._current_service_name == "unsplash")
        search_row.addWidget(self._random_btn)
        search_layout.addLayout(search_row)
        sidebar_layout.addWidget(search_group)
        filter_tabs = QTabWidget()
        filter_tabs.setMaximumHeight(250)
        basic_filter = QWidget()
        basic_layout = QFormLayout(basic_filter)
        self._size_combo = QComboBox()
        self._size_combo.addItems(["任意大小", "大图", "中图", "小图", "图标"])
        basic_layout.addRow("大小:", self._size_combo)
        self._color_combo = QComboBox()
        self._color_combo.addItems(["任意颜色", "彩色", "黑白", "红色", "橙色", "黄色", "绿色", "蓝色", "紫色", "粉色", "白色", "黑色"])
        basic_layout.addRow("颜色:", self._color_combo)
        self._type_combo = QComboBox()
        self._type_combo.addItems(["任意类型", "照片", "剪贴画", "线条画", "动图"])
        basic_layout.addRow("类型:", self._type_combo)
        self._layout_combo = QComboBox()
        self._layout_combo.addItems(["任意方向", "横向", "纵向", "正方形"])
        basic_layout.addRow("方向:", self._layout_combo)
        filter_tabs.addTab(basic_filter, "基本")
        adv_filter = QWidget()
        adv_layout = QFormLayout(adv_filter)
        self._safe_combo = QComboBox()
        self._safe_combo.addItems(["关闭", "中等", "严格"])
        self._safe_combo.setCurrentIndex(1)
        adv_layout.addRow("安全搜索:", self._safe_combo)
        self._domain_edit = QLineEdit()
        self._domain_edit.setPlaceholderText("例如: wikipedia.org")
        adv_layout.addRow("限定域名:", self._domain_edit)
        self._per_page_spin = QSpinBox()
        self._per_page_spin.setRange(10, 100)
        self._per_page_spin.setValue(30)
        adv_layout.addRow("每页结果:", self._per_page_spin)
        filter_tabs.addTab(adv_filter, "高级")
        sidebar_layout.addWidget(filter_tabs)
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
        self._progress_bar = QProgressBar()
        self._progress_bar.setVisible(False)
        sidebar_layout.addWidget(self._progress_bar)
        sidebar_layout.addStretch()
        action_group = QWidget()
        action_layout = QVBoxLayout(action_group)
        self._collection_btn = QPushButton("📁 收藏夹管理")
        self._collection_btn.setFixedHeight(36)
        action_layout.addWidget(self._collection_btn)
        self._add_to_collection_btn = QPushButton("➕ 添加到收藏夹")
        self._add_to_collection_btn.setFixedHeight(36)
        self._add_to_collection_btn.setEnabled(False)
        action_layout.addWidget(self._add_to_collection_btn)
        sidebar_layout.addWidget(action_group)
        self._api_info = QLabel()
        self._api_info.setWordWrap(True)
        self._api_info.setStyleSheet("color: #888888; font-size: 10px;")
        self._update_api_info()
        sidebar_layout.addWidget(self._api_info)
        layout.addWidget(sidebar)
        results_widget = QWidget()
        results_layout = QVBoxLayout(results_widget)
        results_layout.setContentsMargins(0, 0, 0, 0)
        self._sort_combo = QComboBox()
        self._sort_combo.addItems(["相关性", "最新", "大小"])
        self._sort_combo.setFixedHeight(32)
        sort_row = QHBoxLayout()
        sort_row.addWidget(QLabel("排序:"))
        sort_row.addWidget(self._sort_combo)
        sort_row.addStretch()
        results_layout.addLayout(sort_row)
        self._image_list = QListWidget()
        self._image_list.setViewMode(QListWidget.ViewMode.IconMode)
        self._image_list.setIconSize(QSize(200, 200))
        self._image_list.setResizeMode(QListWidget.ResizeMode.Adjust)
        self._image_list.setSpacing(10)
        self._image_list.setSelectionMode(QListWidget.SelectionMode.ExtendedSelection)
        self._image_list.setContextMenuPolicy(Qt.ContextMenuPolicy.CustomContextMenu)
        self._image_list.verticalScrollBar().valueChanged.connect(self._on_scroll)
        results_layout.addWidget(self._image_list)
        layout.addWidget(results_widget, 1)
        self._populate_engines()

    def _populate_engines(self) -> None:
        self._engine_combo.clear()
        engine_names = {
            "unsplash": "Unsplash",
            "google": "Google Images",
            "bing": "Bing Images",
        }
        for key, name in engine_names.items():
            if key in self._services:
                self._engine_combo.addItem(name, key)
        if self._engine_combo.count() == 0:
            self._engine_combo.addItem("请配置API密钥 (Ctrl+,)", "none")
            self._status_label.setText("⚠ 请先配置API密钥：设置 → 配置API密钥")
        else:
            self._current_service_name = self._engine_combo.currentData()

    def _connect_signals(self) -> None:
        self._search_btn.clicked.connect(self._do_search)
        self._search_edit.returnPressed.connect(self._do_search)
        self._random_btn.clicked.connect(self._do_random)
        self._prev_btn.clicked.connect(self._prev_page)
        self._next_btn.clicked.connect(self._next_page)
        self._image_list.itemClicked.connect(self._on_image_clicked)
        self._image_list.itemDoubleClicked.connect(self._on_image_double_clicked)
        self._image_list.customContextMenuRequested.connect(self._show_context_menu)
        self._engine_combo.currentIndexChanged.connect(self._on_engine_changed)
        self._collection_btn.clicked.connect(self._show_collection_dialog)
        self._add_to_collection_btn.clicked.connect(self._add_selected_to_collection)
        self._image_list.itemSelectionChanged.connect(self._on_selection_changed)

    def _update_api_info(self) -> None:
        settings = get_settings()
        configured = []
        if settings.unsplash_api_key:
            configured.append("Unsplash")
        if settings.google_api_key and settings.google_search_engine_id:
            configured.append("Google")
        if settings.bing_api_key:
            configured.append("Bing")
        if configured:
            self._api_info.setText(f"✓ 已配置: {', '.join(configured)}")
            self._api_info.setStyleSheet("color: #4CAF50; font-size: 10px;")
        else:
            self._api_info.setText('⚠ <a href="settings">点击配置API密钥</a> (或按 Ctrl+,)')
            self._api_info.setStyleSheet("color: #FF9800; font-size: 10px;")
            self._api_info.setOpenExternalLinks(False)
            self._api_info.linkActivated.connect(self._on_api_info_link)

    def _on_engine_changed(self) -> None:
        self._current_service_name = self._engine_combo.currentData()
        self._random_btn.setVisible(self._current_service_name == "unsplash")

    def _get_filters(self) -> SearchFilters:
        size_map = [ImageSize.ANY, ImageSize.LARGE, ImageSize.MEDIUM, ImageSize.SMALL, ImageSize.ICON]
        color_map = [ImageColor.ANY, ImageColor.COLOR, ImageColor.BLACK_WHITE, ImageColor.RED, ImageColor.ORANGE,
                     ImageColor.YELLOW, ImageColor.GREEN, ImageColor.BLUE, ImageColor.PURPLE, ImageColor.PINK,
                     ImageColor.WHITE, ImageColor.BLACK]
        type_map = [ImageType.ANY, ImageType.PHOTO, ImageType.CLIPART, ImageType.LINEART, ImageType.ANIMATED]
        layout_map = [ImageLayout.ANY, ImageLayout.HORIZONTAL, ImageLayout.VERTICAL, ImageLayout.SQUARE]
        safe_map = [SafeSearch.OFF, SafeSearch.MODERATE, SafeSearch.STRICT]
        return SearchFilters(
            size=size_map[self._size_combo.currentIndex()],
            color=color_map[self._color_combo.currentIndex()],
            image_type=type_map[self._type_combo.currentIndex()],
            layout=layout_map[self._layout_combo.currentIndex()],
            safe_search=safe_map[self._safe_combo.currentIndex()],
            domain=self._domain_edit.text().strip() or None,
        )

    def _do_search(self) -> None:
        query = self._search_edit.text().strip()
        if not query:
            return
        self._current_query = query
        self._current_filters = self._get_filters()
        self._search_page(1)

    def _do_random(self) -> None:
        if self._current_service_name == "unsplash":
            asyncio.create_task(self._fetch_random())

    async def _fetch_random(self) -> None:
        service = self._services.get("unsplash")
        if not service:
            return
        self._status_label.setText("正在获取随机图片...")
        self._search_btn.setEnabled(False)
        try:
            images = await service.get_random_photos(12)
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

    def _search_page(self, page: int) -> None:
        asyncio.create_task(self._execute_search(page))

    async def _execute_search(self, page: int) -> None:
        service = self._services.get(self._current_service_name)
        if not service:
            QMessageBox.warning(self, "错误", "请先配置搜索引擎API密钥")
            return
        per_page = self._per_page_spin.value()
        self._status_label.setText(f"正在搜索 '{self._current_query}'...")
        self._search_btn.setEnabled(False)
        self._progress_bar.setVisible(True)
        self._progress_bar.setRange(0, 0)
        try:
            result = await service.search(
                self._current_query,
                page=page,
                per_page=per_page,
                filters=self._current_filters,
            )
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
            self._progress_bar.setVisible(False)

    def _display_images(self, images: list[ImageItem]) -> None:
        self._image_list.clear()
        for image in images:
            item = QListWidgetItem(image.title or image.id)
            item.setData(Qt.ItemDataRole.UserRole, image)
            tooltip = f"{image.title or image.id}\n作者: {image.author}\n{image.width}x{image.height}"
            if image.url:
                tooltip += f"\n来源: {image.url[:50]}..."
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
            self._search_page(self._current_result.current_page - 1)

    def _next_page(self) -> None:
        if self._current_result and self._current_result.has_next:
            self._search_page(self._current_result.current_page + 1)

    def _on_scroll(self, value: int) -> None:
        scrollbar = self._image_list.verticalScrollBar()
        if scrollbar and value >= scrollbar.maximum() - self._lazy_load_threshold:
            if self._current_result and self._current_result.has_next:
                self._next_page()

    def _on_image_clicked(self, item: QListWidgetItem) -> None:
        image = item.data(Qt.ItemDataRole.UserRole)
        self.image_selected.emit(image)

    def _on_image_double_clicked(self, item: QListWidgetItem) -> None:
        image = item.data(Qt.ItemDataRole.UserRole)
        self.image_selected.emit(image)

    def _on_selection_changed(self) -> None:
        has_selection = len(self._image_list.selectedItems()) > 0
        self._add_to_collection_btn.setEnabled(has_selection)

    def _on_api_info_link(self, link: str) -> None:
        if link == "settings":
            from imageviewer.ui.settings_dialog import SettingsDialog
            dialog = SettingsDialog(self)
            if dialog.exec():
                self._setup_services()
                self._populate_engines()
                self._update_api_info()

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
        menu.addSeparator()
        add_action = QAction("添加到收藏夹", self)
        add_action.triggered.connect(lambda: self._add_to_collection([image]))
        menu.addAction(add_action)
        copy_url_action = QAction("复制图片URL", self)
        copy_url_action.triggered.connect(lambda: self._copy_image_url(image))
        menu.addAction(copy_url_action)
        menu.exec(self._image_list.mapToGlobal(pos))

    def _copy_image_url(self, image: ImageItem) -> None:
        from PyQt6.QtWidgets import QApplication
        if image.url:
            QApplication.clipboard().setText(image.url)
            self._status_label.setText("URL已复制到剪贴板")

    def _show_collection_dialog(self) -> None:
        dialog = CollectionDialog(self._collection_service, self)
        dialog.exec()

    def _add_selected_to_collection(self) -> None:
        selected = self._image_list.selectedItems()
        images = [item.data(Qt.ItemDataRole.UserRole) for item in selected]
        self._add_to_collection(images)

    def _add_to_collection(self, images: list[ImageItem]) -> None:
        collections = self._collection_service.get_collections()
        if not collections:
            reply = QMessageBox.question(
                self, "无收藏夹",
                "还没有收藏夹，是否创建一个新的收藏夹？",
                QMessageBox.StandardButton.Yes | QMessageBox.StandardButton.No,
            )
            if reply == QMessageBox.StandardButton.Yes:
                from PyQt6.QtWidgets import QInputDialog
                name, ok = QInputDialog.getText(self, "新建收藏夹", "收藏夹名称:")
                if ok and name:
                    self._collection_service.create_collection(name)
                    collections = self._collection_service.get_collections()
                else:
                    return
            else:
                return
        collection_names = [c.name for c in collections]
        from PyQt6.QtWidgets import QInputDialog
        name, ok = QInputDialog.getItem(self, "选择收藏夹", "选择要添加到的收藏夹:", collection_names, 0, False)
        if ok and name:
            for image in images:
                self._collection_service.add_image_to_collection(name, image)
            QMessageBox.information(self, "成功", f"已将 {len(images)} 张图片添加到收藏夹 '{name}'")

    def get_selected_images(self) -> list[ImageItem]:
        images = []
        for item in self._image_list.selectedItems():
            image = item.data(Qt.ItemDataRole.UserRole)
            if image:
                images.append(image)
        return images
