package com.peter.sensor.demo

/**
 * 传感器操作类型枚举
 */
enum class SensorOperationType {
    // Motion sensors
    ACCELEROMETER,
    GYROSCOPE,
    GRAVITY,
    LINEAR_ACCELERATION,
    ROTATION_VECTOR,

    // Position sensors
    MAGNETIC_FIELD,
    ORIENTATION,
    GEOMAGNETIC_ROTATION,

    // Environment sensors
    LIGHT,
    PRESSURE,
    TEMPERATURE,
    HUMIDITY,

    // Proximity sensors
    PROXIMITY,
    HEART_RATE,

    // Device sensors
    SENSOR_LIST,
    SENSOR_DETAILS,
    STEP_COUNTER,
    STEP_DETECTOR,
    SIGNIFICANT_MOTION
}

/**
 * 传感器操作分类
 */
enum class SensorCategory(val displayName: String) {
    MOTION("Motion Sensors"),
    POSITION("Position Sensors"),
    ENVIRONMENT("Environment Sensors"),
    PROXIMITY("Proximity Sensors"),
    DEVICE("Device Sensors")
}

/**
 * 传感器操作数据模型
 */
data class SensorItem(
    val type: SensorOperationType,
    val title: String,
    val description: String,
    val category: SensorCategory = SensorCategory.MOTION,
    val dotDrawableRes: Int = R.drawable.bg_color_dot
)
