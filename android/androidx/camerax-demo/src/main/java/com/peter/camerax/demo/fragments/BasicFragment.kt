package com.peter.camerax.demo.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.camera.core.ImageCapture
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.camerax.demo.CameraAdapter
import com.peter.camerax.demo.CameraItem
import com.peter.camerax.demo.CameraOperationType
import com.peter.camerax.demo.MainActivity
import com.peter.camerax.demo.R
import com.peter.camerax.demo.databinding.DialogCameraPreviewBinding
import com.peter.camerax.demo.databinding.FragmentBasicBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Basic camera features fragment
 * Displays camera preview, take photo, switch camera, zoom control
 */
class BasicFragment : Fragment() {

    private var _binding: FragmentBasicBinding? = null
    private val binding get() = _binding!!

    private val tabPosition = 0
    private lateinit var cameraHelper: com.peter.camerax.demo.CameraHelper
    private var previewDialog: AlertDialog? = null
    private var dialogBinding: DialogCameraPreviewBinding? = null
    private var isCameraStarted = false

    companion object {
        fun newInstance() = BasicFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasicBinding.inflate(inflater, container, false)
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
                type = CameraOperationType.CAMERA_PREVIEW,
                title = getString(R.string.camera_preview),
                description = getString(R.string.camera_preview_desc)
            ),
            CameraItem(
                type = CameraOperationType.TAKE_PHOTO,
                title = getString(R.string.take_photo),
                description = getString(R.string.take_photo_desc)
            ),
            CameraItem(
                type = CameraOperationType.SWITCH_CAMERA,
                title = getString(R.string.switch_camera),
                description = getString(R.string.switch_camera_desc)
            ),
            CameraItem(
                type = CameraOperationType.ZOOM_CONTROL,
                title = getString(R.string.zoom_control),
                description = getString(R.string.zoom_control_desc)
            )
        )
    }

    private fun handleOperation(type: CameraOperationType) {
        val mainActivity = requireActivity() as MainActivity

        if (!mainActivity.hasCameraPermission()) {
            showMessage(getString(R.string.permission_denied_msg))
            mainActivity.requestCameraPermission()
            return
        }

        when (type) {
            CameraOperationType.CAMERA_PREVIEW -> showCameraPreview()
            CameraOperationType.TAKE_PHOTO -> takePhoto()
            CameraOperationType.SWITCH_CAMERA -> switchCamera()
            CameraOperationType.ZOOM_CONTROL -> showZoomControl()
            else -> {}
        }
    }

    private fun showCameraPreview() {
        dialogBinding = DialogCameraPreviewBinding.inflate(layoutInflater)

        // Create control buttons container
        val controlsContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
            setPadding(16, 16, 16, 16)
        }

        // Take photo button
        val takePhotoBtn = ImageButton(requireContext()).apply {
            setImageResource(android.R.drawable.ic_menu_camera)
            layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                marginEnd = 32
            }
            setBackgroundResource(android.R.drawable.btn_default)
            setOnClickListener {
                takePhotoInternal()
            }
        }

        // Switch camera button
        val switchBtn = ImageButton(requireContext()).apply {
            setImageResource(android.R.drawable.ic_menu_rotate)
            layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                marginEnd = 32
            }
            setBackgroundResource(android.R.drawable.btn_default)
            setOnClickListener {
                switchCameraInternal()
            }
        }

        // Zoom button
        val zoomBtn = ImageButton(requireContext()).apply {
            setImageResource(android.R.drawable.ic_menu_search)
            layoutParams = LinearLayout.LayoutParams(120, 120)
            setBackgroundResource(android.R.drawable.btn_default)
            setOnClickListener {
                showZoomControlInternal()
            }
        }

        controlsContainer.addView(takePhotoBtn)
        controlsContainer.addView(switchBtn)
        controlsContainer.addView(zoomBtn)

        // Main container
        val mainContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        mainContainer.addView(dialogBinding!!.root, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))
        mainContainer.addView(controlsContainer)

        previewDialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.camera_preview)
            .setView(mainContainer)
            .setNegativeButton(R.string.close) { _, _ ->
                cameraHelper.stopCamera()
                isCameraStarted = false
                dialogBinding = null
            }
            .setCancelable(false)
            .create()

        previewDialog?.setOnDismissListener {
            cameraHelper.stopCamera()
            isCameraStarted = false
            dialogBinding = null
        }

        previewDialog?.show()

        // Start camera preview
        cameraHelper.startCamera(
            previewView = dialogBinding!!.previewView,
            lifecycleOwner = viewLifecycleOwner
        )
        isCameraStarted = true
    }

    private fun takePhoto() {
        if (!isCameraStarted) {
            showMessage(getString(R.string.please_open_preview_first))
            return
        }
        takePhotoInternal()
    }

    private fun takePhotoInternal() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timeStamp.jpg"
        val photoFile = File(requireContext().getExternalFilesDir("Pictures"), fileName)

        cameraHelper.takePictureToFile(
            file = photoFile,
            onSuccess = {
                activity?.runOnUiThread {
                    showMessage(getString(R.string.photo_saved, photoFile.absolutePath))
                }
            },
            onError = { e ->
                activity?.runOnUiThread {
                    showMessage(getString(R.string.photo_failed, e.message ?: "Unknown error"))
                }
            }
        )
    }

    private fun switchCamera() {
        if (!isCameraStarted) {
            showMessage(getString(R.string.please_open_preview_first))
            return
        }
        switchCameraInternal()
    }

    private fun switchCameraInternal() {
        val isFront = cameraHelper.switchCamera()
        // Restart camera with new selector
        dialogBinding?.let { binding ->
            cameraHelper.startCamera(
                previewView = binding.previewView,
                lifecycleOwner = viewLifecycleOwner
            )
        }
        val message = if (isFront) {
            getString(R.string.front_camera)
        } else {
            getString(R.string.back_camera)
        }
        showMessage(message)
    }

    private fun showZoomControl() {
        if (!isCameraStarted) {
            showMessage(getString(R.string.please_open_preview_first))
            return
        }
        showZoomControlInternal()
    }

    private fun showZoomControlInternal() {
        val maxZoom = cameraHelper.getMaxZoomRatio()
        val currentZoom = cameraHelper.getZoomRatio()

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        val zoomText = TextView(requireContext()).apply {
            text = String.format("%.1fx", currentZoom)
            textSize = 18f
            gravity = android.view.Gravity.CENTER
        }

        val seekBar = SeekBar(requireContext()).apply {
            max = ((maxZoom - 1) * 10).toInt()
            progress = ((currentZoom - 1) * 10).toInt()
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val ratio = 1 + progress / 10f
                    cameraHelper.setZoomRatio(ratio)
                    zoomText.text = String.format("%.1fx", ratio)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        container.addView(zoomText)
        container.addView(seekBar)

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.zoom_control)
            .setView(container)
            .setPositiveButton(R.string.close, null)
            .show()
    }

    private fun showMessage(message: String) {
        (requireActivity() as MainActivity).showSnackbar(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        previewDialog?.dismiss()
        previewDialog = null
        dialogBinding = null
        _binding = null
    }
}
