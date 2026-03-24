package com.peter.camerax.demo

/**
 * Camera operation type enum
 */
enum class CameraOperationType {
    // Basic
    CAMERA_PREVIEW,
    TAKE_PHOTO,
    SWITCH_CAMERA,
    ZOOM_CONTROL,

    // Capture
    SAVE_PHOTO,
    IMAGE_QUALITY,
    FLASH_MODE,
    HDR_MODE,

    // Video
    VIDEO_RECORDING,
    AUDIO_RECORDING,
    VIDEO_QUALITY,
    FRAME_RATE,

    // Analysis
    IMAGE_ANALYSIS,
    BRIGHTNESS_DETECTION,
    QR_CODE_SCANNING,
    COLOR_DETECTION
}

/**
 * Camera operation category
 */
enum class CameraCategory(val displayName: String) {
    BASIC("Basic"),
    CAPTURE("Capture"),
    VIDEO("Video"),
    ANALYSIS("Analysis")
}

/**
 * Camera operation data model
 */
data class CameraItem(
    val type: CameraOperationType,
    val title: String,
    val description: String,
    val category: CameraCategory = CameraCategory.BASIC
)
