package com.example.client;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 演示渲染View
 * 
 * 动画彩色圆环，用于展示跨进程渲染效果
 */
public class DemoRenderView extends View {
    private static final String TAG = "DemoRenderView";

    private Paint mCirclePaint;
    private Paint mTextPaint;
    private Paint mArcPaint;
    private RectF mArcRect;
    private float mSweepAngle = 0;
    private ValueAnimator mAnimator;
    private int mColorIndex = 0;
    private String mLabel = "Client";

    private static final int[] COLORS = {
            Color.parseColor("#FF6B6B"),
            Color.parseColor("#4ECDC4"),
            Color.parseColor("#45B7D1"),
            Color.parseColor("#96CEB4"),
            Color.parseColor("#FFEAA7"),
            Color.parseColor("#DDA0DD")
    };

    public DemoRenderView(Context context) {
        super(context);
        init();
    }

    public DemoRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(40f);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(16f);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);

        mArcRect = new RectF();

        Log.d(TAG, "init: PID=" + android.os.Process.myPid());
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    private void startAnimation() {
        if (mAnimator != null && mAnimator.isRunning()) return;

        mAnimator = ValueAnimator.ofFloat(0, 360);
        mAnimator.setDuration(2000);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(animation -> {
            mSweepAngle = (float) animation.getAnimatedValue();
            if (mSweepAngle == 0) {
                mColorIndex = (mColorIndex + 1) % COLORS.length;
            }
            invalidate();
        });
        mAnimator.start();
    }

    private void stopAnimation() {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;

        int bgColor = COLORS[mColorIndex];
        canvas.drawColor(darkenColor(bgColor, 0.3f));

        int radius = Math.min(width, height) / 3;
        mCirclePaint.setColor(bgColor);
        canvas.drawCircle(centerX, centerY, radius, mCirclePaint);

        int padding = 24;
        mArcRect.set(
                centerX - radius + padding,
                centerY - radius + padding,
                centerX + radius - padding,
                centerY + radius - padding
        );

        mArcPaint.setColor(Color.WHITE);
        canvas.drawArc(mArcRect, -90, mSweepAngle, false, mArcPaint);

        canvas.drawText(mLabel, centerX, centerY - 10, mTextPaint);

        mTextPaint.setTextSize(24f);
        canvas.drawText("PID: " + android.os.Process.myPid(), centerX, centerY + 30, mTextPaint);
        mTextPaint.setTextSize(40f);
    }

    private int darkenColor(int color, float factor) {
        int r = (int) (Color.red(color) * (1 - factor));
        int g = (int) (Color.green(color) * (1 - factor));
        int b = (int) (Color.blue(color) * (1 - factor));
        return Color.rgb(r, g, b);
    }
}
