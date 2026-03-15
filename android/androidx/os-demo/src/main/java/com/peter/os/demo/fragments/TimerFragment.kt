package com.peter.os.demo.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.os.demo.R
import com.peter.os.demo.databinding.FragmentTimerBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * CountDownTimer/SystemClock 计时器示例
 * 
 * CountDownTimer: 倒计时器
 * - start(): 开始倒计时
 * - cancel(): 取消倒计时
 * - onTick(): 每隔一段时间回调
 * - onFinish(): 倒计时结束回调
 * 
 * SystemClock: 系统时钟
 * - elapsedRealtime(): 系统运行时间（包含休眠）
 * - uptimeMillis(): 开机时间（不包含休眠）
 * - currentThreadTimeMillis(): 当前线程 CPU 时间
 * - currentTimeMillis(): 当前时间戳
 */
class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    
    private var countDownTimer: CountDownTimer? = null
    private var totalTime = 60000L // 默认60秒
    private var isRunning = false
    private var isPaused = false
    private var remainingTime = 60000L
    
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        updateClockInfo()
    }

    private fun setupViews() {
        // 设置倒计时时间
        binding.sliderDuration.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                totalTime = (value * 1000).toLong()
                remainingTime = totalTime
                updateTimerDisplay(remainingTime)
                binding.tvDurationLabel.text = "倒计时: ${value.toInt()} 秒"
            }
        }
        binding.sliderDuration.value = 60f

        // 开始按钮
        binding.btnStart.setOnClickListener {
            if (isPaused) {
                resumeTimer()
            } else {
                startTimer()
            }
        }

        // 暂停按钮
        binding.btnPause.setOnClickListener {
            pauseTimer()
        }

        // 重置按钮
        binding.btnReset.setOnClickListener {
            resetTimer()
        }

        // 刷新时钟信息
        binding.btnRefreshClock.setOnClickListener {
            updateClockInfo()
        }

        updateTimerDisplay(totalTime)
    }

    private fun startTimer() {
        countDownTimer?.cancel()
        
        countDownTimer = object : CountDownTimer(remainingTime, 100) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateTimerDisplay(millisUntilFinished)
                
                // 更新进度条
                val progress = ((totalTime - remainingTime) * 100 / totalTime).toInt()
                binding.progressIndicator.progress = progress
            }

            override fun onFinish() {
                isRunning = false
                isPaused = false
                remainingTime = 0
                binding.tvTimerStatus.text = getString(R.string.timer_finished)
                binding.progressIndicator.progress = 100
                updateButtons()
            }
        }.start()
        
        isRunning = true
        isPaused = false
        binding.tvTimerStatus.text = getString(R.string.timer_running)
        updateButtons()
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isRunning = false
        isPaused = true
        binding.tvTimerStatus.text = getString(R.string.timer_paused)
        updateButtons()
    }

    private fun resumeTimer() {
        startTimer()
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        isRunning = false
        isPaused = false
        remainingTime = totalTime
        binding.progressIndicator.progress = 0
        binding.tvTimerStatus.text = getString(R.string.timer_stopped)
        updateTimerDisplay(totalTime)
        updateButtons()
    }

    private fun updateTimerDisplay(millis: Long) {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        val millisPart = (millis % 1000) / 10
        
        binding.tvTimer.text = String.format("%02d:%02d.%02d", minutes, seconds, millisPart)
    }

    private fun updateButtons() {
        when {
            isRunning -> {
                binding.btnStart.isEnabled = false
                binding.btnPause.isEnabled = true
                binding.btnReset.isEnabled = true
            }
            isPaused -> {
                binding.btnStart.isEnabled = true
                binding.btnStart.text = "继续"
                binding.btnPause.isEnabled = false
                binding.btnReset.isEnabled = true
            }
            else -> {
                binding.btnStart.isEnabled = true
                binding.btnStart.text = getString(R.string.timer_start)
                binding.btnPause.isEnabled = false
                binding.btnReset.isEnabled = true
            }
        }
    }

    private fun updateClockInfo() {
        val sb = StringBuilder()
        
        // 系统运行时间（包含休眠）
        val elapsedRealtime = SystemClock.elapsedRealtime()
        sb.appendLine("=== elapsedRealtime ===")
        sb.appendLine("值: $elapsedRealtime ms")
        sb.appendLine("格式: ${formatDuration(elapsedRealtime)}")
        sb.appendLine("(包含休眠时间的系统运行时长)")
        sb.appendLine()
        
        // 开机时间（不包含休眠）
        val uptimeMillis = SystemClock.uptimeMillis()
        sb.appendLine("=== uptimeMillis ===")
        sb.appendLine("值: $uptimeMillis ms")
        sb.appendLine("格式: ${formatDuration(uptimeMillis)}")
        sb.appendLine("(不包含休眠时间的运行时长)")
        sb.appendLine()
        
        // 当前线程 CPU 时间
        val threadTime = SystemClock.currentThreadTimeMillis()
        sb.appendLine("=== currentThreadTimeMillis ===")
        sb.appendLine("值: $threadTime ms")
        sb.appendLine("格式: ${formatDuration(threadTime)}")
        sb.appendLine("(当前线程的 CPU 时间)")
        sb.appendLine()
        
        // 当前时间戳
        val currentTime = System.currentTimeMillis()
        sb.appendLine("=== currentTimeMillis ===")
        sb.appendLine("值: $currentTime ms")
        sb.appendLine("格式: ${timeFormat.format(Date(currentTime))}")
        sb.appendLine("(Unix 时间戳)")
        sb.appendLine()
        
        // 时间差
        sb.appendLine("=== 时间对比 ===")
        sb.appendLine("休眠时间差: ${formatDuration(elapsedRealtime - uptimeMillis)}")
        
        binding.tvClockInfo.text = sb.toString()
    }

    private fun formatDuration(millis: Long): String {
        val days = TimeUnit.MILLISECONDS.toDays(millis)
        val hours = TimeUnit.MILLISECONDS.toHours(millis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        
        return when {
            days > 0 -> String.format("%dd %02d:%02d:%02d", days, hours, minutes, seconds)
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }

    companion object {
        fun newInstance() = TimerFragment()
    }
}
