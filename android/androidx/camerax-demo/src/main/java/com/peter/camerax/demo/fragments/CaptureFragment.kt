package com.peter.camerax.demo.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.camerax.demo.CameraAdapter
import com.peter.camerax.demo.CameraItem
import com.peter.camerax.demo.CameraOperationType
import com.peter.camerax.demo.MainActivity
import com.peter.camerax.demo.R
import com.peter.camerax.demo.databinding.FragmentCaptureBinding

/**
 * Capture features fragment
 * Displays save photo, image quality, flash mode, HDR mode
 */
class CaptureFragment : Fragment() {

    private var _binding: FragmentCaptureBinding? = null
    private val binding get() = _binding!!

    private val tabPosition = 1
    private lateinit var cameraHelper: com.peter.camerax.demo.CameraHelper
    private var currentFlashMode = ImageCapture.FLASH_MODE_OFF

    companion object {
        fun newInstance() = CaptureFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaptureBinding.inflate(inflater, container, false)
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
                type = CameraOperationType.SAVE_PHOTO,
                title = getString(R.string.save_photo),
                description = getString(R.string.save_photo_desc)
            ),
            CameraItem(
                type = CameraOperationType.IMAGE_QUALITY,
                title = getString(R.string.image_quality),
                description = getString(R.string.image_quality_desc)
            ),
            CameraItem(
                type = CameraOperationType.FLASH_MODE,
                title = getString(R.string.flash_mode),
                description = getString(R.string.flash_mode_desc)
            ),
            CameraItem(
                type = CameraOperationType.HDR_MODE,
                title = getString(R.string.hdr_mode),
                description = getString(R.string.hdr_mode_desc)
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
            CameraOperationType.SAVE_PHOTO -> showSavePhotoInfo()
            CameraOperationType.IMAGE_QUALITY -> showImageQualityOptions()
            CameraOperationType.FLASH_MODE -> showFlashModeOptions()
            CameraOperationType.HDR_MODE -> showHdrInfo()
            else -> {}
        }
    }

    private fun showSavePhotoInfo() {
        val info = """
            Photos are saved to:
            /storage/emulated/0/Android/data/com.peter.camerax.demo/files/Pictures/

            File naming format:
            IMG_yyyyMMdd_HHmmss.jpg
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.save_photo)
            .setMessage(info)
            .setPositiveButton(R.string.close, null)
            .show()
    }

    private fun showImageQualityOptions() {
        val options = arrayOf(
            "JPEG (Default)",
            "HEIC (High Efficiency)"
        )

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.image_quality)
            .setItems(options) { _, which ->
                val message = when (which) {
                    0 -> "JPEG format selected"
                    1 -> "HEIC format selected (requires Android 10+)"
                    else -> ""
                }
                showMessage(message)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showFlashModeOptions() {
        val options = arrayOf(
            getString(R.string.flash_off),
            getString(R.string.flash_on),
            getString(R.string.flash_auto)
        )

        val currentIndex = when (currentFlashMode) {
            ImageCapture.FLASH_MODE_OFF -> 0
            ImageCapture.FLASH_MODE_ON -> 1
            ImageCapture.FLASH_MODE_AUTO -> 2
            else -> 0
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.flash_mode)
            .setSingleChoiceItems(options, currentIndex) { dialog, which ->
                currentFlashMode = when (which) {
                    0 -> ImageCapture.FLASH_MODE_OFF
                    1 -> ImageCapture.FLASH_MODE_ON
                    2 -> ImageCapture.FLASH_MODE_AUTO
                    else -> ImageCapture.FLASH_MODE_OFF
                }
                cameraHelper.setFlashMode(currentFlashMode)
            }
            .setPositiveButton(R.string.close) { _, _ ->
                val modeName = when (currentFlashMode) {
                    ImageCapture.FLASH_MODE_OFF -> getString(R.string.flash_off)
                    ImageCapture.FLASH_MODE_ON -> getString(R.string.flash_on)
                    ImageCapture.FLASH_MODE_AUTO -> getString(R.string.flash_auto)
                    else -> ""
                }
                showMessage(getString(R.string.flash_mode_set, modeName))
            }
            .show()
    }

    private fun showHdrInfo() {
        val info = """
            HDR (High Dynamic Range) mode:

            HDR captures multiple exposures and combines them for better dynamic range.

            Note: HDR support depends on device camera capabilities.
            CameraX Extensions API is required for HDR mode.
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.hdr_mode)
            .setMessage(info)
            .setPositiveButton(R.string.close, null)
            .show()
    }

    private fun showMessage(message: String) {
        (requireActivity() as MainActivity).showSnackbar(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
