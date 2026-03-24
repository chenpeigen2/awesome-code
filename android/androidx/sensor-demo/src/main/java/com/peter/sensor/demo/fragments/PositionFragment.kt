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
import com.peter.sensor.demo.databinding.FragmentPositionBinding

/**
 * Position sensors fragment
 * Displays magnetic field, orientation, geomagnetic rotation
 */
class PositionFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentPositionBinding? = null
    private val binding get() = _binding!!

    private val tabPosition = 1
    private lateinit var sensorHelper: SensorHelper
    private var currentSensorType: Int = -1

    companion object {
        fun newInstance() = PositionFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPositionBinding.inflate(inflater, container, false)
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
                type = SensorOperationType.MAGNETIC_FIELD,
                title = getString(R.string.magnetic_field),
                description = getString(R.string.magnetic_field_desc)
            ),
            SensorItem(
                type = SensorOperationType.ORIENTATION,
                title = getString(R.string.orientation),
                description = getString(R.string.orientation_desc)
            ),
            SensorItem(
                type = SensorOperationType.GEOMAGNETIC_ROTATION,
                title = getString(R.string.geomagnetic_rotation),
                description = getString(R.string.geomagnetic_rotation_desc)
            )
        )
    }

    private fun handleOperation(type: SensorOperationType) {
        stopCurrentSensor()

        when (type) {
            SensorOperationType.MAGNETIC_FIELD -> startSensorListening(Sensor.TYPE_MAGNETIC_FIELD)
            SensorOperationType.ORIENTATION -> startOrientationListening()
            SensorOperationType.GEOMAGNETIC_ROTATION -> startSensorListening(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
            else -> {}
        }
    }

    private fun startSensorListening(sensorType: Int) {
        if (sensorHelper.isSensorAvailable(sensorType)) {
            currentSensorType = sensorType
            sensorHelper.registerMagneticField(this)
            showMessage("Started listening to ${sensorHelper.getSensorTypeName(sensorType)}")
        } else {
            showMessage(getString(R.string.sensor_not_available))
        }
    }

    private fun startOrientationListening() {
        // Orientation requires both accelerometer and magnetic field
        if (sensorHelper.isSensorAvailable(Sensor.TYPE_ACCELEROMETER) &&
            sensorHelper.isSensorAvailable(Sensor.TYPE_MAGNETIC_FIELD)) {
            currentSensorType = Sensor.TYPE_ORIENTATION
            sensorHelper.registerAccelerometer(this)
            sensorHelper.registerMagneticField(this)
            showMessage("Started listening to Orientation")
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
            val values = it.values
            val unit = getUnitForSensor(it.sensor.type)
            val displayText = buildString {
                if (values.size >= 3) {
                    append("X: ${String.format("%.2f", values[0])} $unit\n")
                    append("Y: ${String.format("%.2f", values[1])} $unit\n")
                    append("Z: ${String.format("%.2f", values[2])} $unit")
                } else {
                    append("Value: ${String.format("%.2f", values[0])} $unit")
                }
            }
            // Update UI with sensor data
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    private fun getUnitForSensor(sensorType: Int): String {
        return when (sensorType) {
            Sensor.TYPE_MAGNETIC_FIELD -> "μT"
            Sensor.TYPE_ORIENTATION -> "°"
            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> ""
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
