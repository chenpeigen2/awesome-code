package com.peter.sensor.demo.fragments

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.sensor.demo.MainActivity
import com.peter.sensor.demo.R
import com.peter.sensor.demo.SensorAdapter
import com.peter.sensor.demo.SensorHelper
import com.peter.sensor.demo.SensorItem
import com.peter.sensor.demo.SensorOperationType
import com.peter.sensor.demo.databinding.FragmentEnvironmentBinding

/**
 * Environment sensors fragment
 * Displays light, pressure, temperature, humidity sensors
 */
class EnvironmentFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentEnvironmentBinding? = null
    private val binding get() = _binding!!

    private val tabPosition = 2
    private lateinit var sensorHelper: SensorHelper
    private var currentSensorType: Int = -1

    companion object {
        fun newInstance() = EnvironmentFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnvironmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorHelper = (requireActivity() as MainActivity).sensorHelper
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = createSensorItems()
        val mainActivity = requireActivity() as MainActivity
        val adapter = SensorAdapter(
            items = items,
            onItemClick = { type -> handleOperation(type) },
            tabColorRes = mainActivity.getTabColor(tabPosition),
            dotDrawableRes = mainActivity.getTabDotDrawable(tabPosition)
        )
        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun createSensorItems(): List<SensorItem> {
        return listOf(
            SensorItem(
                type = SensorOperationType.LIGHT,
                title = getString(R.string.light),
                description = getString(R.string.light_desc)
            ),
            SensorItem(
                type = SensorOperationType.PRESSURE,
                title = getString(R.string.pressure),
                description = getString(R.string.pressure_desc)
            ),
            SensorItem(
                type = SensorOperationType.TEMPERATURE,
                title = getString(R.string.temperature),
                description = getString(R.string.temperature_desc)
            ),
            SensorItem(
                type = SensorOperationType.HUMIDITY,
                title = getString(R.string.humidity),
                description = getString(R.string.humidity_desc)
            )
        )
    }

    private fun handleOperation(type: SensorOperationType) {
        stopCurrentSensor()

        when (type) {
            SensorOperationType.LIGHT -> startSensorListening(Sensor.TYPE_LIGHT)
            SensorOperationType.PRESSURE -> startSensorListening(Sensor.TYPE_PRESSURE)
            SensorOperationType.TEMPERATURE -> startSensorListening(Sensor.TYPE_AMBIENT_TEMPERATURE)
            SensorOperationType.HUMIDITY -> startSensorListening(Sensor.TYPE_RELATIVE_HUMIDITY)
            else -> {}
        }
    }

    private fun startSensorListening(sensorType: Int) {
        if (sensorHelper.isSensorAvailable(sensorType)) {
            currentSensorType = sensorType
            when (sensorType) {
                Sensor.TYPE_LIGHT -> sensorHelper.registerLight(this)
                Sensor.TYPE_PRESSURE -> sensorHelper.registerPressure(this)
                Sensor.TYPE_AMBIENT_TEMPERATURE -> sensorHelper.registerTemperature(this)
                Sensor.TYPE_RELATIVE_HUMIDITY -> sensorHelper.registerHumidity(this)
            }
            showMessage("Started listening to ${sensorHelper.getSensorTypeName(sensorType)}")
        } else {
            showMessage(getString(R.string.sensor_not_available))
        }
    }

    private fun stopCurrentSensor() {
        if (currentSensorType != -1) {
            sensorHelper.unregisterListener(this)
            currentSensorType = -1
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == currentSensorType) {
                val value = it.values[0]
                val unit = getUnitForSensor(currentSensorType)
                val displayText = when (currentSensorType) {
                    Sensor.TYPE_PRESSURE -> {
                        val altitude = sensorHelper.calculateAltitude(value)
                        "Pressure: ${String.format("%.2f", value)} $unit\nAltitude: ${String.format("%.1f", altitude)} m"
                    }
                    else -> "${String.format("%.2f", value)} $unit"
                }
                // Update UI with sensor data
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    private fun getUnitForSensor(sensorType: Int): String {
        return when (sensorType) {
            Sensor.TYPE_LIGHT -> "lux"
            Sensor.TYPE_PRESSURE -> "hPa"
            Sensor.TYPE_AMBIENT_TEMPERATURE -> "°C"
            Sensor.TYPE_RELATIVE_HUMIDITY -> "%"
            else -> ""
        }
    }

    private fun showMessage(message: String) {
        (requireActivity() as MainActivity).showSnackbar(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopCurrentSensor()
        _binding = null
    }
}
