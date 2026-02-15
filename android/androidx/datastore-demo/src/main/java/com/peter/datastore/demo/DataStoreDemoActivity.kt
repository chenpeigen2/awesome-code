package com.peter.datastore.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.peter.datastore.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataStoreDemoActivity : AppCompatActivity() {

    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var multiTypeDataManager: MultiTypeDataManager
    
    private val logBuilder = StringBuilder()
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datastore_demo)
        
        dataStoreManager = DataStoreManager.getInstance(this)
        
        setupBasicPreferencesDemo()
        setupJsonDataStoreDemo()
        setupReactiveDemo()
        setupMultiTypeDemo()
        setupTransactionDemo()
        
        findViewById<android.widget.Button>(R.id.btnClearLog).setOnClickListener {
            logBuilder.clear()
            updateLogDisplay()
        }
    }

    private fun setupBasicPreferencesDemo() {
        val etStringInput = findViewById<android.widget.EditText>(R.id.etStringInput)
        val etIntInput = findViewById<android.widget.EditText>(R.id.etIntInput)
        val tvBasicResult = findViewById<android.widget.TextView>(R.id.tvBasicResult)
        
        findViewById<android.widget.Button>(R.id.btnSaveString).setOnClickListener {
            val value = etStringInput.text.toString()
            lifecycleScope.launch {
                dataStoreManager.putString("demo_string", value)
                log("保存字符串: $value")
            }
        }
        
        findViewById<android.widget.Button>(R.id.btnSaveInt).setOnClickListener {
            val value = etIntInput.text.toString().toIntOrNull() ?: 0
            lifecycleScope.launch {
                dataStoreManager.putInt("demo_int", value)
                log("保存整数: $value")
            }
        }
        
        findViewById<android.widget.Button>(R.id.btnReadBasic).setOnClickListener {
            lifecycleScope.launch {
                val stringValue = dataStoreManager.getString("demo_string", "默认值")
                val intValue = dataStoreManager.getInt("demo_int", 0)
                withContext(Dispatchers.Main) {
                    tvBasicResult.text = "字符串: $stringValue, 整数: $intValue"
                }
                log("读取基础数据 - 字符串: $stringValue, 整数: $intValue")
            }
        }
        
        findViewById<android.widget.Button>(R.id.btnClearBasic).setOnClickListener {
            lifecycleScope.launch {
                dataStoreManager.remove("demo_string")
                dataStoreManager.remove("demo_int")
                withContext(Dispatchers.Main) {
                    tvBasicResult.text = "当前值: -"
                }
                log("清除基础数据")
            }
        }
    }

    private fun setupJsonDataStoreDemo() {
        val etUserName = findViewById<android.widget.EditText>(R.id.etUserName)
        val etUserAge = findViewById<android.widget.EditText>(R.id.etUserAge)
        val tvUserResult = findViewById<android.widget.TextView>(R.id.tvUserResult)
        
        findViewById<android.widget.Button>(R.id.btnSaveUser).setOnClickListener {
            val name = etUserName.text.toString()
            val age = etUserAge.text.toString().toIntOrNull() ?: 0
            
            lifecycleScope.launch {
                val userPrefs = UserPreferences(
                    userName = name,
                    userAge = age,
                    lastLoginTime = System.currentTimeMillis()
                )
                dataStoreManager.putObject(DemoConfig.Keys.USER_DATA_JSON, userPrefs)
                log("保存用户: $name, 年龄: $age")
            }
        }
        
        findViewById<android.widget.Button>(R.id.btnReadUser).setOnClickListener {
            lifecycleScope.launch {
                val userPrefs = dataStoreManager.getObject(DemoConfig.Keys.USER_DATA_JSON, UserPreferences())
                val timeStr = if (userPrefs.lastLoginTime > 0) {
                    dateFormat.format(Date(userPrefs.lastLoginTime))
                } else {
                    "从未登录"
                }
                withContext(Dispatchers.Main) {
                    tvUserResult.text = "用户: ${userPrefs.userName}, 年龄: ${userPrefs.userAge}, 登录时间: $timeStr"
                }
                log("读取用户信息 - 用户: ${userPrefs.userName}, 年龄: ${userPrefs.userAge}")
            }
        }
    }

    private fun setupReactiveDemo() {
        val tvReactiveValue = findViewById<android.widget.TextView>(R.id.tvReactiveValue)
        
        dataStoreManager.getIntLiveData("reactive_counter", 0).observe(this, Observer { value ->
            tvReactiveValue.text = "实时值: $value (自动更新)"
        })
        
        findViewById<android.widget.Button>(R.id.btnIncrementCounter).setOnClickListener {
            lifecycleScope.launch {
                val current = dataStoreManager.getInt("reactive_counter", 0)
                dataStoreManager.putInt("reactive_counter", current + 1)
                log("计数器 +1, 当前: ${current + 1}")
            }
        }
        
        findViewById<android.widget.Button>(R.id.btnDecrementCounter).setOnClickListener {
            lifecycleScope.launch {
                val current = dataStoreManager.getInt("reactive_counter", 0)
                dataStoreManager.putInt("reactive_counter", current - 1)
                log("计数器 -1, 当前: ${current - 1}")
            }
        }
        
        findViewById<android.widget.Button>(R.id.btnResetCounter).setOnClickListener {
            lifecycleScope.launch {
                dataStoreManager.putInt("reactive_counter", 0)
                log("计数器重置为 0")
            }
        }
    }

    private fun setupMultiTypeDemo() {
        val tvMultiTypeResult = findViewById<android.widget.TextView>(R.id.tvMultiTypeResult)
        
        findViewById<android.widget.Button>(R.id.btnSaveMultiType).setOnClickListener {
            lifecycleScope.launch {
                val userData = MultiTypeDataManager.UserData(
                    userName = "张三",
                    userAge = 25,
                    isLoggedIn = true,
                    lastLoginTime = System.currentTimeMillis(),
                    score = 98.5,
                    favoriteTags = setOf("Android", "Kotlin", "DataStore")
                )
                multiTypeDataManager.saveUserData(userData)
                
                val appConfig = MultiTypeDataManager.AppConfiguration(
                    theme = "dark",
                    fontSize = 16,
                    language = "zh",
                    notificationsEnabled = true
                )
                multiTypeDataManager.saveAppConfiguration(appConfig)
                
                log("保存多类型数据完成")
            }
        }
        
        findViewById<android.widget.Button>(R.id.btnReadMultiType).setOnClickListener {
            lifecycleScope.launch {
                val userData = multiTypeDataManager.readUserData()
                val appConfig = multiTypeDataManager.readAppConfiguration()
                
                withContext(Dispatchers.Main) {
                    tvMultiTypeResult.text = """
                        用户: ${userData.userName} (${userData.userAge}岁)
                        分数: ${userData.score}
                        标签: ${userData.favoriteTags.joinToString(", ")}
                        主题: ${appConfig.theme}, 字体: ${appConfig.fontSize}
                    """.trimIndent()
                }
                log("读取多类型数据完成")
            }
        }
    }

    private fun setupTransactionDemo() {
        val tvTransactionResult = findViewById<android.widget.TextView>(R.id.tvTransactionResult)
        
        findViewById<android.widget.Button>(R.id.btnExecuteTransaction).setOnClickListener {
            lifecycleScope.launch {
                val result = dataStoreManager.executeInTransaction {
                    putString("tx_key1", "事务值1")
                    putInt("tx_key2", 100)
                    putBoolean("tx_key3", true)
                    putObject(DemoConfig.Keys.USER_DATA_JSON, UserPreferences(
                        userName = "事务用户",
                        userAge = 30
                    ))
                    putObject(DemoConfig.Keys.APP_CONFIG_JSON, AppSettings(
                        version = 2,
                        theme = "transaction_theme",
                        fontSize = 18
                    ))
                }
                
                withContext(Dispatchers.Main) {
                    if (result.isSuccess) {
                        tvTransactionResult.text = "事务执行成功!"
                        log("事务执行成功")
                    } else {
                        tvTransactionResult.text = "事务执行失败: ${result.exceptionOrNull()?.message}"
                        log("事务执行失败: ${result.exceptionOrNull()?.message}")
                    }
                }
            }
        }
    }

    private fun log(message: String) {
        val timestamp = dateFormat.format(Date())
        val logMessage = "[$timestamp] $message\n"
        logBuilder.append(logMessage)
        updateLogDisplay()
    }

    private fun updateLogDisplay() {
        findViewById<android.widget.TextView>(R.id.tvLog).text = logBuilder.toString()
    }
}
