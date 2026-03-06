package android.content.res;

import android.content.res.loader.ResourcesLoader;

/**
 * Stub class for compilation only.
 * At runtime, the real Android class will be used.
 */
public class ResourcesKey {

    public String mResDir;
    public String[] mSplitResDirs;
    public String[] mOverlayDirs;
    public String[] mOverlayPaths;
    public int mDisplayId;
    public Configuration mOverrideConfiguration;
    public CompatibilityInfo mCompatInfo;
    public ResourcesLoader[] mLoaders;

    public ResourcesKey(String resDir, String[] splitResDirs, String[] overlayDirs,
                        String[] libDirs, int displayId, Configuration overrideConfig,
                        CompatibilityInfo compatInfo) {
        this.mResDir = resDir;
        this.mSplitResDirs = splitResDirs;
        this.mOverlayDirs = overlayDirs;
        this.mDisplayId = displayId;
        this.mOverrideConfiguration = overrideConfig;
        this.mCompatInfo = compatInfo;
    }

    public ResourcesKey(String resDir, String[] splitResDirs, String[] overlayDirs,
                        String[] libDirs, int displayId, Configuration overrideConfig,
                        CompatibilityInfo compatInfo, ResourcesLoader[] loaders) {
        this.mResDir = resDir;
        this.mSplitResDirs = splitResDirs;
        this.mOverlayDirs = overlayDirs;
        this.mDisplayId = displayId;
        this.mOverrideConfiguration = overrideConfig;
        this.mCompatInfo = compatInfo;
        this.mLoaders = loaders;
    }
}
