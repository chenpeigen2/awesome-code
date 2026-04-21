package com.peter.jni.demo

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.peter.jni.demo.databinding.ActivityDemoBinding

abstract class BaseDemoActivity : AppCompatActivity() {

    protected lateinit var binding: ActivityDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    protected fun getContainer(): LinearLayout = binding.container

    protected fun addSection(title: String, action: () -> Unit) {
        val container = getContainer()
        TextView(this).apply {
            text = title
            textSize = 16f
            setPadding(0, 24, 0, 8)
            container.addView(this)
        }
        Button(this).apply {
            text = "执行"
            setOnClickListener { action() }
            container.addView(this)
        }
    }

    protected fun appendResult(text: String) {
        val container = getContainer()
        val lastBtn = (0 until container.childCount).lastOrNull { container.getChildAt(it) is Button }
            ?: return
        val tag = "result_${container.childCount}"
        val existing = container.findViewWithTag<TextView>(tag)
        if (existing != null) {
            existing.text = "结果: $text"
        } else {
            TextView(this).apply {
                this.text = "结果: $text"
                textSize = 14f
                setTextColor(0xFF333333.toInt())
                this.tag = tag
                setPadding(16, 8, 0, 8)
                container.addView(this, lastBtn + 1)
            }
        }
    }
}
