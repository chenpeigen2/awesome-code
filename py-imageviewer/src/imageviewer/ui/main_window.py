"""Main application window."""

import asyncio
from pathlib import Path

from PyQt6.QtCore import Qt
from PyQt6.QtGui import QAction, QKeySequence
from PyQt6.QtWidgets import (
    QFileDialog,
    QHBoxLayout,
    QLabel,
    QMainWindow,
    QMessageBox,
    QProgressBar,
    QPushButton,
    QSplitter,
    QStackedWidget,
    QStatusBar,
    QToolBar,
    QWidget,
)

from imageviewer.core.models import ImageItem, ViewMode
from imageviewer.services.download_service import DownloadService
from imageviewer.ui.image_viewer import ImageViewerWidget
from imageviewer.ui.local_viewer import LocalViewerPanel
from imageviewer.ui.enhanced_online_viewer import EnhancedOnlineViewerPanel
from imageviewer.utils.config import get_settings
from imageviewer.utils.logger import get_logger

logger = get_logger()


class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self._view_mode = ViewMode.LOCAL
        self._download_service = DownloadService()
        self._current_image: ImageItem | None = None
        self._setup_ui()
        self._setup_menu()
        self._setup_toolbar()
        self._setup_statusbar()
        self._connect_signals()
        self._load_settings()

    def _setup_ui(self) -> None:
        self.setWindowTitle("图像查看器 - ImageViewer")
        self.setMinimumSize(1200, 800)
        central_widget = QWidget()
        self.setCentralWidget(central_widget)
        main_layout = QHBoxLayout(central_widget)
        main_layout.setContentsMargins(0, 0, 0, 0)
        splitter = QSplitter(Qt.Orientation.Horizontal)
        self._browser_stack = QStackedWidget()
        self._local_panel = LocalViewerPanel()
        self._online_panel = EnhancedOnlineViewerPanel()
        self._browser_stack.addWidget(self._local_panel)
        self._browser_stack.addWidget(self._online_panel)
        splitter.addWidget(self._browser_stack)
        self._image_viewer = ImageViewerWidget()
        splitter.addWidget(self._image_viewer)
        splitter.setSizes([350, 850])
        main_layout.addWidget(splitter)

    def _setup_menu(self) -> None:
        menubar = self.menuBar()
        file_menu = menubar.addMenu("文件(&F)")
        open_action = QAction("打开文件(&O)", self)
        open_action.setShortcut(QKeySequence.StandardKey.Open)
        open_action.triggered.connect(self._open_file)
        file_menu.addAction(open_action)
        open_dir_action = QAction("打开目录(&D)", self)
        open_dir_action.triggered.connect(self._open_directory)
        file_menu.addAction(open_dir_action)
        file_menu.addSeparator()
        save_action = QAction("保存(&S)", self)
        save_action.setShortcut(QKeySequence.StandardKey.Save)
        save_action.triggered.connect(self._save_image)
        file_menu.addAction(save_action)
        save_as_action = QAction("另存为...", self)
        save_as_action.triggered.connect(self._save_image_as)
        file_menu.addAction(save_as_action)
        file_menu.addSeparator()
        exit_action = QAction("退出(&X)", self)
        exit_action.setShortcut(QKeySequence.StandardKey.Quit)
        exit_action.triggered.connect(self.close)
        file_menu.addAction(exit_action)
        view_menu = menubar.addMenu("查看(&V)")
        local_action = QAction("本地浏览(&L)", self)
        local_action.setShortcut(QKeySequence("Ctrl+L"))
        local_action.triggered.connect(lambda: self._set_view_mode(ViewMode.LOCAL))
        view_menu.addAction(local_action)
        online_action = QAction("在线搜索(&O)", self)
        online_action.setShortcut(QKeySequence("Ctrl+O"))
        online_action.triggered.connect(lambda: self._set_view_mode(ViewMode.ONLINE))
        view_menu.addAction(online_action)
        view_menu.addSeparator()
        zoom_in_action = QAction("放大(&I)", self)
        zoom_in_action.setShortcut(QKeySequence.StandardKey.ZoomIn)
        zoom_in_action.triggered.connect(self._image_viewer.zoom_in)
        view_menu.addAction(zoom_in_action)
        zoom_out_action = QAction("缩小(&O)", self)
        zoom_out_action.setShortcut(QKeySequence.StandardKey.ZoomOut)
        zoom_out_action.triggered.connect(self._image_viewer.zoom_out)
        view_menu.addAction(zoom_out_action)
        fit_action = QAction("适应窗口(&F)", self)
        fit_action.setShortcut(QKeySequence("Ctrl+F"))
        fit_action.triggered.connect(self._image_viewer.zoom_fit)
        view_menu.addAction(fit_action)
        actual_action = QAction("实际大小(&A)", self)
        actual_action.setShortcut(QKeySequence("Ctrl+0"))
        actual_action.triggered.connect(self._image_viewer.zoom_actual_size)
        view_menu.addAction(actual_action)
        view_menu.addSeparator()
        rotate_left_action = QAction("向左旋转", self)
        rotate_left_action.setShortcut(QKeySequence("Ctrl+Left"))
        rotate_left_action.triggered.connect(self._image_viewer.rotate_left)
        view_menu.addAction(rotate_left_action)
        rotate_right_action = QAction("向右旋转", self)
        rotate_right_action.setShortcut(QKeySequence("Ctrl+Right"))
        rotate_right_action.triggered.connect(self._image_viewer.rotate_right)
        view_menu.addAction(rotate_right_action)
        settings_menu = menubar.addMenu("设置(&S)")
        settings_action = QAction("配置API密钥(&K)", self)
        settings_action.setShortcut(QKeySequence("Ctrl+,"))
        settings_action.triggered.connect(self._show_settings)
        settings_menu.addAction(settings_action)
        help_menu = menubar.addMenu("帮助(&H)")
        about_action = QAction("关于(&A)", self)
        about_action.triggered.connect(self._show_about)
        help_menu.addAction(about_action)

    def _setup_toolbar(self) -> None:
        toolbar = QToolBar("主工具栏")
        toolbar.setMovable(False)
        self.addToolBar(toolbar)
        self._local_btn = QPushButton("本地浏览")
        self._local_btn.setCheckable(True)
        self._local_btn.setChecked(True)
        self._local_btn.setFixedHeight(36)
        toolbar.addWidget(self._local_btn)
        self._online_btn = QPushButton("在线搜索")
        self._online_btn.setCheckable(True)
        self._online_btn.setFixedHeight(36)
        toolbar.addWidget(self._online_btn)
        toolbar.addSeparator()
        zoom_in_btn = QPushButton("🔍+")
        zoom_in_btn.setToolTip("放大 (Ctrl++)")
        zoom_in_btn.setFixedHeight(36)
        zoom_in_btn.clicked.connect(self._image_viewer.zoom_in)
        toolbar.addWidget(zoom_in_btn)
        zoom_out_btn = QPushButton("🔍-")
        zoom_out_btn.setToolTip("缩小 (Ctrl+-)")
        zoom_out_btn.setFixedHeight(36)
        zoom_out_btn.clicked.connect(self._image_viewer.zoom_out)
        toolbar.addWidget(zoom_out_btn)
        fit_btn = QPushButton("适应")
        fit_btn.setToolTip("适应窗口 (Ctrl+F)")
        fit_btn.setFixedHeight(36)
        fit_btn.clicked.connect(self._image_viewer.zoom_fit)
        toolbar.addWidget(fit_btn)
        toolbar.addSeparator()
        rotate_left_btn = QPushButton("↺")
        rotate_left_btn.setToolTip("向左旋转")
        rotate_left_btn.setFixedHeight(36)
        rotate_left_btn.clicked.connect(self._image_viewer.rotate_left)
        toolbar.addWidget(rotate_left_btn)
        rotate_right_btn = QPushButton("↻")
        rotate_right_btn.setToolTip("向右旋转")
        rotate_right_btn.setFixedHeight(36)
        rotate_right_btn.clicked.connect(self._image_viewer.rotate_right)
        toolbar.addWidget(rotate_right_btn)
        toolbar.addSeparator()
        self._download_btn = QPushButton("⬇ 下载")
        self._download_btn.setToolTip("下载当前图片")
        self._download_btn.setFixedHeight(36)
        self._download_btn.setEnabled(False)
        self._download_btn.clicked.connect(self._download_current_image)
        toolbar.addWidget(self._download_btn)

    def _setup_statusbar(self) -> None:
        self._statusbar = QStatusBar()
        self.setStatusBar(self._statusbar)
        self._status_label = QLabel("就绪")
        self._statusbar.addWidget(self._status_label, 1)
        self._image_info_label = QLabel("")
        self._statusbar.addPermanentWidget(self._image_info_label)
        self._progress_bar = QProgressBar()
        self._progress_bar.setFixedWidth(200)
        self._progress_bar.setVisible(False)
        self._statusbar.addPermanentWidget(self._progress_bar)

    def _connect_signals(self) -> None:
        self._local_btn.clicked.connect(lambda: self._set_view_mode(ViewMode.LOCAL))
        self._online_btn.clicked.connect(lambda: self._set_view_mode(ViewMode.ONLINE))
        self._local_panel.image_selected.connect(self._on_image_selected)
        self._online_panel.image_selected.connect(self._on_image_selected)
        self._online_panel.download_requested.connect(self._download_image)
        self._image_viewer.image_changed.connect(self._on_image_changed)
        self._image_viewer.transform_changed.connect(self._on_transform_changed)

    def _load_settings(self) -> None:
        settings = get_settings()
        if settings.unsplash_api_key:
            logger.info("Unsplash API key configured")

    def _set_view_mode(self, mode: ViewMode) -> None:
        self._view_mode = mode
        if mode == ViewMode.LOCAL:
            self._browser_stack.setCurrentWidget(self._local_panel)
            self._local_btn.setChecked(True)
            self._online_btn.setChecked(False)
        else:
            self._browser_stack.setCurrentWidget(self._online_panel)
            self._local_btn.setChecked(False)
            self._online_btn.setChecked(True)

    def _on_image_selected(self, image: ImageItem) -> None:
        self._current_image = image
        self._image_viewer.load_image_item(image)
        self._download_btn.setEnabled(image.is_online)

    def _on_image_changed(self, image: ImageItem | None) -> None:
        if image:
            self._status_label.setText(f"已加载: {image.title or image.id}")
            self._image_info_label.setText(f"{image.width} x {image.height}")
        else:
            self._status_label.setText("就绪")
            self._image_info_label.setText("")

    def _on_transform_changed(self, transform) -> None:
        pass

    def _open_file(self) -> None:
        file_path, _ = QFileDialog.getOpenFileName(
            self,
            "打开图片",
            "",
            "图片文件 (*.jpg *.jpeg *.png *.gif *.bmp *.webp *.tiff);;所有文件 (*.*)",
        )
        if file_path:
            path = Path(file_path)
            image = self._local_panel._service.get_image_by_path(path)
            if image:
                self._on_image_selected(image)

    def _open_directory(self) -> None:
        dir_path = QFileDialog.getExistingDirectory(self, "选择目录")
        if dir_path:
            self._local_panel._set_directory(Path(dir_path))

    def _save_image(self) -> None:
        if not self._image_viewer.current_image:
            QMessageBox.warning(self, "警告", "没有可保存的图片")
            return
        self._save_image_as()

    def _save_image_as(self) -> None:
        if not self._image_viewer.current_image:
            QMessageBox.warning(self, "警告", "没有可保存的图片")
            return
        file_path, _ = QFileDialog.getSaveFileName(
            self,
            "保存图片",
            "",
            "JPEG (*.jpg);;PNG (*.png);;BMP (*.bmp);;WebP (*.webp)",
        )
        if file_path:
            from imageviewer.utils.image_utils import save_image
            save_image(self._image_viewer.current_image, Path(file_path))

    def _download_image(self, image: ImageItem) -> None:
        asyncio.create_task(self._execute_download(image))

    def _download_current_image(self) -> None:
        if self._current_image and self._current_image.is_online:
            self._download_image(self._current_image)

    async def _execute_download(self, image: ImageItem) -> None:
        self._progress_bar.setVisible(True)
        self._progress_bar.setValue(0)
        self._status_label.setText(f"正在下载: {image.title or image.id}")

        def progress_callback(progress: float):
            self._progress_bar.setValue(int(progress * 100))

        try:
            async with self._download_service as service:
                task = await service.download_image(image, progress_callback=progress_callback)
                if task.status == "completed":
                    self._status_label.setText(f"下载完成: {task.save_path}")
                    QMessageBox.information(self, "下载完成", f"图片已保存到:\n{task.save_path}")
                else:
                    self._status_label.setText(f"下载失败: {task.error_message}")
                    QMessageBox.warning(self, "下载失败", task.error_message or "未知错误")
        except Exception as e:
            logger.error(f"Download failed: {e}")
            QMessageBox.warning(self, "下载失败", str(e))
        finally:
            self._progress_bar.setVisible(False)

    def _show_about(self) -> None:
        QMessageBox.about(
            self,
            "关于图像查看器",
            "图像查看器 v0.1.0\n\n"
            "一个功能完善的图像查看器应用程序\n"
            "支持本地图片浏览和在线图片搜索\n\n"
            "使用 PyQt6 + Pillow 开发",
        )

    def _show_settings(self) -> None:
        from imageviewer.ui.settings_dialog import SettingsDialog
        dialog = SettingsDialog(self)
        dialog.exec()

    def closeEvent(self, event) -> None:
        asyncio.create_task(self._cleanup())
        event.accept()

    async def _cleanup(self) -> None:
        await self._download_service.close()
