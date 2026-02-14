"""Application entry point."""

import asyncio
import sys

from PyQt6.QtCore import QTimer
from PyQt6.QtWidgets import QApplication

from imageviewer.ui.main_window import MainWindow
from imageviewer.ui.styles import apply_dark_theme
from imageviewer.utils.logger import setup_logging


def main():
    setup_logging()
    app = QApplication(sys.argv)
    app.setApplicationName("ImageViewer")
    app.setApplicationVersion("0.1.0")
    app.setOrganizationName("ImageViewer")
    apply_dark_theme(app)
    window = MainWindow()
    window.show()

    def run_async():
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        loop.run_until_complete(asyncio.sleep(0.01))

    async_timer = QTimer()
    async_timer.timeout.connect(run_async)
    async_timer.start(50)
    sys.exit(app.exec())


if __name__ == "__main__":
    main()
