"""Configuration management for the image viewer application."""

from pathlib import Path

from pydantic_settings import BaseSettings, SettingsConfigDict

from imageviewer.core.models import AppSettings


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_prefix="IMAGEVIEWER_",
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )

    unsplash_api_key: str = ""
    pexels_api_key: str = ""
    google_api_key: str = ""
    google_search_engine_id: str = ""
    bing_api_key: str = ""
    download_dir: str = str(Path.home() / "Pictures" / "ImageViewer")
    collections_dir: str = str(Path.home() / "Pictures" / "ImageViewer" / "Collections")
    thumbnail_size: int = 200
    max_concurrent_downloads: int = 3
    log_level: str = "INFO"
    log_file: str = "imageviewer.log"


_settings: Settings | None = None


def get_settings() -> Settings:
    global _settings
    if _settings is None:
        _settings = Settings()
    return _settings


def get_app_settings() -> AppSettings:
    settings = get_settings()
    return AppSettings(
        download_dir=Path(settings.download_dir),
        thumbnail_size=(settings.thumbnail_size, settings.thumbnail_size),
        max_concurrent_downloads=settings.max_concurrent_downloads,
        unsplash_api_key=settings.unsplash_api_key,
        pexels_api_key=settings.pexels_api_key,
    )


def reset_settings() -> None:
    global _settings
    _settings = None
