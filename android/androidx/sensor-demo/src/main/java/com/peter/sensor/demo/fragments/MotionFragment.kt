package com.peter.sensor.demo.fragments

import android.app.AlertDialog
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.sensor.demo.MainActivity
import com.peter.sensor.demo.R
import com.peter.sensor.demo.SensorAdapter
import com.peter.sensor.demo.SensorHelper
import com.peter.sensor.demo.SensorItem
import com.peter.sensor.demo.SensorOperationType
import com.peter.sensor.demo.databinding.FragmentMotionBinding

/**
 * Motion sensors fragment
 * Displays accelerometer, gyroscope, gravity, linear acceleration, rotation vector
 */
class MotionFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentMotionBinding? = null
    private val binding get() = _binding!!

    private val tabPosition = 0
    private lateinit var sensorHelper: SensorHelper
    private var currentSensorType: Int = -1
    private var sensorDialog: AlertDialog? = null

    companion object {
        fun newInstance() = MotionFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMotionBinding.inflate(inflater, container, false)
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
                type = SensorOperationType.ACCELEROMETER,
                title = getString(R.string.accelerometer),
                description = getString(R.string.accelerometer_desc)
            ),
            SensorItem(
                type = SensorOperationType.GYROSCOPE,
                title = getString(R.string.gyroscope),
                description = getString(R.string.gyroscope_desc)
            ),
            SensorItem(
                type = SensorOperationType.GRAVITY,
                title = getString(R.string.gravity),
                description = getString(R.string.gravity_desc)
            ),
            SensorItem(
                type = SensorOperationType.LINEAR_ACCELERATION,
                title = getString(R.string.linear_acceleration),
                description = getString(R.string.linear_acceleration_desc)
            ),
            SensorItem(
                type = SensorOperationType.ROTATION_VECTOR,
                title = getString(R.string.rotation_vector),
                description = getString(R.string.rotation_vector_desc)
            )
        )
    }

    private fun handleOperation(type: SensorOperationType) {
        stopCurrentSensor()
        dismissDialog()

        when (type) {
            SensorOperationType.ACCELEROMETER -> startSensorListening(Sensor.TYPE_ACCELEROMETER)
            SensorOperationType.GYROSCOPE -> startSensorListening(Sensor.TYPE_GYROSCOPE)
            SensorOperationType.GRAVITY -> startSensorListening(Sensor.TYPE_GRAVITY)
            SensorOperationType.LINEAR_ACCELERATION -> startSensorListening(Sensor.TYPE_LINEAR_ACCELERATION)
            SensorOperationType.ROTATION_VECTOR -> startSensorListening(Sensor.TYPE_ROTATION_VECTOR)
            else -> {}
        }
    }

    private fun startSensorListening(sensorType: Int) {
        if (sensorHelper.isSensorAvailable(sensorType)) {
            currentSensorType = sensorType
            val success = sensorHelper.registerSensor(this, sensorType)
            if (success) {
                showSensorDialog(sensorType)
            } else {
                showMessage("Failed to register sensor")
            }
        } else {
            showMessage(getString(R.string.sensor_not_available))
        }
    }

    private fun showSensorDialog(sensorType: Int) {
        val sensorName = sensorHelper.getSensorTypeName(sensorType)
        val unit = getUnitForSensor(sensorType)

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(android.R.layout.simple_list_item_1, null)
        val textView = dialogView.findViewById<TextView>(android.R.id.text1)
        textView.textSize = 18f
        textView.setPadding(32, 32, 32, 32)
        textView.text = "Listening..."

        sensorDialog = AlertDialog.Builder(requireContext())
            .setTitle(sensorName)
            .setView(dialogView)
            .setNegativeButton(R.string.stop_listening) { _, _ ->
                stopCurrentSensor()
            }
            .setCancelable(false)
            .create()

        sensorDialog?.show()
    }

    private fun stopCurrentSensor() {
        if (currentSensorType != -1) {
            sensorHelper.unregisterListener(this)
            currentSensorType = -1
        }
    }

    private fun dismissDialog() {
        sensorDialog?.dismiss()
        sensorDialog = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == currentSensorType) {
                val values = it.values
                val unit = getUnitForSensor(currentSensorType)
                val displayText = buildString {
                    append("X: ${String.format("%.2f", values[0])} $unit\n")
                    append("Y: ${String.format("%.2f", values[1])} $unit\n")
                    append("Z: ${String.format("%.2f", values[2])} $unit")
                }

                activity?.runOnUiThread {
                    sensorDialog?.findViewById<TextView>(android.R.id.text1)?.text = displayText
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    private fun getUnitForSensor(sensorType: Int): String {
        return when (sensorType) {
            Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GRAVITY, Sensor.TYPE_LINEAR_ACCELERATION -> getString(R.string.unit_m_s2)
            Sensor.TYPE_GYROSCOPE -> getString(R.string.unit_rad_s)
            else -> ""
        }
    }

    private fun showMessage(message: String) {
        (requireActivity() as MainActivity).showSnackbar(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopCurrentSensor()
        dismissDialog()
        _binding = null
    }
}
