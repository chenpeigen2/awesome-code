"""Local file browser panel for browsing local images."""

from pathlib import Path

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
    QPushButton,
    QSplitter,
    QToolBar,
    QTreeWidget,
    QTreeWidgetItem,
    QVBoxLayout,
    QWidget,
)

from imageviewer.core.models import ImageItem, SortOrder
from imageviewer.services.local_service import LocalFileService
from imageviewer.utils.image_utils import load_thumbnail
from imageviewer.utils.logger import get_logger

logger = get_logger()


class ThumbnailLoader(QThread):
    thumbnail_loaded = pyqtSignal(str, object)

    def __init__(self, path: Path, size: tuple[int, int] = (200, 200)):
        super().__init__()
        self.path = path
        self.size = size

    def run(self) -> None:
        thumbnail = load_thumbnail(self.path, self.size)
        if thumbnail:
            self.thumbnail_loaded.emit(str(self.path), thumbnail)


class LocalViewerPanel(QWidget):
    image_selected = pyqtSignal(object)
    directory_changed = pyqtSignal(object)

    def __init__(self, parent: QWidget | None = None):
        super().__init__(parent)
        self._service = LocalFileService()
        self._current_images: list[ImageItem] = []
        self._thumbnail_loaders: list[ThumbnailLoader] = []
        self._sort_order = SortOrder.NAME_ASC
        self._setup_ui()
        self._connect_signals()
        self._load_initial_directory()

    def _setup_ui(self) -> None:
        layout = QVBoxLayout(self)
        layout.setContentsMargins(0, 0, 0, 0)
        toolbar = QToolBar()
        toolbar.setMovable(False)
        self._back_btn = QPushButton("← 上级")
        self._back_btn.setFixedHeight(32)
        toolbar.addWidget(self._back_btn)
        self._path_edit = QLineEdit()
        self._path_edit.setPlaceholderText("输入路径或选择目录...")
        self._path_edit.setFixedHeight(32)
        toolbar.addWidget(self._path_edit)
        self._go_btn = QPushButton("转到")
        self._go_btn.setFixedHeight(32)
        toolbar.addWidget(self._go_btn)
        self._sort_combo = QComboBox()
        self._sort_combo.addItems(["名称升序", "名称降序", "日期升序", "日期降序", "大小升序", "大小降序"])
        self._sort_combo.setFixedHeight(32)
        toolbar.addWidget(QLabel("排序:"))
        toolbar.addWidget(self._sort_combo)
        layout.addWidget(toolbar)
        splitter = QSplitter(Qt.Orientation.Horizontal)
        self._tree_widget = QTreeWidget()
        self._tree_widget.setHeaderLabel("目录")
        self._tree_widget.setMinimumWidth(150)
        splitter.addWidget(self._tree_widget)
        self._image_list = QListWidget()
        self._image_list.setViewMode(QListWidget.ViewMode.IconMode)
        self._image_list.setIconSize(QSize(*self._service.settings.thumbnail_size))
        self._image_list.setResizeMode(QListWidget.ResizeMode.Adjust)
        self._image_list.setSpacing(10)
        self._image_list.setContextMenuPolicy(Qt.ContextMenuPolicy.CustomContextMenu)
        splitter.addWidget(self._image_list)
        splitter.setSizes([200, 600])
        layout.addWidget(splitter)
        self._status_bar = QHBoxLayout()
        self._status_label = QLabel("就绪")
        self._status_bar.addWidget(self._status_label)
        self._status_bar.addStretch()
        layout.addLayout(self._status_bar)

    def _connect_signals(self) -> None:
        self._back_btn.clicked.connect(self._go_parent)
        self._go_btn.clicked.connect(self._go_to_path)
        self._path_edit.returnPressed.connect(self._go_to_path)
        self._tree_widget.itemClicked.connect(self._on_tree_item_clicked)
        self._image_list.itemClicked.connect(self._on_image_item_clicked)
        self._image_list.itemDoubleClicked.connect(self._on_image_double_clicked)
        self._image_list.customContextMenuRequested.connect(self._show_context_menu)
        self._sort_combo.currentIndexChanged.connect(self._on_sort_changed)

    def _load_initial_directory(self) -> None:
        self._populate_drives()
        recent_dirs = self._service.get_recent_directories()
        if recent_dirs:
            self._set_directory(recent_dirs[0])

    def _populate_drives(self) -> None:
        self._tree_widget.clear()
        drives = self._service.get_drives()
        for drive in drives:
            item = QTreeWidgetItem([str(drive)])
            item.setData(0, Qt.ItemDataRole.UserRole, drive)
            item.setChildIndicatorPolicy(QTreeWidgetItem.ChildIndicatorPolicy.ShowIndicator)
            self._tree_widget.addTopLevelItem(item)

    def _set_directory(self, path: Path) -> None:
        try:
            self._service.set_directory(path)
            self._path_edit.setText(str(path))
            self._update_tree_selection(path)
            self._load_images()
            self.directory_changed.emit(path)
            self._status_label.setText(f"当前目录: {path}")
        except Exception as e:
            logger.error(f"Failed to set directory: {e}")
            self._status_label.setText(f"错误: {e}")

    def _update_tree_selection(self, path: Path) -> None:
        for i in range(self._tree_widget.topLevelItemCount()):
            item = self._tree_widget.topLevelItem(i)
            if self._expand_to_path(item, path):
                break

    def _expand_to_path(self, parent_item: QTreeWidgetItem, target_path: Path) -> bool:
        item_path = parent_item.data(0, Qt.ItemDataRole.UserRole)
        if item_path == target_path:
            self._tree_widget.setCurrentItem(parent_item)
            return True
        if target_path.is_relative_to(item_path):
            parent_item.setExpanded(True)
            self._populate_subdirectories(parent_item)
            for i in range(parent_item.childCount()):
                child = parent_item.child(i)
                if self._expand_to_path(child, target_path):
                    return True
        return False

    def _populate_subdirectories(self, parent_item: QTreeWidgetItem) -> None:
        parent_path = parent_item.data(0, Qt.ItemDataRole.UserRole)
        if parent_item.childCount() > 0:
            return
        try:
            for subdir in sorted(parent_path.iterdir()):
                if subdir.is_dir() and not subdir.name.startswith("."):
                    item = QTreeWidgetItem([subdir.name])
                    item.setData(0, Qt.ItemDataRole.UserRole, subdir)
                    item.setChildIndicatorPolicy(QTreeWidgetItem.ChildIndicatorPolicy.ShowIndicator)
                    parent_item.addChild(item)
        except PermissionError:
            pass

    def _load_images(self) -> None:
        self._image_list.clear()
        self._current_images = self._service.scan_images(sort_order=self._sort_order)
        self._status_label.setText(f"找到 {len(self._current_images)} 张图片")
        for image in self._current_images:
            item = QListWidgetItem(image.title)
            item.setData(Qt.ItemDataRole.UserRole, image)
            item.setToolTip(f"{image.title}\n{image.width}x{image.height}\n{image.file_size / 1024:.1f} KB")
            self._image_list.addItem(item)
            if image.file_path:
                self._load_thumbnail_async(image.file_path, item)

    def _load_thumbnail_async(self, path: Path, list_item: QListWidgetItem) -> None:
        loader = ThumbnailLoader(path, self._service.settings.thumbnail_size)
        loader.thumbnail_loaded.connect(lambda p, t: self._set_thumbnail(list_item, t))
        loader.finished.connect(lambda: self._cleanup_loader(loader))
        self._thumbnail_loaders.append(loader)
        loader.start()

    def _set_thumbnail(self, item: QListWidgetItem, thumbnail: Image.Image) -> None:
        pixmap = self._pil_to_qpixmap(thumbnail)
        item.setIcon(QIcon(pixmap))

    def _pil_to_qpixmap(self, pil_image: Image.Image) -> QPixmap:
        if pil_image.mode != "RGB":
            pil_image = pil_image.convert("RGB")
        data = pil_image.tobytes("raw", "RGB")
        from PyQt6.QtGui import QImage
        qimage = QImage(data, pil_image.width, pil_image.height, QImage.Format.Format_RGB888)
        return QPixmap.fromImage(qimage)

    def _cleanup_loader(self, loader: ThumbnailLoader) -> None:
        if loader in self._thumbnail_loaders:
            self._thumbnail_loaders.remove(loader)

    def _on_tree_item_clicked(self, item: QTreeWidgetItem, column: int) -> None:
        path = item.data(0, Qt.ItemDataRole.UserRole)
        if path and path.is_dir():
            self._set_directory(path)
            self._populate_subdirectories(item)

    def _on_image_item_clicked(self, item: QListWidgetItem) -> None:
        image = item.data(Qt.ItemDataRole.UserRole)
        self.image_selected.emit(image)

    def _on_image_double_clicked(self, item: QListWidgetItem) -> None:
        image = item.data(Qt.ItemDataRole.UserRole)
        self.image_selected.emit(image)

    def _go_parent(self) -> None:
        if self._service.current_directory:
            parent = self._service.get_parent_directory()
            if parent:
                self._set_directory(parent)

    def _go_to_path(self) -> None:
        path_str = self._path_edit.text().strip()
        if path_str:
            path = Path(path_str)
            if path.exists() and path.is_dir():
                self._set_directory(path)

    def _on_sort_changed(self, index: int) -> None:
        sort_orders = [
            SortOrder.NAME_ASC,
            SortOrder.NAME_DESC,
            SortOrder.DATE_ASC,
            SortOrder.DATE_DESC,
            SortOrder.SIZE_ASC,
            SortOrder.SIZE_DESC,
        ]
        self._sort_order = sort_orders[index]
        self._load_images()

    def _show_context_menu(self, pos) -> None:
        item = self._image_list.itemAt(pos)
        if not item:
            return
        menu = QMenu(self)
        open_action = QAction("打开", self)
        open_action.triggered.connect(lambda: self._on_image_item_clicked(item))
        menu.addAction(open_action)
        menu.exec(self._image_list.mapToGlobal(pos))

    def refresh(self) -> None:
        if self._service.current_directory:
            self._load_images()
