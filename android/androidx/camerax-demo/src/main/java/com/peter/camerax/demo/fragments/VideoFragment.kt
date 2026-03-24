package com.peter.camerax.demo.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.camerax.demo.CameraAdapter
import com.peter.camerax.demo.CameraItem
import com.peter.camerax.demo.CameraOperationType
import com.peter.camerax.demo.MainActivity
import com.peter.camerax.demo.R
import com.peter.camerax.demo.databinding.FragmentVideoBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Video features fragment
 * Displays video recording, audio recording, video quality, frame rate
 */
class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    private val tabPosition = 2
    private lateinit var cameraHelper: com.peter.camerax.demo.CameraHelper
    private var isRecording = false
    private var recordingWithAudio = true

    companion object {
        fun newInstance() = VideoFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraHelper = (requireActivity() as MainActivity).cameraHelper
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = createCameraItems()
        val mainActivity = requireActivity() as MainActivity
        val adapter = CameraAdapter(
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

    private fun createCameraItems(): List<CameraItem> {
        return listOf(
            CameraItem(
                type = CameraOperationType.VIDEO_RECORDING,
                title = getString(R.string.video_recording),
                description = getString(R.string.video_recording_desc)
            ),
            CameraItem(
                type = CameraOperationType.AUDIO_RECORDING,
                title = getString(R.string.audio_recording),
                description = getString(R.string.audio_recording_desc)
            ),
            CameraItem(
                type = CameraOperationType.VIDEO_QUALITY,
                title = getString(R.string.video_quality),
                description = getString(R.string.video_quality_desc)
            ),
            CameraItem(
                type = CameraOperationType.FRAME_RATE,
                title = getString(R.string.frame_rate),
                description = getString(R.string.frame_rate_desc)
            )
        )
    }

    private fun handleOperation(type: CameraOperationType) {
        val mainActivity = requireActivity() as MainActivity

        if (!mainActivity.hasCameraPermission()) {
            mainActivity.requestCameraPermission()
            return
        }

        when (type) {
            CameraOperationType.VIDEO_RECORDING -> handleVideoRecording()
            CameraOperationType.AUDIO_RECORDING -> toggleAudioRecording()
            CameraOperationType.VIDEO_QUALITY -> showVideoQualityOptions()
            CameraOperationType.FRAME_RATE -> showFrameRateOptions()
            else -> {}
        }
    }

    private fun handleVideoRecording() {
        if (isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        val mainActivity = requireActivity() as MainActivity

        // Check audio permission if recording with audio
        if (recordingWithAudio && !mainActivity.hasRecordAudioPermission()) {
            mainActivity.requestAllPermissions()
            return
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "VID_$timeStamp.mp4"
        val videoFile = File(requireContext().getExternalFilesDir("Movies"), fileName)

        // Initialize camera with video capture
        cameraHelper.startCamera(
            previewView = null,
            lifecycleOwner = viewLifecycleOwner,
            enableVideoCapture = true
        )

        if (recordingWithAudio) {
            cameraHelper.startRecordingWithAudio(
                file = videoFile,
                onSuccess = {
                    activity?.runOnUiThread {
                        isRecording = true
                        showMessage(getString(R.string.recording_started))
                    }
                },
                onError = { e ->
                    activity?.runOnUiThread {
                        showMessage(getString(R.string.recording_failed, e.message ?: "Unknown error"))
                    }
                }
            )
        } else {
            cameraHelper.startRecording(
                file = videoFile,
                onSuccess = {
                    activity?.runOnUiThread {
                        isRecording = true
                        showMessage(getString(R.string.recording_started))
                    }
                },
                onError = { e ->
                    activity?.runOnUiThread {
                        showMessage(getString(R.string.recording_failed, e.message ?: "Unknown error"))
                    }
                }
            )
        }
    }

    private fun stopRecording() {
        cameraHelper.stopRecording()
        isRecording = false
        showMessage(getString(R.string.recording_stopped))
    }

    private fun toggleAudioRecording() {
        recordingWithAudio = !recordingWithAudio
        val status = if (recordingWithAudio) {
            getString(R.string.audio_enabled)
        } else {
            getString(R.string.audio_disabled)
        }
        showMessage(status)
    }

    private fun showVideoQualityOptions() {
        val options = arrayOf(
            "SD (480p)",
            "HD (720p)",
            "FHD (1080p)",
            "UHD (4K)"
        )

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.video_quality)
            .setItems(options) { _, which ->
                val quality = when (which) {
                    0 -> "SD (480p)"
                    1 -> "HD (720p)"
                    2 -> "FHD (1080p)"
                    3 -> "UHD (4K)"
                    else -> "HD (720p)"
                }
                showMessage(getString(R.string.video_quality_set, quality))
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showFrameRateOptions() {
        val options = arrayOf(
            "30 fps",
            "60 fps"
        )

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.frame_rate)
            .setItems(options) { _, which ->
                val fps = if (which == 0) "30 fps" else "60 fps"
                showMessage(getString(R.string.frame_rate_set, fps))
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showMessage(message: String) {
        (requireActivity() as MainActivity).showSnackbar(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isRecording) {
            cameraHelper.stopRecording()
            isRecording = false
        }
        _binding = null
    }
}
