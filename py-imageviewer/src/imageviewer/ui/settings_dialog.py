"""Settings dialog for configuring API keys and application settings."""

from pathlib import Path

from PyQt6.QtCore import Qt
from PyQt6.QtWidgets import (
    QDialog,
    QDialogButtonBox,
    QFileDialog,
    QFormLayout,
    QHBoxLayout,
    QLabel,
    QLineEdit,
    QMessageBox,
    QPushButton,
    QTabWidget,
    QVBoxLayout,
    QWidget,
)

from imageviewer.utils.config import get_settings
from imageviewer.utils.logger import get_logger

logger = get_logger()


class SettingsDialog(QDialog):
    def __init__(self, parent: QWidget | None = None):
        super().__init__(parent)
        self.setWindowTitle("设置")
        self.setMinimumSize(500, 400)
        self._setup_ui()
        self._load_settings()

    def _setup_ui(self) -> None:
        layout = QVBoxLayout(self)
        tabs = QTabWidget()
        tabs.addTab(self._create_api_tab(), "API密钥")
        tabs.addTab(self._create_paths_tab(), "路径设置")
        layout.addWidget(tabs)
        buttons = QDialogButtonBox(
            QDialogButtonBox.StandardButton.Save | QDialogButtonBox.StandardButton.Cancel
        )
        buttons.accepted.connect(self._save_settings)
        buttons.rejected.connect(self.reject)
        layout.addWidget(buttons)

    def _create_api_tab(self) -> QWidget:
        widget = QWidget()
        layout = QVBoxLayout(widget)
        form = QFormLayout()
        self._unsplash_key = QLineEdit()
        self._unsplash_key.setPlaceholderText("从 https://unsplash.com/developers 获取")
        self._unsplash_key.setEchoMode(QLineEdit.EchoMode.Password)
        form.addRow("Unsplash API Key:", self._unsplash_key)
        self._google_key = QLineEdit()
        self._google_key.setPlaceholderText("从 https://console.cloud.google.com 获取")
        self._google_key.setEchoMode(QLineEdit.EchoMode.Password)
        form.addRow("Google API Key:", self._google_key)
        self._google_se_id = QLineEdit()
        self._google_se_id.setPlaceholderText("自定义搜索引擎ID")
        form.addRow("Google Search Engine ID:", self._google_se_id)
        self._bing_key = QLineEdit()
        self._bing_key.setPlaceholderText("从 https://azure.microsoft.com 获取")
        self._bing_key.setEchoMode(QLineEdit.EchoMode.Password)
        form.addRow("Bing API Key:", self._bing_key)
        layout.addLayout(form)
        help_text = QLabel(
            "<b>如何获取API密钥：</b><br>"
            "• <a href='https://unsplash.com/developers'>Unsplash</a> - 免费注册开发者账号<br>"
            "• <a href='https://developers.google.com/custom-search/v1/introduction'>Google</a> - 需要API Key和Search Engine ID<br>"
            "• <a href='https://azure.microsoft.com/en-us/services/cognitive-services/bing-image-search-api/'>Bing</a> - Azure认知服务"
        )
        help_text.setOpenExternalLinks(True)
        help_text.setWordWrap(True)
        layout.addWidget(help_text)
        layout.addStretch()
        show_btn = QPushButton("显示/隐藏密钥")
        show_btn.setCheckable(True)
        show_btn.toggled.connect(self._toggle_key_visibility)
        layout.addWidget(show_btn)
        return widget

    def _create_paths_tab(self) -> QWidget:
        widget = QWidget()
        layout = QVBoxLayout(widget)
        form = QFormLayout()
        download_row = QHBoxLayout()
        self._download_dir = QLineEdit()
        download_row.addWidget(self._download_dir)
        download_btn = QPushButton("浏览...")
        download_btn.clicked.connect(self._browse_download_dir)
        download_row.addWidget(download_btn)
        form.addRow("下载目录:", download_row)
        collections_row = QHBoxLayout()
        self._collections_dir = QLineEdit()
        collections_row.addWidget(self._collections_dir)
        collections_btn = QPushButton("浏览...")
        collections_btn.clicked.connect(self._browse_collections_dir)
        collections_row.addWidget(collections_btn)
        form.addRow("收藏夹目录:", collections_row)
        layout.addLayout(form)
        layout.addStretch()
        return widget

    def _toggle_key_visibility(self, checked: bool) -> None:
        mode = QLineEdit.EchoMode.Normal if checked else QLineEdit.EchoMode.Password
        self._unsplash_key.setEchoMode(mode)
        self._google_key.setEchoMode(mode)
        self._bing_key.setEchoMode(mode)

    def _browse_download_dir(self) -> None:
        dir_path = QFileDialog.getExistingDirectory(self, "选择下载目录")
        if dir_path:
            self._download_dir.setText(dir_path)

    def _browse_collections_dir(self) -> None:
        dir_path = QFileDialog.getExistingDirectory(self, "选择收藏夹目录")
        if dir_path:
            self._collections_dir.setText(dir_path)

    def _load_settings(self) -> None:
        settings = get_settings()
        self._unsplash_key.setText(settings.unsplash_api_key)
        self._google_key.setText(settings.google_api_key)
        self._google_se_id.setText(settings.google_search_engine_id)
        self._bing_key.setText(settings.bing_api_key)
        self._download_dir.setText(settings.download_dir)
        self._collections_dir.setText(settings.collections_dir)

    def _save_settings(self) -> None:
        import os
        from imageviewer.utils.config import reset_settings
        env_path = Path(__file__).parent.parent.parent.parent.parent / ".env"
        lines = []
        existing = {}
        if env_path.exists():
            with open(env_path, encoding="utf-8") as f:
                for line in f:
                    line = line.strip()
                    if line and not line.startswith("#") and "=" in line:
                        key, _, value = line.partition("=")
                        existing[key.strip()] = value.strip()
        existing["IMAGEVIEWER_UNSPLASH_API_KEY"] = self._unsplash_key.text()
        existing["IMAGEVIEWER_GOOGLE_API_KEY"] = self._google_key.text()
        existing["IMAGEVIEWER_GOOGLE_SEARCH_ENGINE_ID"] = self._google_se_id.text()
        existing["IMAGEVIEWER_BING_API_KEY"] = self._bing_key.text()
        existing["IMAGEVIEWER_DOWNLOAD_DIR"] = self._download_dir.text()
        existing["IMAGEVIEWER_COLLECTIONS_DIR"] = self._collections_dir.text()
        with open(env_path, "w", encoding="utf-8") as f:
            f.write("# Image Viewer Configuration\n")
            f.write("# Generated by Settings Dialog\n\n")
            for key, value in existing.items():
                f.write(f"{key}={value}\n")
        reset_settings()
        QMessageBox.information(self, "设置已保存", "API密钥和设置已保存。\n请重启应用程序以应用更改。")
        self.accept()
