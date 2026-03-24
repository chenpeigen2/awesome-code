package com.peter.sensor.demo.fragments

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
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
import com.peter.sensor.demo.databinding.FragmentDeviceBinding

/**
 * Device sensors fragment
 * Displays sensor list, step counter, Step detector, Significant motion
 */
class DeviceFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentDeviceBinding? = null
    private val binding get() = _binding!!

    private val tabPosition = 4
    private lateinit var sensorHelper: SensorHelper
    private var currentSensorType: Int = -1
    private var triggerListener: TriggerEventListener? = null

    companion object {
        fun newInstance() = DeviceFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
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
                type = SensorOperationType.SENSOR_LIST,
                title = getString(R.string.sensor_list),
                description = getString(R.string.sensor_list_desc)
            ),
            SensorItem(
                type = SensorOperationType.SENSOR_DETAILS,
                title = getString(R.string.sensor_details),
                description = getString(R.string.sensor_details_desc)
            ),
            SensorItem(
                type = SensorOperationType.STEP_COUNTER,
                title = getString(R.string.step_counter),
                description = getString(R.string.step_counter_desc)
            ),
            SensorItem(
                type = SensorOperationType.STEP_DETECTOR,
                title = getString(R.string.step_detector),
                description = getString(R.string.step_detector_desc)
            ),
            SensorItem(
                type = SensorOperationType.SIGNIFICANT_MOTION,
                title = getString(R.string.significant_motion),
                description = getString(R.string.significant_motion_desc)
            )
        )
    }

    private fun handleOperation(type: SensorOperationType) {
        stopCurrentSensor()

        when (type) {
            SensorOperationType.SENSOR_LIST -> showSensorList()
            SensorOperationType.SENSOR_DETAILS -> {
                showMessage("Sensor details feature - select a sensor from the list")
            }
            SensorOperationType.STEP_COUNTER -> startSensorListening(Sensor.TYPE_STEP_COUNTER)
            SensorOperationType.STEP_DETECTOR -> startSensorListening(Sensor.TYPE_STEP_DETECTOR)
            SensorOperationType.SIGNIFICANT_MOTION -> startSignificantMotionListening()
            else -> {}
        }
    }

    private fun showSensorList() {
        val sensors = sensorHelper.getAllSensors()
        if (sensors.isEmpty()) {
            showMessage("No sensors found")
            return
        }

        val sensorNames = sensors.map {
            "${it.name}\n  Type: ${sensorHelper.getSensorTypeName(it.type)}\n  Vendor: ${it.vendor}"
        }
        showMessage(sensorNames.joinToString())
    }

    private fun startSensorListening(sensorType: Int) {
        if (sensorHelper.isSensorAvailable(sensorType)) {
            currentSensorType = sensorType
            when (sensorType) {
                Sensor.TYPE_STEP_COUNTER -> sensorHelper.registerStepCounter(this)
                Sensor.TYPE_STEP_DETECTOR -> sensorHelper.registerStepDetector(this)
                else -> sensorHelper.registerAccelerometer(this)
            }
            showMessage("Started listening to ${sensorHelper.getSensorTypeName(sensorType)}")
        } else {
            showMessage(getString(R.string.sensor_not_available))
        }
    }

    private fun startSignificantMotionListening() {
        triggerListener = object : TriggerEventListener() {
            override fun onTrigger(event: TriggerEvent?) {
                showMessage(getString(R.string.motion_detected))
            }
        }
        if (sensorHelper.isSensorAvailable(Sensor.TYPE_SIGNIFICANT_MOTION)) {
            triggerListener?.let { listener ->
                sensorHelper.registerSignificantMotion(listener)
                showMessage("Waiting for significant motion...")
            }
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
            if (event.sensor.type == currentSensorType) {
                val value = event.values[0]
                when (currentSensorType) {
                    Sensor.TYPE_STEP_COUNTER -> {
                        // Step counter returns total steps since boot
                        showMessage("Total Steps: ${value.toLong()}")
                    }
                    Sensor.TYPE_STEP_DETECTOR -> {
                        showMessage(getString(R.string.step_detected))
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    private fun showMessage(message: String) {
        (requireActivity() as MainActivity).showSnackbar(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopCurrentSensor()
        triggerListener = null
        _binding = null
    }
}
