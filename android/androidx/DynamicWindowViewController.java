package com.jd.systemui.car.window;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.SurfaceControl;
import android.view.SurfaceControlViewHost;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import android.util.Log;

import com.android.systemui.car.window.OverlayViewController;
import com.android.systemui.car.window.OverlayViewGlobalStateController;
import com.jd.systemui.car.ipc.data.JdWindowConfig;

/**
 * A controller that manages a dynamic window rendered via SurfaceControlViewHost.
 */
public class DynamicWindowViewController extends OverlayViewController {
    private static final String TAG = "JJJJJJDynamicWindowVC";
    
    // Layer calculation constants
    // Layer = LAYER_BASE + type * LAYER_TYPE_MULTIPLIER + priority
    // This ensures different types have non-overlapping layer ranges
    private static final int LAYER_BASE = 100000;
    private static final int LAYER_TYPE_MULTIPLIER = 10000;
    
    /**
     * Callback interface for window state changes.
     * This allows the Mediator to be notified when the view is hidden
     * through any path (not just through the Mediator's hideWindow method).
     */
    public interface OnStateChangeListener {
        /**
         * Called when this controller's view is hidden.
         * @param controller The controller that was hidden
         */
        void onHidden(DynamicWindowViewController controller);
        
        /**
         * Called when this controller's view is detached from window.
         * @param controller The controller that was detached
         */
        void onDetached(DynamicWindowViewController controller);

//        default void onSurfaceInitialized(DynamicWindowViewController controller) {
//        }
    }
    
    private final JdWindowConfig mConfig;
    private final SurfaceControlViewHost.SurfacePackage mSurfacePackage;
    private final int mType;
    private final int mPriority;
    
    private SurfaceView mSurfaceView;
    private OnStateChangeListener mStateChangeListener;
    private boolean mIsHidden = false;
    private boolean mLayerApplied = false;
    private int mInsertionIndex = -1;

    public DynamicWindowViewController(
            OverlayViewGlobalStateController globalStateController,
            JdWindowConfig config,
            SurfaceControlViewHost.SurfacePackage surfacePackage,
            int type,
            int priority) {
        super(0, globalStateController); // stubId is not used
        mConfig = config;
        mSurfacePackage = surfacePackage;
        mType = type;
        mPriority = priority;
    }

    /**
     * Sets the insertion index for the view in the parent layout.
     * Must be called before inflate().
     * @param index The index to insert the view at, or -1 to add to end.
     */
    public void setInsertionIndex(int index) {
        mInsertionIndex = index;
    }

    @Override
    public void inflate(ViewGroup baseLayout) {
        Context context = baseLayout.getContext();
        mSurfaceView = new SurfaceView(context);

        // Z-Order handling:
        // 1. setZOrderOnTop(true) to ensure Surface renders above normal Views
        // 2. Use SurfaceControl.Transaction.setLayer() to control Z-order among Surfaces
        // 3. View order in ViewGroup controls touch event dispatch order
        mSurfaceView.setZOrderOnTop(true);
        //mSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        
        // Listen for Surface creation to apply layer
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "surfaceCreated for type=" + mType + ", priority=" + mPriority);
                applyZOrder();

                // CRITICAL FIX: When Surface is recreated (e.g. after detach/attach),
                Log.d(TAG, "surfaceCreated for type=" + mType + ", priority=" + mPriority);
                applyZOrder();

                // CRITICAL Fix ...
                if (mSurfacePackage != null) {
                    mSurfaceView.setChildSurfacePackage(mSurfacePackage);
                    Log.d(TAG, "surfaceCreated: re-attached SurfacePackage");
                }
//                if (mStateChangeListener != null) {
//                    mStateChangeListener.onSurfaceInitialized(DynamicWindowViewController.this);
//                }
            }
            
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG, "surfaceChanged: width=" + width + ", height=" + height + ", format=" + format);
                // Re-apply layer on surface change if needed
                if (!mLayerApplied) {
                    applyZOrder();
                }
            }
            
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "surfaceDestroyed");
                mLayerApplied = false;
            }
        });
        
        //mSurfaceView.setChildSurfacePackage(mSurfacePackage);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                mConfig.width, mConfig.height, mConfig.gravity);
        lp.leftMargin = mConfig.x;
        lp.topMargin = mConfig.y;

        mSurfaceView.setLayoutParams(lp);

        if (mInsertionIndex >= 0) {
            baseLayout.addView(mSurfaceView, mInsertionIndex);
            Log.d(TAG, "inflate: added view at index " + mInsertionIndex);
        } else {
            baseLayout.addView(mSurfaceView);
            Log.d(TAG, "inflate: added view at end");
        }
        
        // Assign to protected mLayout in parent
        mLayout = mSurfaceView;
        
        onFinishInflate();
    }

    @Override
    protected boolean shouldFocusWindow() {
        return mConfig.focusable;
    }

    @Override
    protected boolean shouldShowStatusBarInsets() {
        // Respect config or default?
        return false;
    }

    @Override
    protected boolean shouldShowNavigationBarInsets() {
        return true;
    }
    
    public void updateSurfacePackage(SurfaceControlViewHost.SurfacePackage newPackage) {
        if (mSurfaceView != null) {
            mSurfaceView.setChildSurfacePackage(newPackage);
        }
    }
    
    /**
     * Apply Z-Order to the Surface using SurfaceControl.Transaction.
     * Layer value is calculated as: LAYER_BASE + type * LAYER_TYPE_MULTIPLIER + priority
     * 
     * Example layer values:
     * - TYPE_APP_NORMAL(1), priority=0   -> 110000
     * - TYPE_APP_NORMAL(1), priority=100 -> 110100
     * - TYPE_BELOW_BARS(2), priority=0   -> 120000
     * - TYPE_OVERLAY_TOP(3), priority=50 -> 130050
     * - TYPE_SYSTEM_CRITICAL(4), priority=0 -> 140000
     */
    public void applyZOrder() {
        if (mLayerApplied) {
            Log.d(TAG, "applyZOrder: layer already applied");
            return;
        }
        
        if (mSurfaceView == null) {
            Log.w(TAG, "applyZOrder: mSurfaceView is null");
            return;
        }
        
        SurfaceControl surfaceControl = mSurfaceView.getSurfaceControl();
        if (surfaceControl == null || !surfaceControl.isValid()) {
            Log.w(TAG, "applyZOrder: SurfaceControl is null or invalid");
            return;
        }
        
        int layerValue = calculateLayer();
        Log.d(TAG, "applyZOrder: setting layer=" + layerValue + " (type=" + mType + ", priority=" + mPriority + ")");
        
        try {
            new SurfaceControl.Transaction()
                    .setLayer(surfaceControl, layerValue)
                    .apply();
            mLayerApplied = true;
            Log.d(TAG, "applyZOrder: layer applied successfully");
        } catch (Exception e) {
            Log.e(TAG, "applyZOrder: failed to set layer", e);
        }
    }
    
    private int calculateLayer() {
        return LAYER_BASE + mType * LAYER_TYPE_MULTIPLIER + mPriority;
    }
    
    /**
     * Get the type of this window.
     */
    public int getType() {
        return mType;
    }
    
    /**
     * Get the priority of this window.
     */
    public int getPriority() {
        return mPriority;
    }
    
    /**
     * Sets the state change listener.
     * @param listener The listener to notify on state changes
     */
    public void setOnStateChangeListener(OnStateChangeListener listener) {
        mStateChangeListener = listener;
    }
    
    @Override
    protected void showInternal() {
        super.showInternal();
        Log.d(TAG, "showInternal: visibility set to VISIBLE, mSurfaceView=" + mSurfaceView);
        // Note: applyZOrder is triggered in surfaceCreated callback, no need to call here
    }
    
    /**
     * Bridge method for DynamicWindowMediator to trigger show.
     * This is needed because showInternal() is protected and cannot be 
     * directly referenced by method reference from Mediator.
     */
    void triggerShow() {
        if (mLayout != null) {
            showInternal();
        }
    }
    
    @Override
    protected void hideInternal() {
        Log.d(TAG, "hideInternal called, mIsHidden=" + mIsHidden);
        if (mIsHidden) {
            return; // Prevent duplicate callbacks
        }
        mIsHidden = true;
        super.hideInternal();
        
        // Notify listener that this view was hidden
        if (mStateChangeListener != null) {
            Log.d(TAG, "Notifying listener onHidden");
            mStateChangeListener.onHidden(this);
        }
    }
    
    /**
     * Called when the view is attached to window.
     * Sets up a listener for detach events.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        
        if (mSurfaceView != null) {
            mSurfaceView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    Log.d(TAG, "View attached to window");
                }
                
                @Override
                public void onViewDetachedFromWindow(View v) {
                    Log.d(TAG, "View detached from window, mIsHidden=" + mIsHidden);
                    // Notify listener if we're being detached without going through hideInternal
                    if (!mIsHidden && mStateChangeListener != null) {
                        Log.d(TAG, "Notifying listener onDetached (unexpected detach)");
                        mStateChangeListener.onDetached(DynamicWindowViewController.this);
                    }
                }
            });
        }
    }
    
    /**
     * Resets the hidden state. Called when the view is being reused or shown again.
     */
    public void resetState() {
        mIsHidden = false;
    }
    
    /**
     * Returns whether this view has been hidden.
     */
    public boolean isHidden() {
        return mIsHidden;
    }
}
