package com.peter.sensor.demo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener

/**
 * 传感器信息数据类
 */
data class SensorInfo(
    val name: String,
    val type: Int,
    val vendor: String,
    val version: Int,
    val maxRange: Float,
    val resolution: Float,
    val power: Float,
    val minDelay: Int
) {
    companion object {
        fun fromSensor(sensor: Sensor): SensorInfo {
            return SensorInfo(
                name = sensor.name,
                type = sensor.type,
                vendor = sensor.vendor,
                version = sensor.version,
                maxRange = sensor.maximumRange,
                resolution = sensor.resolution,
                power = sensor.power,
                minDelay = sensor.minDelay
            )
        }
    }
}

/**
 * 传感器辅助类
 * 封装传感器注册、注销和数据格式化
 */
class SensorHelper(private val context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // 获取所有传感器列表
    fun getAllSensors(): List<SensorInfo> {
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
        return sensorList.map { SensorInfo.fromSensor(it) }
    }

    // 获取特定类型的传感器详情
    fun getSensorDetails(sensorType: Int): SensorInfo? {
        val sensor = sensorManager.getDefaultSensor(sensorType) ?: return null
        return SensorInfo.fromSensor(sensor)
    }

    // 检查传感器是否可用
    fun isSensorAvailable(sensorType: Int): Boolean {
        return sensorManager.getDefaultSensor(sensorType) != null
    }

    // 通用传感器注册方法
    fun registerSensor(listener: SensorEventListener, sensorType: Int): Boolean {
        val sensor = sensorManager.getDefaultSensor(sensorType) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // ==================== Motion Sensors ====================

    // 加速度计
    fun registerAccelerometer(listener: SensorEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // 陀螺仪
    fun registerGyroscope(listener: SensorEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // 重力传感器
    fun registerGravity(listener: SensorEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // 线性加速度
    fun registerLinearAcceleration(listener: SensorEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // 旋转向量
    fun registerRotationVector(listener: SensorEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // ==================== Position Sensors ====================

    // 磁场传感器
    fun registerMagneticField(listener: SensorEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // 地磁旋转向量
    fun registerGeomagneticRotation(listener: SensorEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // 计算设备方向
    fun getOrientation(accelerometerValues: FloatArray, magneticFieldValues: FloatArray): FloatArray? {
        val rotationMatrix = FloatArray(9)
        val orientationValues = FloatArray(3)

        if (SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magneticFieldValues)) {
            SensorManager.getOrientation(rotationMatrix, orientationValues)
            return orientationValues
        }
        return null
    }

    // ==================== Environment Sensors ====================

    // 光线传感器
    fun registerLight(listener: SensorEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // 气压传感器
    fun registerPressure(listener: SensorEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // 温度传感器
    fun registerTemperature(listener: SensorEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // 湿度传感器
    fun registerHumidity(listener: SensorEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // ==================== Proximity Sensors ====================

    // 距离传感器
    fun registerProximity(listener: SensorEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // 心率传感器
    fun registerHeartRate(listener: SensorEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // ==================== Device Sensors ====================

    // 计步器（步数计数器）
    fun registerStepCounter(listener: SensorEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // 步数检测器
    fun registerStepDetector(listener: SensorEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) ?: return false
        return sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    // 显著运动检测
    fun registerSignificantMotion(listener: TriggerEventListener): Boolean {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION) ?: return false
        sensorManager.requestTriggerSensor(listener, sensor)
        return true
    }

    // 取消显著运动检测
    fun cancelSignificantMotion(listener: TriggerEventListener) {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION)
        if (sensor != null) {
            sensorManager.cancelTriggerSensor(listener, sensor)
        }
    }

    // ==================== Utility Methods ====================

    // 注销传感器监听
    fun unregisterListener(listener: SensorEventListener) {
        sensorManager.unregisterListener(listener)
    }

    // 格式化传感器数值
    fun formatSensorValue(value: Float, unit: String): String {
        return String.format("%.2f %s", value, unit)
    }

    // 格式化三轴数值
    fun formatAxisValues(values: FloatArray, unit: String): String {
        return "X: ${String.format("%.2f", values[0])} $unit\n" +
               "Y: ${String.format("%.2f", values[1])} $unit\n" +
               "Z: ${String.format("%.2f", values[2])} $unit"
    }

    // 获取传感器类型名称
    fun getSensorTypeName(sensorType: Int): String {
        return when (sensorType) {
            Sensor.TYPE_ACCELEROMETER -> "Accelerometer"
            Sensor.TYPE_MAGNETIC_FIELD -> "Magnetic Field"
            Sensor.TYPE_ORIENTATION -> "Orientation"
            Sensor.TYPE_GYROSCOPE -> "Gyroscope"
            Sensor.TYPE_LIGHT -> "Light"
            Sensor.TYPE_PRESSURE -> "Pressure"
            Sensor.TYPE_TEMPERATURE -> "Temperature"
            Sensor.TYPE_PROXIMITY -> "Proximity"
            Sensor.TYPE_GRAVITY -> "Gravity"
            Sensor.TYPE_LINEAR_ACCELERATION -> "Linear Acceleration"
            Sensor.TYPE_ROTATION_VECTOR -> "Rotation Vector"
            Sensor.TYPE_RELATIVE_HUMIDITY -> "Relative Humidity"
            Sensor.TYPE_AMBIENT_TEMPERATURE -> "Ambient Temperature"
            Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> "Magnetic Field Uncalibrated"
            Sensor.TYPE_GAME_ROTATION_VECTOR -> "Game Rotation Vector"
            Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> "Gyroscope Uncalibrated"
            Sensor.TYPE_SIGNIFICANT_MOTION -> "Significant Motion"
            Sensor.TYPE_STEP_DETECTOR -> "Step Detector"
            Sensor.TYPE_STEP_COUNTER -> "Step Counter"
            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> "Geomagnetic Rotation Vector"
            Sensor.TYPE_HEART_RATE -> "Heart Rate"
            Sensor.TYPE_POSE_6DOF -> "Pose 6DOF"
            Sensor.TYPE_STATIONARY_DETECT -> "Stationary Detect"
            Sensor.TYPE_MOTION_DETECT -> "Motion Detect"
            Sensor.TYPE_HEART_BEAT -> "Heart Beat"
            Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT -> "Low Latency Offbody Detect"
            Sensor.TYPE_ACCELEROMETER_UNCALIBRATED -> "Accelerometer Uncalibrated"
            else -> "Unknown ($sensorType)"
        }
    }

    // 计算海拔（基于气压）
    fun calculateAltitude(pressure: Float): Float {
        // 使用国际标准大气模型
        //海拔 = 44330 * (1 - (P / P0)^0.1903)
        val P0 = SensorManager.PRESSURE_STANDARD_ATMOSPHERE
        return 44330 * (1 - Math.pow((pressure / P0).toDouble(), 0.1903)).toFloat()
    }
}
