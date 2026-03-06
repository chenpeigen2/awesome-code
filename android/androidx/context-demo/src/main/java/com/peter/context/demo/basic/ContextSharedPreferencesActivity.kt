package com.peter.context.demo.basic

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.context.demo.databinding.ActivityContextSharedpreferencesBinding

/**
 * Context SharedPreferences 示例
 * 
 * SharedPreferences 是 Android 提供的轻量级数据存储方案：
 * 1. 以 key-value 形式存储数据
 * 2. 数据保存在 XML 文件中
 * 3. 适合存储少量配置信息
 * 
 * 获取 SharedPreferences 的方式：
 * 1. Context.getSharedPreferences(name, mode) - 指定文件名
 * 2. Activity.getPreferences(mode) - 使用 Activity 名作为文件名
 * 3. PreferenceManager.getDefaultSharedPreferences(context) - 已废弃
 * 
 * 注意：大量数据请使用 Room/DataStore
 */
class ContextSharedPreferencesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContextSharedpreferencesBinding
    private val sb = StringBuilder()
    
    // SharedPreferences 文件名
    private val PREF_NAME = "user_prefs"
    private val KEY_USERNAME = "username"
    private val KEY_AGE = "age"
    private val KEY_IS_LOGGED_IN = "is_logged_in"
    private val KEY_LAST_LOGIN = "last_login"
    
    // 监听器引用 - 必须保存引用，否则会被 GC 回收导致监听失效
    private var preferenceListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
    private var isListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContextSharedpreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showSharedPreferencesInfo()
    }

    private fun setupListeners() {
        // 保存数据
        binding.btnSave.setOnClickListener {
            saveData()
        }

        // 读取数据
        binding.btnRead.setOnClickListener {
            readData()
        }

        // 清除数据
        binding.btnClear.setOnClickListener {
            clearData()
        }
        
        // 监听数据变化
        binding.btnListen.setOnClickListener {
            toggleChangeListener()
        }
        
        // 多进程
        binding.btnMultiProcess.setOnClickListener {
            demonstrateMultiProcess()
        }
    }

    private fun showSharedPreferencesInfo() {
        sb.clear()
        
        sb.appendLine("=== SharedPreferences 基础知识 ===")
        sb.appendLine()
        
        // 1. 获取 SharedPreferences 的几种方式
        sb.appendLine("=== 1. 获取 SharedPreferences 的方式 ===")
        
        // 方式1: 通过 Context 获取，指定文件名
        val prefs1 = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sb.appendLine("方式1: getSharedPreferences(\"$PREF_NAME\", MODE_PRIVATE)")
        sb.appendLine("  文件路径: /data/data/$packageName/shared_prefs/$PREF_NAME.xml")
        sb.appendLine("  实例: $prefs1")
        
        // 方式2: 通过 Activity 获取，使用 Activity 名作为文件名
        val prefs2 = getPreferences(Context.MODE_PRIVATE)
        sb.appendLine("方式2: getPreferences(MODE_PRIVATE)")
        sb.appendLine("  文件名: ${localClassName}")
        sb.appendLine("  实例: $prefs2")
        
        // 同一个文件名返回同一个实例
        val prefs3 = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sb.appendLine("相同文件名是否返回同一实例: ${prefs1 === prefs3}")
        sb.appendLine()
        
        // 2. 操作模式说明
        sb.appendLine("=== 2. 操作模式 ===")
        sb.appendLine("MODE_PRIVATE (0): 私有模式，只有本应用可访问")
        sb.appendLine("MODE_APPEND (32768): 已废弃，追加内容")
        sb.appendLine("MODE_WORLD_READABLE: 已废弃，全局可读")
        sb.appendLine("MODE_WORLD_WRITEABLE: 已废弃，全局可写")
        sb.appendLine("MODE_MULTI_PROCESS: 已废弃，多进程模式")
        sb.appendLine()
        
        // 3. 支持的数据类型
        sb.appendLine("=== 3. 支持的数据类型 ===")
        sb.appendLine("putString / getString - 字符串")
        sb.appendLine("putInt / getInt - 整数")
        sb.appendLine("putLong / getLong - 长整数")
        sb.appendLine("putFloat / getFloat - 浮点数")
        sb.appendLine("putBoolean / getBoolean - 布尔值")
        sb.appendLine("putStringSet / getStringSet - 字符串集合")
        sb.appendLine()
        
        // 4. 文件位置
        sb.appendLine("=== 4. 存储位置 ===")
        sb.appendLine("文件路径: /data/data/$packageName/shared_prefs/$PREF_NAME.xml")
        sb.appendLine()
        
        binding.tvInfo.text = sb.toString()
    }

    private fun saveData() {
        sb.clear()
        sb.appendLine("=== 保存数据 ===\n")
        
        val username = binding.etUsername.text.toString().ifEmpty { "default_user" }
        val age = binding.etAge.text.toString().toIntOrNull() ?: 0
        
        // 获取 SharedPreferences
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        
        // 方式1: 同步提交
        // editor.apply() 是异步的，没有返回值
        // editor.commit() 是同步的，返回是否成功
        
        val editor = prefs.edit()
        
        // 存储各种类型数据
        editor.putString(KEY_USERNAME, username)
        editor.putInt(KEY_AGE, age)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putLong(KEY_LAST_LOGIN, System.currentTimeMillis())
        
        // 方式1: commit() - 同步提交，返回成功/失败
        // 适用于：需要知道保存是否成功的场景
        val success = editor.commit()
        sb.appendLine("commit() 结果: $success")
        sb.appendLine("commit() 是同步操作，会阻塞当前线程")
        sb.appendLine()
        
        // 方式2: apply() - 异步提交，无返回值
        // 适用于：不需要知道结果，性能更好
        // 注意：apply() 是异步的，但数据会立即写入内存
        prefs.edit()
            .putString("async_key", "async_value")
            .apply()
        sb.appendLine("apply() 是异步操作，不阻塞当前线程")
        sb.appendLine("推荐使用 apply()，性能更好")
        sb.appendLine()
        
        // 链式调用
        prefs.edit()
            .putString("chain_key1", "value1")
            .putString("chain_key2", "value2")
            .apply()
        sb.appendLine("支持链式调用: prefs.edit().putXxx().putXxx().apply()")
        sb.appendLine()
        
        Toast.makeText(this, "数据已保存", Toast.LENGTH_SHORT).show()
        
        binding.tvResult.text = sb.toString()
        Log.d("SharedPreferences", sb.toString())
    }

    private fun readData() {
        sb.clear()
        sb.appendLine("=== 读取数据 ===\n")
        
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        
        // 读取数据，提供默认值
        val username = prefs.getString(KEY_USERNAME, "")
        val age = prefs.getInt(KEY_AGE, 0)
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        val lastLogin = prefs.getLong(KEY_LAST_LOGIN, 0)
        
        sb.appendLine("用户名: $username")
        sb.appendLine("年龄: $age")
        sb.appendLine("是否登录: $isLoggedIn")
        sb.appendLine("上次登录: ${java.util.Date(lastLogin)}")
        sb.appendLine()
        
        // 检查 key 是否存在
        sb.appendLine("=== 检查 key 是否存在 ===")
        sb.appendLine("contains(\"$KEY_USERNAME\"): ${prefs.contains(KEY_USERNAME)}")
        sb.appendLine("contains(\"not_exist\"): ${prefs.contains("not_exist")}")
        sb.appendLine()
        
        // 获取所有键值对
        sb.appendLine("=== 所有键值对 ===")
        val allPrefs = prefs.all
        allPrefs.forEach { (key, value) ->
            sb.appendLine("$key = $value (${value?.javaClass?.simpleName})")
        }
        sb.appendLine()
        
        binding.tvResult.text = sb.toString()
        Log.d("SharedPreferences", sb.toString())
    }

    private fun clearData() {
        sb.clear()
        sb.appendLine("=== 清除数据 ===\n")
        
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        
        // 清除单个 key
        prefs.edit()
            .remove("chain_key1")
            .apply()
        sb.appendLine("已移除 chain_key1")
        
        // 清除所有数据
        prefs.edit()
            .clear()
            .apply()
        sb.appendLine("已清除所有数据")
        sb.appendLine()
        
        sb.appendLine("注意: clear() 会清除整个文件的所有数据")
        
        Toast.makeText(this, "数据已清除", Toast.LENGTH_SHORT).show()
        
        binding.tvResult.text = sb.toString()
        Log.d("SharedPreferences", sb.toString())
    }
    
    /**
     * 开关监听器
     * 重要：监听器对象必须保存为成员变量，否则会被 GC 回收导致监听失效
     */
    private fun toggleChangeListener() {
        sb.clear()
        sb.appendLine("=== 监听数据变化 ===\n")
        
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        
        if (isListening && preferenceListener != null) {
            // 注销监听器
            prefs.unregisterOnSharedPreferenceChangeListener(preferenceListener)
            preferenceListener = null
            isListening = false
            sb.appendLine("已注销监听器")
            sb.appendLine("点击按钮可重新注册")
        } else {
            // 创建并注册监听器 - 必须保存引用！
            preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                // 在主线程更新 UI
                runOnUiThread {
                    val newValue = when (key) {
                        KEY_USERNAME -> sharedPreferences.getString(key, "")
                        KEY_AGE -> sharedPreferences.getInt(key, 0)
                        KEY_IS_LOGGED_IN -> sharedPreferences.getBoolean(key, false)
                        KEY_LAST_LOGIN -> sharedPreferences.getLong(key, 0)
                        else -> sharedPreferences.all[key]
                    }
                    
                    sb.clear()
                    sb.appendLine("=== 监听器触发 ===\n")
                    sb.appendLine("变化的 key: $key")
                    sb.appendLine("新值: $newValue")
                    binding.tvResult.text = sb.toString()
                    Log.d("SharedPreferences", "数据变化: key=$key, value=$newValue")
                }
            }
            
            prefs.registerOnSharedPreferenceChangeListener(preferenceListener)
            isListening = true
            sb.appendLine("已注册监听器")
            sb.appendLine("现在修改数据会触发回调")
            sb.appendLine()
            sb.appendLine("测试方法：")
            sb.appendLine("1. 输入用户名")
            sb.appendLine("2. 点击「保存」按钮")
            sb.appendLine("3. 观察下方显示变化")
        }
        
        binding.tvResult.text = sb.toString()
        Log.d("SharedPreferences", sb.toString())
    }
    
    private fun demonstrateMultiProcess() {
        sb.clear()
        sb.appendLine("=== 多进程注意事项 ===\n")
        
        sb.appendLine("SharedPreferences 不推荐用于多进程通信：")
        sb.appendLine()
        sb.appendLine("问题：")
        sb.appendLine("1. MODE_MULTI_PROCESS 已废弃")
        sb.appendLine("2. 多进程读写可能导致数据不一致")
        sb.appendLine("3. 文件锁机制不可靠")
        sb.appendLine()
        sb.appendLine("替代方案：")
        sb.appendLine("1. ContentProvider - 标准的多进程数据共享方案")
        sb.appendLine("2. DataStore (Preferences DataStore) - 支持 IPC")
        sb.appendLine("3. 使用 IPC 机制（AIDL、Messenger）")
        sb.appendLine("4. 每个进程使用独立的 SharedPreferences")
        
        binding.tvResult.text = sb.toString()
        Log.d("SharedPreferences", sb.toString())
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 重要：注销监听器，避免内存泄漏
        preferenceListener?.let {
            getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(it)
        }
        preferenceListener = null
    }
}
