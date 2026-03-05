package com.miui.lifecycle.lifecycle

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


private val TAG = "xxxaa"

class MyObserver : DefaultLifecycleObserver {
    override fun onResume(owner: LifecycleOwner) {
        Log.d(TAG, "onResume: ")
    }

    override fun onPause(owner: LifecycleOwner) {
        Log.d(TAG, "onPause: ")
    }
}
