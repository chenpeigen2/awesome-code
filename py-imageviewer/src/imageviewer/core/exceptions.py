"""Custom exceptions for the image viewer application."""


class ImageViewerError(Exception):
    """Base exception for all image viewer errors."""

    pass


class FileNotFoundError(ImageViewerError):
    """Raised when a file or directory is not found."""

    pass


class UnsupportedFormatError(ImageViewerError):
    """Raised when an unsupported image format is encountered."""

    pass


class APIError(ImageViewerError):
    """Base exception for API-related errors."""

    pass


class APIKeyError(APIError):
    """Raised when API key is missing or invalid."""

    pass


class APIRateLimitError(APIError):
    """Raised when API rate limit is exceeded."""

    pass


class APINetworkError(APIError):
    """Raised when a network error occurs during API call."""

    pass


class DownloadError(ImageViewerError):
    """Raised when a download operation fails."""

    pass


class ImageLoadError(ImageViewerError):
    """Raised when an image cannot be loaded."""

    pass


class ImageSaveError(ImageViewerError):
    """Raised when an image cannot be saved."""

    pass


class ConfigurationError(ImageViewerError):
    """Raised when there is a configuration error."""

    pass
