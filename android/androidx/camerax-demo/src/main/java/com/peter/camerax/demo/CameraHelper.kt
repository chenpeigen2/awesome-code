package com.peter.camerax.demo

import android.content.Context
import android.util.Size
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * CameraX helper class
 * Encapsulates camera preview, capture, video recording, and image analysis
 */
class CameraHelper(private val context: Context) {

    private var cameraProvider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var camera: Camera? = null
    private var recording: Recording? = null

    private var isFrontCamera = false
    private var currentFlashMode = ImageCapture.FLASH_MODE_OFF

    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    /**
     * Initialize camera provider
     */
    fun initCameraProvider(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Start camera preview
     */
    fun startCamera(
        previewView: PreviewView?,
        lifecycleOwner: LifecycleOwner,
        enableVideoCapture: Boolean = false,
        enableImageAnalysis: Boolean = false
    ) {
        val provider = cameraProvider ?: return

        // Unbind all use cases
        provider.unbindAll()

        // Build use cases list
        val useCases = mutableListOf<UseCase>()

        // Create preview
        preview = Preview.Builder()
            .build()
            .also {
                if (previewView != null) {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                useCases.add(it)
            }

        // Create image capture
        imageCapture = ImageCapture.Builder()
            .setFlashMode(currentFlashMode)
            .build()
            .also { useCases.add(it) }

        // Select camera
        val cameraSelector = if (isFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        // Add video capture if needed
        if (enableVideoCapture) {
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)
            useCases.add(videoCapture!!)
        }

        // Add image analysis if needed
        if (enableImageAnalysis) {
            imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            useCases.add(imageAnalysis!!)
        }

        // Bind to lifecycle
        try {
            camera = provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                *useCases.toTypedArray()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Stop camera
     */
    fun stopCamera() {
        cameraProvider?.unbindAll()
        preview = null
        imageCapture = null
        videoCapture = null
        imageAnalysis = null
        camera = null
    }

    /**
     * Switch between front and back camera
     * @return true if now using front camera
     */
    fun switchCamera(): Boolean {
        isFrontCamera = !isFrontCamera
        return isFrontCamera
    }

    /**
     * Check if currently using front camera
     */
    fun isFrontCamera(): Boolean = isFrontCamera

    /**
     * Take picture and get ImageProxy
     */
    fun takePicture(
        onSuccess: (androidx.camera.core.ImageProxy) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val capture = imageCapture ?: run {
            onError(IllegalStateException("ImageCapture not initialized"))
            return
        }

        capture.takePicture(
            cameraExecutor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                    onSuccess(image)
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            }
        )
    }

    /**
     * Take picture and save to file
     */
    fun takePictureToFile(
        file: File,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val capture = imageCapture ?: run {
            onError(IllegalStateException("ImageCapture not initialized"))
            return
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        capture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onSuccess()
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            }
        )
    }

    /**
     * Start video recording
     */
    fun startRecording(
        file: File,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val capture = videoCapture ?: run {
            onError(IllegalStateException("VideoCapture not initialized"))
            return
        }

        try {
            recording = capture.output
                .prepareRecording(context, FileOutputOptions.Builder(file).build())
                .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                    when (recordEvent) {
                        is VideoRecordEvent.Start -> {
                            onSuccess()
                        }
                        is VideoRecordEvent.Finalize -> {
                            if (!recordEvent.hasError()) {
                                onSuccess()
                            } else {
                                onError(Exception(recordEvent.cause?.message ?: "Unknown error"))
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            onError(e)
        }
    }

    /**
     * Start video recording with audio
     */
    fun startRecordingWithAudio(
        file: File,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val capture = videoCapture ?: run {
            onError(IllegalStateException("VideoCapture not initialized"))
            return
        }

        try {
            recording = capture.output
                .prepareRecording(context, FileOutputOptions.Builder(file).build())
                .withAudioEnabled()
                .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                    when (recordEvent) {
                        is VideoRecordEvent.Start -> {
                            onSuccess()
                        }
                        is VideoRecordEvent.Finalize -> {
                            if (!recordEvent.hasError()) {
                                onSuccess()
                            } else {
                                onError(Exception(recordEvent.cause?.message ?: "Unknown error"))
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            onError(e)
        }
    }

    /**
     * Stop video recording
     */
    fun stopRecording() {
        recording?.stop()
        recording = null
    }

    /**
     * Check if recording
     */
    fun isRecording(): Boolean = recording != null

    /**
     * Set flash mode
     */
    fun setFlashMode(mode: Int) {
        currentFlashMode = mode
        imageCapture?.flashMode = mode
    }

    /**
     * Get current flash mode
     */
    fun getFlashMode(): Int = currentFlashMode

    /**
     * Set zoom ratio
     */
    fun setZoomRatio(ratio: Float) {
        camera?.cameraControl?.setZoomRatio(ratio)
    }

    /**
     * Get current zoom ratio
     */
    fun getZoomRatio(): Float = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1.0f

    /**
     * Get max zoom ratio
     */
    fun getMaxZoomRatio(): Float = camera?.cameraInfo?.zoomState?.value?.maxZoomRatio ?: 1.0f

    /**
     * Set image analyzer
     */
    fun setImageAnalyzer(analyzer: ImageAnalysis.Analyzer) {
        imageAnalysis?.setAnalyzer(cameraExecutor, analyzer)
    }

    /**
     * Clear image analyzer
     */
    fun clearImageAnalyzer() {
        imageAnalysis?.clearAnalyzer()
    }

    /**
     * Check if camera is available
     */
    fun isCameraAvailable(): Boolean = cameraProvider != null

    /**
     * Check if flash is available
     */
    fun hasFlash(): Boolean = camera?.cameraInfo?.hasFlashUnit() ?: false

    /**
     * Get camera info
     */
    fun getCameraInfo(): CameraInfo? {
        val cam = camera ?: return null
        return CameraInfo(
            isFrontCamera = isFrontCamera,
            hasFlash = cam.cameraInfo.hasFlashUnit(),
            zoomRatio = cam.cameraInfo.zoomState.value?.zoomRatio ?: 1.0f,
            maxZoomRatio = cam.cameraInfo.zoomState.value?.maxZoomRatio ?: 1.0f
        )
    }

    /**
     * Release resources
     */
    fun release() {
        stopCamera()
        cameraExecutor.shutdown()
    }
}

/**
 * Camera info data class
 */
data class CameraInfo(
    val isFrontCamera: Boolean,
    val hasFlash: Boolean,
    val zoomRatio: Float,
    val maxZoomRatio: Float
)
