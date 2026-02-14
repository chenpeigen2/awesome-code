"""Image processing utilities."""

from pathlib import Path

from PIL import Image, ImageOps

from imageviewer.core.models import ImageFormat, ImageItem, ImageTransform
from imageviewer.utils.logger import get_logger

logger = get_logger()

FORMAT_MAPPING = {
    ".jpg": ImageFormat.JPEG,
    ".jpeg": ImageFormat.JPEG,
    ".png": ImageFormat.PNG,
    ".gif": ImageFormat.GIF,
    ".bmp": ImageFormat.BMP,
    ".webp": ImageFormat.WEBP,
    ".tiff": ImageFormat.TIFF,
    ".tif": ImageFormat.TIFF,
}

PIL_FORMAT_MAPPING = {
    ImageFormat.JPEG: "JPEG",
    ImageFormat.PNG: "PNG",
    ImageFormat.GIF: "GIF",
    ImageFormat.BMP: "BMP",
    ImageFormat.WEBP: "WEBP",
    ImageFormat.TIFF: "TIFF",
}


def get_image_format(file_path: Path) -> ImageFormat:
    ext = file_path.suffix.lower()
    return FORMAT_MAPPING.get(ext, ImageFormat.UNKNOWN)


def is_supported_format(file_path: Path) -> bool:
    return get_image_format(file_path) != ImageFormat.UNKNOWN


def load_image(file_path: Path) -> Image.Image | None:
    try:
        img = Image.open(file_path)
        img.load()
        return img
    except Exception as e:
        logger.error(f"Failed to load image {file_path}: {e}")
        return None


def load_thumbnail(file_path: Path, size: tuple[int, int] = (200, 200)) -> Image.Image | None:
    try:
        img = Image.open(file_path)
        img.thumbnail(size, Image.Resampling.LANCZOS)
        return img
    except Exception as e:
        logger.error(f"Failed to create thumbnail for {file_path}: {e}")
        return None


def apply_transform(image: Image.Image, transform: ImageTransform) -> Image.Image:
    result = image.copy()
    if transform.rotation != 0:
        result = result.rotate(-transform.rotation, expand=True)
    if transform.flip_horizontal:
        result = ImageOps.mirror(result)
    if transform.flip_vertical:
        result = ImageOps.flip(result)
    if transform.scale != 1.0:
        new_width = int(result.width * transform.scale)
        new_height = int(result.height * transform.scale)
        result = result.resize((new_width, new_height), Image.Resampling.LANCZOS)
    return result


def get_image_info(file_path: Path) -> dict:
    try:
        with Image.open(file_path) as img:
            return {
                "width": img.width,
                "height": img.height,
                "format": get_image_format(file_path),
                "mode": img.mode,
                "file_size": file_path.stat().st_size,
            }
    except Exception as e:
        logger.error(f"Failed to get image info for {file_path}: {e}")
        return {}


def create_image_item_from_path(file_path: Path) -> ImageItem | None:
    if not file_path.exists() or not is_supported_format(file_path):
        return None
    info = get_image_info(file_path)
    if not info:
        return None
    return ImageItem(
        id=str(file_path),
        source="local",
        file_path=file_path,
        title=file_path.name,
        width=info.get("width", 0),
        height=info.get("height", 0),
        file_size=info.get("file_size", 0),
        format=info.get("format", ImageFormat.UNKNOWN),
    )


def save_image(image: Image.Image, save_path: Path, format: ImageFormat | None = None) -> bool:
    try:
        save_path.parent.mkdir(parents=True, exist_ok=True)
        if format:
            pil_format = PIL_FORMAT_MAPPING.get(format)
            if pil_format:
                image.save(save_path, format=pil_format)
            else:
                image.save(save_path)
        else:
            image.save(save_path)
        logger.info(f"Image saved to {save_path}")
        return True
    except Exception as e:
        logger.error(f"Failed to save image to {save_path}: {e}")
        return False
