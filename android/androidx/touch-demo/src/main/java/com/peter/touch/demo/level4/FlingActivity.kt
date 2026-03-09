package com.peter.touch.demo.level4

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller
import com.peter.touch.demo.BaseTouchActivity
import com.peter.touch.demo.R
import com.peter.touch.demo.databinding.ActivityFlingBinding
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Level 4.4: Fling 惯性滑动
 *
 * 演示内容：
 * - VelocityTracker：速度追踪
 * - Scroller：惯性滑动计算
 * - computeScroll()：驱动动画
 */
class FlingActivity : BaseTouchActivity() {

    private lateinit var binding: ActivityFlingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDescription()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.level4_fling_title)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupDescription() {
        binding.tvDescription.text = """
            |Fling 惯性滑动演示：
            |
            |• 在下方区域快速滑动并抬起
            |• 观察「小球」的惯性滚动效果
            |• 查看日志面板中的速度和位置变化
            |
            |核心 API：
            |• VelocityTracker - 追踪手指滑动速度
            |• Scroller - 计算惯性滑动轨迹
            |• computeScroll() - 驱动动画循环
        """.trimMargin()
    }
}

/**
 * 可视化 Fling 效果的 View
 * 绘制一个可拖动并带有惯性效果的小球
 */
class FlingDemoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val velocityTracker: VelocityTracker = VelocityTracker.obtain()
    private val scroller: Scroller = Scroller(context)

    // 小球位置
    private var ballX = 0f
    private var ballY = 0f
    private val ballRadius = 40f

    // 拖动状态
    private var lastX = 0f
    private var lastY = 0f
    private var isDragging = false

    // 绘制
    private val ballPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2196F3")
        style = Paint.Style.FILL
    }
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40000000")
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }

    // 日志回调
    var onLog: ((tag: String, method: String, action: String, result: String) -> Unit)? = null

    private val maxFlingVelocity = ViewConfiguration.getMaximumFlingVelocity().toFloat()
    private val minFlingVelocity = ViewConfiguration.getMinimumFlingVelocity().toFloat()

    init {
        // 设置可点击以接收触摸事件
        isClickable = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 初始化小球位置到中心
        if (ballX == 0f && ballY == 0f) {
            ballX = w / 2f
            ballY = h / 2f
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        velocityTracker.addMovement(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // 停止正在进行的 fling
                if (!scroller.isFinished) {
                    scroller.abortAnimation()
                    onLog?.invoke("Scroller", "abortAnimation", "停止惯性动画", "")
                }

                lastX = event.x
                lastY = event.y
                isDragging = true

                onLog?.invoke("Touch", "ACTION_DOWN", "手指按下", "(${event.x.toInt()}, ${event.y.toInt()})")
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastX
                val dy = event.y - lastY

                if (abs(dx) > 1 || abs(dy) > 1) {
                    // 移动小球
                    ballX = (ballX + dx).coerceIn(ballRadius, width - ballRadius)
                    ballY = (ballY + dy).coerceIn(ballRadius, height - ballRadius)

                    lastX = event.x
                    lastY = event.y
                    invalidate()
                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                isDragging = false

                // 计算速度
                velocityTracker.computeCurrentVelocity(1000, maxFlingVelocity)
                val velocityX = velocityTracker.xVelocity
                val velocityY = velocityTracker.yVelocity

                val speed = kotlin.math.sqrt(velocityX * velocityX + velocityY * velocityY)
                onLog?.invoke("Velocity", "computeCurrentVelocity", "计算速度", "vx=${velocityX.toInt()}, vy=${velocityY.toInt()}, speed=${speed.toInt()}")

                // 如果速度足够快，启动 fling
                if (speed > minFlingVelocity) {
                    startFling(-velocityX.toInt(), -velocityY.toInt())
                } else {
                    onLog?.invoke("Fling", "skip", "速度不足", "需要 > $minFlingVelocity")
                    logSeparator()
                }

                velocityTracker.clear()
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                velocityTracker.clear()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    private fun startFling(velocityX: Int, velocityY: Int) {
        val startX = ballX.toInt()
        val startY = ballY.toInt()

        // 边界限制
        val minX = ballRadius.toInt()
        val maxX = (width - ballRadius).toInt()
        val minY = ballRadius.toInt()
        val maxY = (height - ballRadius).toInt()

        scroller.fling(
            startX, startY,
            velocityX, velocityY,
            minX, maxX,
            minY, maxY
        )

        onLog?.invoke("Scroller", "fling", "启动惯性滑动", "vx=$velocityX, vy=$velocityY")

        // 触发 computeScroll
        invalidate()
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            // 更新小球位置
            ballX = scroller.currX.toFloat()
            ballY = scroller.currY.toFloat()

            // 持续重绘
            postInvalidateOnAnimation()
        } else if (!isDragging && scroller.isFinished) {
            // fling 结束
            onLog?.invoke("Scroller", "computeScroll", "惯性滑动结束", "(${ballX.toInt()}, ${ballY.toInt()})")
            logSeparator()
        }
    }

    private fun logSeparator() {
        onLog?.invoke("───", "──────────────────────", "", "")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 绘制网格背景
        drawGrid(canvas)

        // 绘制阴影
        canvas.drawCircle(ballX + 4, ballY + 4, ballRadius, shadowPaint)

        // 绘制小球
        canvas.drawCircle(ballX, ballY, ballRadius, ballPaint)

        // 绘制中心点
        canvas.drawCircle(ballX, ballY, 4f, textPaint)
    }

    private fun drawGrid(canvas: Canvas) {
        val gridPaint = Paint().apply {
            color = Color.parseColor("#10000000")
            strokeWidth = 1f
        }

        val step = 50
        for (x in 0..width step step) {
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), gridPaint)
        }
        for (y in 0..height step step) {
            canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), gridPaint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        velocityTracker.recycle()
    }
}