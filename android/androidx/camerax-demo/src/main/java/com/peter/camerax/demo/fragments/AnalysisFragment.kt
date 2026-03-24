package com.peter.camerax.demo.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.camerax.demo.CameraAdapter
import com.peter.camerax.demo.CameraItem
import com.peter.camerax.demo.CameraOperationType
import com.peter.camerax.demo.MainActivity
import com.peter.camerax.demo.R
import com.peter.camerax.demo.databinding.FragmentAnalysisBinding

/**
 * Analysis features fragment
 * Displays image analysis, brightness detection, QR code scanning, color detection
 */
class AnalysisFragment : Fragment() {

    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!

    private val tabPosition = 3
    private lateinit var cameraHelper: com.peter.camerax.demo.CameraHelper
    private var analysisDialog: AlertDialog? = null
    private var isAnalyzing = false

    companion object {
        fun newInstance() = AnalysisFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
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
                type = CameraOperationType.IMAGE_ANALYSIS,
                title = getString(R.string.image_analysis),
                description = getString(R.string.image_analysis_desc)
            ),
            CameraItem(
                type = CameraOperationType.BRIGHTNESS_DETECTION,
                title = getString(R.string.brightness_detection),
                description = getString(R.string.brightness_detection_desc)
            ),
            CameraItem(
                type = CameraOperationType.QR_CODE_SCANNING,
                title = getString(R.string.qr_code_scanning),
                description = getString(R.string.qr_code_scanning_desc)
            ),
            CameraItem(
                type = CameraOperationType.COLOR_DETECTION,
                title = getString(R.string.color_detection),
                description = getString(R.string.color_detection_desc)
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
            CameraOperationType.IMAGE_ANALYSIS -> showImageAnalysis()
            CameraOperationType.BRIGHTNESS_DETECTION -> startBrightnessDetection()
            CameraOperationType.QR_CODE_SCANNING -> showQrCodeInfo()
            CameraOperationType.COLOR_DETECTION -> startColorDetection()
            else -> {}
        }
    }

    private fun showImageAnalysis() {
        val info = """
            Image Analysis allows real-time processing of camera frames.

            Common use cases:
            • Object detection
            • Face recognition
            • Text recognition (OCR)
            • Barcode/QR code scanning
            • Custom ML model inference

            CameraX provides ImageAnalysis use case
            with configurable resolution and backpressure strategy.
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.image_analysis)
            .setMessage(info)
            .setPositiveButton(R.string.close, null)
            .show()
    }

    private fun startBrightnessDetection() {
        if (isAnalyzing) {
            stopAnalysis()
            return
        }

        isAnalyzing = true

        // Initialize camera with image analysis
        cameraHelper.startCamera(
            previewView = null,
            lifecycleOwner = viewLifecycleOwner,
            enableImageAnalysis = true
        )

        val textView = TextView(requireContext()).apply {
            text = getString(R.string.analyzing_brightness)
            textSize = 24f
            setPadding(48, 48, 48, 48)
            setTextColor(Color.BLACK)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        analysisDialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.brightness_detection)
            .setView(textView)
            .setNegativeButton(R.string.stop) { _, _ ->
                stopAnalysis()
            }
            .setCancelable(false)
            .create()

        analysisDialog?.show()

        // Set brightness analyzer
        cameraHelper.setImageAnalyzer(BrightnessAnalyzer { brightness ->
            activity?.runOnUiThread {
                val level = when {
                    brightness < 50 -> "Dark"
                    brightness < 150 -> "Normal"
                    else -> "Bright"
                }
                textView.text = getString(R.string.brightness_level, brightness.toInt(), level)
            }
        })
    }

    private fun startColorDetection() {
        if (isAnalyzing) {
            stopAnalysis()
            return
        }

        isAnalyzing = true

        // Initialize camera with image analysis
        cameraHelper.startCamera(
            previewView = null,
            lifecycleOwner = viewLifecycleOwner,
            enableImageAnalysis = true
        )

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            gravity = android.view.Gravity.CENTER
        }

        val colorView = View(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                gravity = android.view.Gravity.CENTER
            }
            setBackgroundColor(Color.GRAY)
        }

        val textView = TextView(requireContext()).apply {
            text = getString(R.string.detecting_color)
            textSize = 16f
            setPadding(0, 32, 0, 0)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        container.addView(colorView)
        container.addView(textView)

        analysisDialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.color_detection)
            .setView(container)
            .setNegativeButton(R.string.stop) { _, _ ->
                stopAnalysis()
            }
            .setCancelable(false)
            .create()

        analysisDialog?.show()

        // Set color analyzer
        cameraHelper.setImageAnalyzer(ColorAnalyzer { color ->
            activity?.runOnUiThread {
                colorView.setBackgroundColor(color)
                val hexColor = String.format("#%06X", (0xFFFFFF and color))
                textView.text = getString(R.string.dominant_color, hexColor)
            }
        })
    }

    private fun showQrCodeInfo() {
        val info = """
            QR Code Scanning requires ML Kit or
            a barcode scanning library.

            Recommended libraries:
            • Google ML Kit Barcode Scanning
            • ZXing (Zebra Crossing)
            • Google Code Scanner API

            CameraX ImageAnalysis can be used
            with these libraries for real-time scanning.
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.qr_code_scanning)
            .setMessage(info)
            .setPositiveButton(R.string.close, null)
            .show()
    }

    private fun stopAnalysis() {
        cameraHelper.clearImageAnalyzer()
        isAnalyzing = false
        analysisDialog?.dismiss()
        analysisDialog = null
    }

    private fun showMessage(message: String) {
        (requireActivity() as MainActivity).showSnackbar(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAnalysis()
        _binding = null
    }

    /**
     * Brightness analyzer - calculates average luminance
     */
    private class BrightnessAnalyzer(
        private val onBrightnessDetected: (Float) -> Unit
    ) : ImageAnalysis.Analyzer {

        override fun analyze(image: ImageProxy) {
            val buffer = image.planes[0].buffer
            val width = image.width
            val height = image.height

            var totalBrightness = 0L
            var sampledCount = 0

            // Sample pixels (every 10th pixel for performance)
            val step = 10

            for (y in 0 until height step step) {
                for (x in 0 until width step step) {
                    val index = (y * width + x)
                    if (index < buffer.remaining()) {
                        buffer.position(index)
                        val luminance = buffer.get().toInt() and 0xFF
                        totalBrightness += luminance
                        sampledCount++
                    }
                }
            }

            val avgBrightness = if (sampledCount > 0) {
                totalBrightness.toFloat() / sampledCount
            } else {
                0f
            }

            onBrightnessDetected(avgBrightness)
            image.close()
        }
    }

    /**
     * Color analyzer - detects dominant color
     */
    private class ColorAnalyzer(
        private val onColorDetected: (Int) -> Unit
    ) : ImageAnalysis.Analyzer {

        override fun analyze(image: ImageProxy) {
            val buffer = image.planes[0].buffer
            val width = image.width
            val height = image.height

            var totalR = 0L
            var totalG = 0L
            var totalB = 0L
            var sampledCount = 0

            // Sample center region
            val centerX = width / 2
            val centerY = height / 2
            val sampleSize = 50

            for (y in (centerY - sampleSize) until (centerY + sampleSize) step 5) {
                for (x in (centerX - sampleSize) until (centerX + sampleSize) step 5) {
                    if (x >= 0 && x < width && y >= 0 && y < height) {
                        val index = (y * width + x) * 4 // RGBA
                        if (index + 2 < buffer.remaining()) {
                            buffer.position(index)
                            val r = buffer.get().toInt() and 0xFF
                            val g = buffer.get().toInt() and 0xFF
                            val b = buffer.get().toInt() and 0xFF
                            totalR += r
                            totalG += g
                            totalB += b
                            sampledCount++
                        }
                    }
                }
            }

            if (sampledCount > 0) {
                val avgR = (totalR / sampledCount).toInt()
                val avgG = (totalG / sampledCount).toInt()
                val avgB = (totalB / sampledCount).toInt()
                val color = Color.rgb(avgR, avgG, avgB)
                onColorDetected(color)
            }

            image.close()
        }
    }
}
