package com.peter.androidx;

import static android.opengl.ETC1.getHeight;
import static android.opengl.ETC1.getWidth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CircleSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder mHolder;
    private volatile boolean mIsDrawing; // 控制绘制线程的标志
    private Paint mPaint;
    private int mCenterX, mCenterY, mRadius;

    public CircleSurfaceView(Context context) {
        super(context);
        init();
    }

    public CircleSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);

        mPaint = new Paint();
        mPaint.setColor(Color.RED); // 设置画笔颜色
        mPaint.setStyle(Paint.Style.FILL); // 设置填充模式
        mPaint.setAntiAlias(true); // 抗锯齿

        // 可选：设置透明背景
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawing = true;
        // 获取View的宽高，确定圆心和半径
        mCenterX = getWidth() / 2;
        mCenterY = getHeight() / 2;
        mRadius = Math.min(mCenterX, mCenterY) - 20; // 半径留些边距
        new Thread(this).start(); // 启动绘制线程
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Surface尺寸改变时调用
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false; // 停止绘制线程
    }

    @Override
    public void run() {
        while (mIsDrawing) {
            drawCircle();
            try {
                Thread.sleep(16); // 约60帧/秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawCircle() {
        Canvas canvas = mHolder.lockCanvas();
        if (canvas != null) {
            try {
                // 清空画布（透明背景）
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                // 绘制圆形
                canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
                canvas.drawRoundRect(0, 0, getWidth(), getHeight(),40f,30f,mPaint);
            } finally {
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
