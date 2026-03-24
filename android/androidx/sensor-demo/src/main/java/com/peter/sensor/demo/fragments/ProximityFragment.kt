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
import com.peter.sensor.demo.databinding.FragmentProximityBinding

/**
 * Proximity sensors fragment
 * Displays proximity, heart rate sensors
 */
class ProximityFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentProximityBinding? = null
    private val binding get() = _binding!!

    private val tabPosition = 3
    private lateinit var sensorHelper: SensorHelper
    private var currentSensorType: Int = -1

    companion object {
        fun newInstance() = ProximityFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProximityBinding.inflate(inflater, container, false)
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
                type = SensorOperationType.PROXIMITY,
                title = getString(R.string.proximity),
                description = getString(R.string.proximity_desc)
            ),
            SensorItem(
                type = SensorOperationType.HEART_RATE,
                title = getString(R.string.heart_rate),
                description = getString(R.string.heart_rate_desc)
            )
        )
    }

    private fun handleOperation(type: SensorOperationType) {
        stopCurrentSensor()

        when (type) {
            SensorOperationType.PROXIMITY -> startSensorListening(Sensor.TYPE_PROXIMITY)
            SensorOperationType.HEART_RATE -> startSensorListening(Sensor.TYPE_HEART_RATE)
            else -> {}
        }
    }

    private fun startSensorListening(sensorType: Int) {
        if (sensorHelper.isSensorAvailable(sensorType)) {
            currentSensorType = sensorType
            sensorHelper.registerProximity(this)
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
            if (event.sensor.type == currentSensorType) {
                val value = event.values[0]
                when (currentSensorType) {
                    Sensor.TYPE_PROXIMITY -> {
                        // Proximity sensor usually returns binary (0/1)
                        val distance = if (value < 0) "Near" else "Far"
                        showMessage("Distance: $distance cm")
                    }
                    Sensor.TYPE_HEART_RATE -> {
                        // Heart rate in BPM
                        showMessage("Heart Rate: $value BPM")
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
        _binding = null
    }
}
