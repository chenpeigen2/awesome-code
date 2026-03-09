package com.peter.touch.demo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.peter.touch.demo.widget.FloatingLogPanel

/**
 * 带悬浮日志面板的基类Activity
 */
abstract class BaseTouchActivity : AppCompatActivity() {

    protected var floatingLogPanel: FloatingLogPanel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        floatingLogPanel = FloatingLogPanel(this)
    }

    override fun onPostResume() {
        super.onPostResume()
        // 只在首次创建时显示，避免重复添加
        floatingLogPanel?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingLogPanel?.hide()
        floatingLogPanel = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, MENU_CLEAR_LOG, 0, "清空日志")
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            MENU_CLEAR_LOG -> {
                floatingLogPanel?.clearLogs()
                true
            }
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected fun log(tag: String, method: String, action: String, result: String = "") {
        floatingLogPanel?.log(tag, method, action, result)
    }

    protected fun logSeparator(text: String = "──────────────────────") {
        floatingLogPanel?.logSeparator(text)
    }

    companion object {
        private const val MENU_CLEAR_LOG = 1
    }
}
