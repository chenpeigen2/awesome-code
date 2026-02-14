"""Tests for base search service."""

import pytest

from imageviewer.services.base_search_service import (
    ImageColor,
    ImageLayout,
    ImageSize,
    ImageType,
    SafeSearch,
    SearchFilters,
)


class TestSearchFilters:
    def test_default_values(self):
        filters = SearchFilters()
        assert filters.size == ImageSize.ANY
        assert filters.color == ImageColor.ANY
        assert filters.image_type == ImageType.ANY
        assert filters.layout == ImageLayout.ANY
        assert filters.safe_search == SafeSearch.MODERATE

    def test_to_dict(self):
        filters = SearchFilters(
            size=ImageSize.LARGE,
            color=ImageColor.RED,
            image_type=ImageType.PHOTO,
        )
        d = filters.to_dict()
        assert d["size"] == "large"
        assert d["color"] == "red"
        assert d["image_type"] == "photo"


class TestEnums:
    def test_image_size(self):
        assert ImageSize.ANY.value == ""
        assert ImageSize.LARGE.value == "large"
        assert ImageSize.MEDIUM.value == "medium"
        assert ImageSize.SMALL.value == "small"

    def test_image_color(self):
        assert ImageColor.ANY.value == ""
        assert ImageColor.RED.value == "red"
        assert ImageColor.BLUE.value == "blue"
        assert ImageColor.BLACK_WHITE.value == "blackandwhite"

    def test_image_type(self):
        assert ImageType.ANY.value == ""
        assert ImageType.PHOTO.value == "photo"
        assert ImageType.CLIPART.value == "clipart"
        assert ImageType.ANIMATED.value == "animated"

    def test_image_layout(self):
        assert ImageLayout.ANY.value == ""
        assert ImageLayout.HORIZONTAL.value == "horizontal"
        assert ImageLayout.VERTICAL.value == "vertical"
        assert ImageLayout.SQUARE.value == "square"

    def test_safe_search(self):
        assert SafeSearch.OFF.value == "off"
        assert SafeSearch.MODERATE.value == "moderate"
        assert SafeSearch.STRICT.value == "strict"
