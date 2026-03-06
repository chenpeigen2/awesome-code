package com.peter.components.demo.activity.basic

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.R

/**
 * 隐式 Intent 目标 Activity
 *
 * 通过 Intent Filter 中的 Action 匹配启动
 *
 * ═══════════════════════════════════════════════════════════════
 * Intent Filter 匹配规则
 * ═══════════════════════════════════════════════════════════════
 *
 * 在 AndroidManifest.xml 中：
 * <activity android:name=".ImplicitIntentActivity">
 *     <intent-filter>
 *         <action android:name="com.peter.components.demo.IMPLICIT_ACTION" />
 *         <category android:name="android.intent.category.DEFAULT" />
 *     </intent-filter>
 * </activity>
 *
 * 匹配规则：
 * 1. Intent 的 Action 必须匹配 Filter 中的至少一个
 * 2. Intent 的所有 Category 必须在 Filter 中存在
 * 3. Data/Type 匹配（如果指定）
 */
class ImplicitIntentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_implicit_intent)

        findViewById<TextView>(R.id.tvAction).text = "Action: ${intent?.action}"
    }
}
