"""Logging configuration for the image viewer application."""

import sys
from pathlib import Path

from loguru import logger

from imageviewer.utils.config import get_settings


def setup_logging() -> None:
    settings = get_settings()
    log_file = Path(settings.log_file)
    if not log_file.is_absolute():
        log_file = Path.home() / ".imageviewer" / settings.log_file
    log_file.parent.mkdir(parents=True, exist_ok=True)
    logger.remove()
    logger.add(
        sys.stderr,
        level=settings.log_level,
        format="<green>{time:YYYY-MM-DD HH:mm:ss}</green> | <level>{level: <8}</level> | <cyan>{name}</cyan>:<cyan>{function}</cyan>:<cyan>{line}</cyan> - <level>{message}</level>",
    )
    logger.add(
        log_file,
        level="DEBUG",
        format="{time:YYYY-MM-DD HH:mm:ss} | {level: <8} | {name}:{function}:{line} - {message}",
        rotation="10 MB",
        retention="7 days",
        compression="zip",
    )
    logger.info("Logging initialized")


def get_logger():
    return logger
