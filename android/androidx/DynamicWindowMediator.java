package com.jd.systemui.car.window;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.SurfaceControlViewHost;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.android.systemui.car.window.OverlayViewController;
import com.android.systemui.car.window.OverlayViewGlobalStateController;
import com.android.systemui.car.window.OverlayViewMediator;
import com.android.systemui.car.window.SystemUIOverlayWindowController;
import com.android.systemui.dagger.SysUISingleton;
import com.jd.systemui.car.ipc.data.JdWindowConfig;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

@SysUISingleton
public class DynamicWindowMediator implements OverlayViewMediator,
        DynamicWindowViewController.OnStateChangeListener {
    private static final String TAG = "JJJJJJDynamicWindowMediator";
    
    private final Context mContext;
    private final OverlayViewGlobalStateController mGlobalStateController;
    private final SystemUIOverlayWindowController mWindowController;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    // windowId -> WindowRecord
    private final Map<String, WindowRecord> mWindows = new HashMap<>();
    // controller -> windowId (reverse mapping for callback lookup)
    private final Map<DynamicWindowViewController, String> mControllerToIdMap = new HashMap<>();
    
    private final int mScreenWidth;
    private final int mScreenHeight;

    // Band Constants
    public static final int TYPE_APP_NORMAL = 1;
    public static final int TYPE_BELOW_BARS = 2;
    public static final int TYPE_OVERLAY_TOP = 3;
    public static final int TYPE_SYSTEM_CRITICAL = 4;

    @Inject
    public DynamicWindowMediator(
            Context context,
            OverlayViewGlobalStateController globalStateController,
            SystemUIOverlayWindowController windowController) {
        mContext = context;
        mGlobalStateController = globalStateController;
        mWindowController = windowController;
        
        // Get screen dimensions
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }

    @Override
    public void registerListeners() {
        // No external listeners needed initially.
    }

    @Override
    public void setupOverlayContentViewControllers() {
        // No static controllers to setup.
    }

    /**
     * Returns the window token for creating SurfaceControlViewHost on the client side.
     * The client uses this token to embed its Surface into SystemUI's window hierarchy.
     *
     * @param windowId Unique identifier for the window (for logging/tracking)
     * @return IBinder token from SystemUI's overlay window, or null if window not attached
     */
    public IBinder requestHostToken(String windowId) {
        IBinder token = mWindowController.getWindowToken();
        if (token == null) {
            Log.w(TAG, "requestHostToken: window token is null for windowId=" + windowId 
                    + ", window may not be attached yet");
        } else {
            Log.d(TAG, "requestHostToken: returning valid token for windowId=" + windowId);
        }
        return token;
    }

    public void showWindow(String windowId, int type, int priority, JdWindowConfig config, SurfaceControlViewHost.SurfacePackage surfacePackage) {
        Log.d(TAG, "showWindow: windowId=" + windowId + ", type=" + type + ", priority=" + priority);
        mHandler.post(() -> handleShowWindow(windowId, type, priority, config, surfacePackage));
    }

    public void hideWindow(String windowId) {
        Log.d(TAG, "hideWindow: windowId=" + windowId);
        mHandler.post(() -> handleHideWindow(windowId));
    }

    /**
     * Hide all dynamic windows.
     * Called when IPC connection is disconnected (e.g., client process died or unbound).
     */
    public void hideAllWindows() {
        Log.d(TAG, "hideAllWindows: hiding " + mWindows.size() + " windows");
        mHandler.post(this::handleHideAllWindows);
    }

    private void handleHideAllWindows() {
        Log.d(TAG, "handleHideAllWindows: starting cleanup, mWindows.size=" + mWindows.size());
        
        // Step 1: Hide windows we know about in mWindows
        List<String> windowIds = new ArrayList<>(mWindows.keySet());
        for (String windowId : windowIds) {
            handleHideWindow(windowId);
        }
        
        // Step 2: Force cleanup any orphaned controllers from GlobalStateController
        // This handles the case where mWindows and mZOrderVisibleSortedMap are out of sync
        List<OverlayViewController> removedControllers = 
                mGlobalStateController.forceRemoveAllControllersOfType(DynamicWindowViewController.class);
        
        // Step 3: Remove views from parent for all removed controllers
        ViewGroup baseLayout = mWindowController.getBaseLayout();
        for (OverlayViewController controller : removedControllers) {
            View layout = controller.getLayout();
            if (layout != null && layout.getParent() == baseLayout) {
                baseLayout.removeView(layout);
                Log.d(TAG, "handleHideAllWindows: removed orphaned view from baseLayout");
            }
        }
        
        // Step 4: Clear our internal maps
        mWindows.clear();
        mControllerToIdMap.clear();
        
        // Step 5: Update touch modal state
        updateTouchModalState();
        
        Log.d(TAG, "handleHideAllWindows: completed, removed " + removedControllers.size() + " orphaned controllers");
    }

    private void handleShowWindow(String windowId, int type, int priority, JdWindowConfig config, SurfaceControlViewHost.SurfacePackage surfacePackage) {
        Log.d(TAG, "handleShowWindow: windowId=" + windowId + ", config=" + config + ", surfacePackage=" + surfacePackage);
        if (mWindows.containsKey(windowId)) {
            handleHideWindow(windowId);
        }

        DynamicWindowViewController controller = new DynamicWindowViewController(
                mGlobalStateController, config, surfacePackage, type, priority);
        
        // Set up state change listener to be notified if view is hidden externally
        controller.setOnStateChangeListener(this);

        WindowRecord record = new WindowRecord(windowId, type, priority, controller, config);
        
        // Calculate insertion index BEFORE adding to mWindows map, but we need to consider
        // all existing windows plus this new one's position relative to them.
        // Actually, calculateInsertionIndex needs to find where this NEW window fits 
        // among the EXISTING windows.
        ViewGroup baseLayout = mWindowController.getBaseLayout();
        int index = calculateInsertionIndex(baseLayout, type, priority);
        controller.setInsertionIndex(index);

        mWindows.put(windowId, record);
        mControllerToIdMap.put(controller, windowId);

        // Inflate (adds to specific index or top)
        mGlobalStateController.inflateView(controller);

        // Log parent window visibility and view status
        boolean isWindowVisible = mWindowController.isWindowVisible();
        View layout = controller.getLayout();
        int finalIndex = -1;
        if (layout != null && layout.getParent() == baseLayout) {
            finalIndex = baseLayout.indexOfChild(layout);
        }
        Log.d(TAG, "handleShowWindow: inflation complete. isWindowVisible=" + isWindowVisible 
                + ", finalIndex=" + finalIndex + ", childCount=" + baseLayout.getChildCount());

        // Pass triggerShow to properly set visibility (fixes window not appearing bug)
        mGlobalStateController.showView(controller, controller::triggerShow);
        
        Log.d(TAG, "handleShowWindow: showView called. isWindowVisible=" + mWindowController.isWindowVisible());

        // Update touch modal state based on window fullscreen status
        updateTouchModalState();

        // Timeout
        if (config.durationMs > 0) {
            mHandler.postDelayed(() -> handleHideWindow(windowId), config.durationMs);
        }
    }
    
    /**
     * Calculates the index where the new window view should be inserted in the baseLayout
     * to maintain the correct Z-order (consistent with type and priority).
     * 
     * @param baseLayout The parent ViewGroup
     * @param newType The type of the new window
     * @param newPriority The priority of the new window
     * @return The index to insert at, or -1 to add to the end
     */
    private int calculateInsertionIndex(ViewGroup baseLayout, int newType, int newPriority) {
        if (mWindows.isEmpty()) {
            return -1;
        }

        // 1. Get all existing window records
        List<WindowRecord> existingWindows = new ArrayList<>(mWindows.values());
        
        // 2. Sort them by type then priority (ascending: lower Z -> higher Z)
        // This represents the desired order from bottom to top.
        Collections.sort(existingWindows, (r1, r2) -> {
            if (r1.type != r2.type) return Integer.compare(r1.type, r2.type);
            return Integer.compare(r1.priority, r2.priority);
        });
        
        // 3. Find the first window that should be physically ABOVE the new window.
        // The new window should be inserted just BEFORE this "anchor" window.
        // Since the list is sorted Low -> High, we look for the first record that is "larger" than new window.
        WindowRecord anchor = null;
        for (WindowRecord record : existingWindows) {
            if (record.type > newType || (record.type == newType && record.priority > newPriority)) {
                anchor = record;
                break;
            }
        }
        
        // 4. If an anchor is found, get its view's index
        if (anchor != null && anchor.controller != null) {
            View anchorView = anchor.controller.getLayout();
            if (anchorView != null && anchorView.getParent() == baseLayout) {
                int anchorIndex = baseLayout.indexOfChild(anchorView);
                Log.d(TAG, "calculateInsertionIndex: found anchor windowId=" + anchor.id 
                        + ", type=" + anchor.type + ", priority=" + anchor.priority 
                        + ", index=" + anchorIndex);
                if (anchorIndex >= 0) {
                    return anchorIndex;
                }
            }
        }
        
        Log.d(TAG, "calculateInsertionIndex: no suitable anchor found, adding to end (top)");
        // Default: add to the end (top)
        return -1;
    }

    private void handleHideWindow(String windowId) {
        handleHideWindowInternal(windowId, true);
    }
    
    /**
     * Internal method to hide a window.
     * @param windowId The window ID to hide
     * @param callHideView Whether to call hideView on the controller.
     *                     Set to false if this is called from a callback where
     *                     the view has already been hidden.
     */
    private void handleHideWindowInternal(String windowId, boolean callHideView) {
        WindowRecord record = mWindows.remove(windowId);
        if (record != null) {
            Log.d(TAG, "handleHideWindowInternal: windowId=" + windowId + ", callHideView=" + callHideView);
            
            // Remove reverse mapping
            mControllerToIdMap.remove(record.controller);
            
            // Clear the listener to prevent any further callbacks
            record.controller.setOnStateChangeListener(null);
            
            if (callHideView) {
                mGlobalStateController.hideView(record.controller, null);
            }
            
            // Always force remove the view from parent to ensure cleanup
            View layout = record.controller.getLayout();
            if (layout != null && layout.getParent() instanceof ViewGroup) {
                ((ViewGroup) layout.getParent()).removeView(layout);
            }
        } else {
            Log.d(TAG, "handleHideWindowInternal: windowId=" + windowId + " not found in mWindows");
        }
        
        // Update touch modal state after window is hidden
        updateTouchModalState();
    }

    /**
     * Update the touch modal state based on current visible windows.
     * If there are no windows or any fullscreen window exists, use modal mode (default).
     * If all visible windows are non-fullscreen, allow touch pass-through and set touchable region.
     */
    private void updateTouchModalState() {
        Log.d(TAG, "updateTouchModalState: mWindows.size=" + mWindows.size());
        
        if (mWindows.isEmpty()) {
            // No dynamic windows, restore modal mode
            mGlobalStateController.setTouchModal(true);
            mGlobalStateController.setTouchableRegion(null);
            Log.d(TAG, "updateTouchModalState: no windows, restoring touch modal");
            return;
        }
        
        // Check if any window is fullscreen
        boolean hasFullscreenWindow = false;
        for (WindowRecord record : mWindows.values()) {
            boolean isFS = isFullscreen(record.config);
            Log.d(TAG, "updateTouchModalState: checking window=" + record.id 
                    + ", config.width=" + record.config.width 
                    + ", config.height=" + record.config.height
                    + ", screenW=" + mScreenWidth + ", screenH=" + mScreenHeight
                    + ", isFullscreen=" + isFS);
            if (isFS) {
                hasFullscreenWindow = true;
                break;
            }
        }
        
        if (hasFullscreenWindow) {
            // Has fullscreen window, use modal mode
            mGlobalStateController.setTouchModal(true);
            mGlobalStateController.setTouchableRegion(null);
            Log.d(TAG, "updateTouchModalState: has fullscreen window, using touch modal");
        } else {
            // All windows are non-fullscreen, allow touch pass-through
            mGlobalStateController.setTouchModal(false);
            
            // Calculate and set touchable region for all non-fullscreen windows
            Region combinedRegion = calculateTouchableRegion();
            mGlobalStateController.setTouchableRegion(combinedRegion);
            Log.d(TAG, "updateTouchModalState: all windows non-fullscreen, allowing touch pass-through, region=" + combinedRegion);
        }
    }
    
    /**
     * Calculate the combined touchable region for all visible windows.
     * Uses Region to properly represent multiple non-overlapping rectangles,
     * allowing touches in the "gaps" between windows to pass through.
     */
    private Region calculateTouchableRegion() {
        Region combined = new Region();
        
        for (WindowRecord record : mWindows.values()) {
            Rect windowRect = calculateWindowRect(record.config);
            Log.d(TAG, "calculateTouchableRegion: window=" + record.id + ", rect=" + windowRect);
            combined.op(windowRect, Region.Op.UNION);
        }
        
        Log.d(TAG, "calculateTouchableRegion: combined region bounds=" + combined.getBounds());
        return combined;
    }
    
    /**
     * Calculate the screen rectangle for a window based on its config.
     */
    private Rect calculateWindowRect(JdWindowConfig config) {
        int width = config.width;
        int height = config.height;
        
        // Handle WRAP_CONTENT and MATCH_PARENT
        if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            width = 0; // Will be updated when view is laid out
        } else if (width == ViewGroup.LayoutParams.MATCH_PARENT) {
            width = mScreenWidth;
        }
        
        if (height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            height = 0; // Will be updated when view is laid out
        } else if (height == ViewGroup.LayoutParams.MATCH_PARENT) {
            height = mScreenHeight;
        }
        
        // Calculate position based on gravity
        int left = config.x;
        int top = config.y;
        
        int hGravity = config.gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
        int vGravity = config.gravity & Gravity.VERTICAL_GRAVITY_MASK;
        
        // Horizontal position
        switch (hGravity) {
            case Gravity.CENTER_HORIZONTAL:
                left = (mScreenWidth - width) / 2 + config.x;
                break;
            case Gravity.RIGHT:
                left = mScreenWidth - width - config.x;
                break;
            case Gravity.LEFT:
            default:
                left = config.x;
                break;
        }
        
        // Vertical position
        switch (vGravity) {
            case Gravity.CENTER_VERTICAL:
                top = (mScreenHeight - height) / 2 + config.y;
                break;
            case Gravity.BOTTOM:
                top = mScreenHeight - height - config.y;
                break;
            case Gravity.TOP:
            default:
                top = config.y;
                break;
        }
        
        return new Rect(left, top, left + width, top + height);
    }

    /**
     * Check if a window configuration represents a fullscreen window.
     */
    private boolean isFullscreen(JdWindowConfig config) {
        // MATCH_PARENT (-1) means fullscreen
        if (config.width == ViewGroup.LayoutParams.MATCH_PARENT 
                && config.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            return true;
        }
        // Check if dimensions match or exceed screen size
        return config.width >= mScreenWidth && config.height >= mScreenHeight;
    }

    // ========== OnStateChangeListener Implementation ==========
    
    @Override
    public void onHidden(DynamicWindowViewController controller) {
        Log.d(TAG, "onHidden callback: controller=" + controller);
        mHandler.post(() -> handleControllerHidden(controller));
    }
    
    @Override
    public void onDetached(DynamicWindowViewController controller) {
        Log.d(TAG, "onDetached callback: controller=" + controller);
        mHandler.post(() -> handleControllerDetached(controller));
    }

//    @Override
//    public void onSurfaceInitialized(DynamicWindowViewController controller) {
//        Log.d(TAG, "onSurfaceInitialized callback: setting overlay background to semi-transparent red");
//        ViewGroup baseLayout = mWindowController.getBaseLayout();
//        if (baseLayout != null) {
//            baseLayout.setBackgroundColor(Color.argb(0x80, 0xFF, 0x00, 0x00));
//        }
//    }
    
    /**
     * Called when a controller is hidden through external means
     * (e.g., occlusion, notification panel overlap, etc.)
     */
    private void handleControllerHidden(DynamicWindowViewController controller) {
        String windowId = mControllerToIdMap.get(controller);
        if (windowId != null) {
            Log.d(TAG, "handleControllerHidden: found windowId=" + windowId + " for controller");
            // The view was already hidden, so don't call hideView again
            handleHideWindowInternal(windowId, false);
        } else {
            Log.d(TAG, "handleControllerHidden: controller not found in mControllerToIdMap");
        }
    }
    
    /**
     * Called when a controller's view is unexpectedly detached from the window
     * (e.g., parent removed, window destroyed, etc.)
     */
    private void handleControllerDetached(DynamicWindowViewController controller) {
        String windowId = mControllerToIdMap.get(controller);
        if (windowId != null) {
            Log.d(TAG, "handleControllerDetached: found windowId=" + windowId + " for controller");
            // The view was detached, so don't call hideView again
            handleHideWindowInternal(windowId, false);
        } else {
            Log.d(TAG, "handleControllerDetached: controller not found in mControllerToIdMap");
        }
    }

    private static class WindowRecord {
        String id;
        int type;
        int priority;
        DynamicWindowViewController controller;
        JdWindowConfig config;

        public WindowRecord(String id, int type, int priority, DynamicWindowViewController controller, JdWindowConfig config) {
            this.id = id;
            this.type = type;
            this.priority = priority;
            this.controller = controller;
            this.config = config;
        }
    }
}
