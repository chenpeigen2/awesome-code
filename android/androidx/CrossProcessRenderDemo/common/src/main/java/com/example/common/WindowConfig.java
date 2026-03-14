package com.example.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 窗口配置类
 */
public class WindowConfig implements Parcelable {
    public int width;
    public int height;
    public int x;
    public int y;
    public int gravity;

    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;

    public WindowConfig() {
        this.width = MATCH_PARENT;
        this.height = MATCH_PARENT;
        this.x = 0;
        this.y = 0;
        this.gravity = android.view.Gravity.CENTER;
    }

    public WindowConfig(int width, int height, int x, int y, int gravity) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.gravity = gravity;
    }

    protected WindowConfig(Parcel in) {
        width = in.readInt();
        height = in.readInt();
        x = in.readInt();
        y = in.readInt();
        gravity = in.readInt();
    }

    public static final Creator<WindowConfig> CREATOR = new Creator<WindowConfig>() {
        @Override
        public WindowConfig createFromParcel(Parcel in) {
            return new WindowConfig(in);
        }

        @Override
        public WindowConfig[] newArray(int size) {
            return new WindowConfig[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeInt(x);
        dest.writeInt(y);
        dest.writeInt(gravity);
    }
}
