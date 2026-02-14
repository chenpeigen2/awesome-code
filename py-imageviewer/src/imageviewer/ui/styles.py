"""Application styles and themes."""

from PyQt6.QtGui import QColor, QFont, QPalette
from PyQt6.QtWidgets import QApplication


def get_dark_palette() -> QPalette:
    palette = QPalette()
    palette.setColor(QPalette.ColorGroup.Active, QPalette.ColorRole.Window, QColor(53, 53, 53))
    palette.setColor(QPalette.ColorGroup.Active, QPalette.ColorRole.WindowText, QColor(255, 255, 255))
    palette.setColor(QPalette.ColorGroup.Active, QPalette.ColorRole.Base, QColor(35, 35, 35))
    palette.setColor(QPalette.ColorGroup.Active, QPalette.ColorRole.AlternateBase, QColor(53, 53, 53))
    palette.setColor(QPalette.ColorGroup.Active, QPalette.ColorRole.ToolTipBase, QColor(25, 25, 25))
    palette.setColor(QPalette.ColorGroup.Active, QPalette.ColorRole.ToolTipText, QColor(255, 255, 255))
    palette.setColor(QPalette.ColorGroup.Active, QPalette.ColorRole.Text, QColor(255, 255, 255))
    palette.setColor(QPalette.ColorGroup.Active, QPalette.ColorRole.Button, QColor(53, 53, 53))
    palette.setColor(QPalette.ColorGroup.Active, QPalette.ColorRole.ButtonText, QColor(255, 255, 255))
    palette.setColor(QPalette.ColorGroup.Active, QPalette.ColorRole.BrightText, QColor(255, 0, 0))
    palette.setColor(QPalette.ColorGroup.Active, QPalette.ColorRole.Link, QColor(42, 130, 218))
    palette.setColor(QPalette.ColorGroup.Active, QPalette.ColorRole.Highlight, QColor(42, 130, 218))
    palette.setColor(QPalette.ColorGroup.Active, QPalette.ColorRole.HighlightedText, QColor(0, 0, 0))
    palette.setColor(QPalette.ColorGroup.Disabled, QPalette.ColorRole.WindowText, QColor(127, 127, 127))
    palette.setColor(QPalette.ColorGroup.Disabled, QPalette.ColorRole.Text, QColor(127, 127, 127))
    palette.setColor(QPalette.ColorGroup.Disabled, QPalette.ColorRole.ButtonText, QColor(127, 127, 127))
    return palette


STYLESHEET = """
QMainWindow {
    background-color: #353535;
}

QWidget {
    background-color: #353535;
    color: #ffffff;
    font-family: 'Segoe UI', Arial, sans-serif;
    font-size: 12px;
}

QPushButton {
    background-color: #2a82da;
    color: white;
    border: none;
    padding: 8px 16px;
    border-radius: 4px;
    font-weight: bold;
}

QPushButton:hover {
    background-color: #3a92ea;
}

QPushButton:pressed {
    background-color: #1a72ca;
}

QPushButton:disabled {
    background-color: #555555;
    color: #888888;
}

QLineEdit {
    background-color: #232323;
    border: 1px solid #444444;
    padding: 8px;
    border-radius: 4px;
    color: #ffffff;
}

QLineEdit:focus {
    border-color: #2a82da;
}

QTreeWidget, QListWidget, QTableWidget {
    background-color: #232323;
    border: 1px solid #444444;
    border-radius: 4px;
    color: #ffffff;
}

QTreeWidget::item:selected, QListWidget::item:selected, QTableWidget::item:selected {
    background-color: #2a82da;
}

QTreeWidget::item:hover, QListWidget::item:hover {
    background-color: #404040;
}

QScrollBar:vertical {
    background-color: #232323;
    width: 12px;
    border-radius: 6px;
}

QScrollBar::handle:vertical {
    background-color: #555555;
    border-radius: 6px;
    min-height: 20px;
}

QScrollBar::handle:vertical:hover {
    background-color: #666666;
}

QScrollBar:horizontal {
    background-color: #232323;
    height: 12px;
    border-radius: 6px;
}

QScrollBar::handle:horizontal {
    background-color: #555555;
    border-radius: 6px;
    min-width: 20px;
}

QLabel {
    color: #ffffff;
}

QComboBox {
    background-color: #232323;
    border: 1px solid #444444;
    padding: 6px;
    border-radius: 4px;
    color: #ffffff;
}

QComboBox::drop-down {
    border: none;
}

QComboBox::down-arrow {
    image: none;
    border-left: 5px solid transparent;
    border-right: 5px solid transparent;
    border-top: 5px solid #ffffff;
    margin-right: 8px;
}

QComboBox QAbstractItemView {
    background-color: #232323;
    border: 1px solid #444444;
    selection-background-color: #2a82da;
    color: #ffffff;
}

QProgressBar {
    background-color: #232323;
    border: 1px solid #444444;
    border-radius: 4px;
    text-align: center;
    color: #ffffff;
}

QProgressBar::chunk {
    background-color: #2a82da;
    border-radius: 3px;
}

QSplitter::handle {
    background-color: #444444;
}

QTabWidget::pane {
    border: 1px solid #444444;
    border-radius: 4px;
}

QTabBar::tab {
    background-color: #232323;
    padding: 8px 16px;
    border-top-left-radius: 4px;
    border-top-right-radius: 4px;
    color: #ffffff;
}

QTabBar::tab:selected {
    background-color: #2a82da;
}

QTabBar::tab:hover:!selected {
    background-color: #404040;
}

QGroupBox {
    border: 1px solid #444444;
    border-radius: 4px;
    margin-top: 8px;
    padding-top: 8px;
    color: #ffffff;
}

QGroupBox::title {
    subcontrol-origin: margin;
    subcontrol-position: top left;
    padding: 0 8px;
    color: #ffffff;
}

QSlider::groove:horizontal {
    background-color: #232323;
    height: 8px;
    border-radius: 4px;
}

QSlider::handle:horizontal {
    background-color: #2a82da;
    width: 16px;
    height: 16px;
    margin: -4px 0;
    border-radius: 8px;
}

QSlider::handle:horizontal:hover {
    background-color: #3a92ea;
}

QMenu {
    background-color: #232323;
    border: 1px solid #444444;
    color: #ffffff;
}

QMenu::item:selected {
    background-color: #2a82da;
}

QStatusBar {
    background-color: #232323;
    color: #ffffff;
}

QToolBar {
    background-color: #404040;
    border: none;
    spacing: 4px;
    padding: 4px;
}

QToolButton {
    background-color: transparent;
    border: none;
    padding: 4px;
    border-radius: 4px;
}

QToolButton:hover {
    background-color: #555555;
}

QToolButton:pressed {
    background-color: #2a82da;
}
"""


def apply_dark_theme(app: QApplication) -> None:
    app.setStyle("Fusion")
    app.setPalette(get_dark_palette())
    app.setStyleSheet(STYLESHEET)


def get_font(size: int = 12, bold: bool = False) -> QFont:
    font = QFont("Segoe UI", size)
    font.setBold(bold)
    return font
