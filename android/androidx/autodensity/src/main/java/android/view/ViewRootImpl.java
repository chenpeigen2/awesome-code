package android.view;

import android.app.ICompatCameraControlCallback;
import android.content.res.Configuration;

/**
 * Stub class for compilation only.
 * At runtime, the real Android class will be used.
 */
public class ViewRootImpl {

    public interface ActivityConfigCallback {
        void onConfigurationChanged(Configuration overrideConfig, int newDisplayId);
        void requestCompatCameraControl(boolean showControl, boolean transformationApplied,
                                        ICompatCameraControlCallback callback);
    }
}
