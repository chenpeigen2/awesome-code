package com.peter.context.demo.advanced

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.context.demo.databinding.ActivityContextDatabaseBinding

/**
 * Context 数据库操作示例
 * 
 * Context 提供的数据库相关方法：
 * 1. openOrCreateDatabase() - 打开或创建数据库
 * 2. deleteDatabase() - 删除数据库
 * 3. databaseList() - 列出所有数据库
 * 4. getDatabasePath() - 获取数据库路径
 * 
 * SQLiteOpenHelper 是更推荐的使用方式
 * 
 * 注意：现代开发推荐使用 Room 库
 */
class ContextDatabaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContextDatabaseBinding
    private val sb = StringBuilder()
    
    private val DB_NAME = "user_db"
    private val DB_NAME_RAW = "raw_db"
    
    private lateinit var dbHelper: UserDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContextDatabaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = UserDbHelper(this)
        
        setupListeners()
        showDatabaseInfo()
    }

    private fun setupListeners() {
        binding.btnCreateDb.setOnClickListener { createDatabaseRaw() }
        binding.btnInsert.setOnClickListener { insertData() }
        binding.btnQuery.setOnClickListener { queryData() }
        binding.btnUpdate.setOnClickListener { updateData() }
        binding.btnDelete.setOnClickListener { deleteData() }
        binding.btnDeleteDb.setOnClickListener { deleteDatabase() }
    }

    private fun showDatabaseInfo() {
        sb.clear()
        
        sb.appendLine("=== Context 数据库操作 ===\n")
        
        // 1. 数据库存储位置
        sb.appendLine("=== 1. 数据库存储位置 ===")
        val dbPath = getDatabasePath(DB_NAME)
        sb.appendLine("数据库路径: $dbPath")
        sb.appendLine("完整路径: ${dbPath.absolutePath}")
        sb.appendLine()
        
        // 2. Context 提供的数据库方法
        sb.appendLine("=== 2. Context 数据库方法 ===")
        sb.appendLine("openOrCreateDatabase(name, mode, factory)")
        sb.appendLine("  - 打开或创建数据库")
        sb.appendLine("  - 返回 SQLiteDatabase 对象")
        sb.appendLine()
        sb.appendLine("deleteDatabase(name)")
        sb.appendLine("  - 删除数据库文件")
        sb.appendLine("  - 返回是否删除成功")
        sb.appendLine()
        sb.appendLine("databaseList()")
        sb.appendLine("  - 返回所有数据库名称数组")
        sb.appendLine()
        sb.appendLine("getDatabasePath(name)")
        sb.appendLine("  - 返回数据库文件路径")
        sb.appendLine()
        
        // 3. 现有数据库列表
        sb.appendLine("=== 3. 现有数据库列表 ===")
        val databases = databaseList()
        if (databases.isEmpty()) {
            sb.appendLine("(暂无数据库)")
        } else {
            databases.forEach { sb.appendLine("  - $it") }
        }
        sb.appendLine()
        
        // 4. SQLiteOpenHelper 说明
        sb.appendLine("=== 4. SQLiteOpenHelper ===")
        sb.appendLine("推荐使用 SQLiteOpenHelper 管理数据库:")
        sb.appendLine("  - onCreate: 创建表")
        sb.appendLine("  - onUpgrade: 升级表结构")
        sb.appendLine("  - onDowngrade: 降级表结构")
        sb.appendLine("  - 自动管理数据库版本")
        sb.appendLine()
        
        // 5. 现代方案
        sb.appendLine("=== 5. 现代数据库方案 ===")
        sb.appendLine("Room (Jetpack):")
        sb.appendLine("  - SQLite 的抽象层")
        sb.appendLine("  - 编译时 SQL 验证")
        sb.appendLine("  - 支持 LiveData/Flow")
        sb.appendLine("  - 支持迁移")
        sb.appendLine()
        
        binding.tvInfo.text = sb.toString()
    }

    private fun createDatabaseRaw() {
        sb.clear()
        sb.appendLine("=== 使用 Context 创建数据库 ===\n")
        
        // 方式1: 直接使用 Context.openOrCreateDatabase
        val db = openOrCreateDatabase(DB_NAME_RAW, Context.MODE_PRIVATE, null)
        sb.appendLine("数据库创建/打开成功")
        sb.appendLine("路径: ${db.path}")
        
        // 创建表
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                age INTEGER
            )
        """.trimIndent())
        sb.appendLine("已创建 users 表")
        
        db.close()
        
        // 列出数据库
        sb.appendLine("\n当前数据库列表:")
        databaseList().forEach { sb.appendLine("  - $it") }
        
        Toast.makeText(this, "数据库创建成功", Toast.LENGTH_SHORT).show()
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextDatabase", sb.toString())
    }

    private fun insertData() {
        sb.clear()
        sb.appendLine("=== 插入数据 ===\n")
        
        val db = dbHelper.writableDatabase
        
        // 方式1: 使用 ContentValues
        val values = ContentValues().apply {
            put("name", "张三")
            put("age", 25)
        }
        val rowId = db.insert("users", null, values)
        sb.appendLine("插入成功, rowId = $rowId")
        
        // 方式2: 使用 execSQL
        db.execSQL("INSERT INTO users (name, age) VALUES ('李四', 30)")
        sb.appendLine("使用 execSQL 插入成功")
        
        // 方式3: 事务
        db.beginTransaction()
        try {
            db.execSQL("INSERT INTO users (name, age) VALUES ('王五', 28)")
            db.execSQL("INSERT INTO users (name, age) VALUES ('赵六', 32)")
            db.setTransactionSuccessful()
            sb.appendLine("事务插入成功")
        } finally {
            db.endTransaction()
        }
        
        Toast.makeText(this, "数据插入成功", Toast.LENGTH_SHORT).show()
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextDatabase", sb.toString())
    }

    private fun queryData() {
        sb.clear()
        sb.appendLine("=== 查询数据 ===\n")
        
        val db = dbHelper.readableDatabase
        
        // 方式1: query() 方法
        sb.appendLine("--- query() 方法 ---")
        val cursor = db.query(
            "users",           // 表名
            arrayOf("id", "name", "age"),  // 列
            null,              // selection (WHERE)
            null,              // selectionArgs
            null,              // groupBy
            null,              // having
            "id DESC"          // orderBy
        )
        
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow("id"))
                val name = it.getString(it.getColumnIndexOrThrow("name"))
                val age = it.getInt(it.getColumnIndexOrThrow("age"))
                sb.appendLine("id=$id, name=$name, age=$age")
            }
        }
        
        // 方式2: rawQuery()
        sb.appendLine("\n--- rawQuery() 方法 ---")
        val rawCursor = db.rawQuery("SELECT * FROM users WHERE age > ?", arrayOf("25"))
        rawCursor.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndexOrThrow("name"))
                val age = it.getInt(it.getColumnIndexOrThrow("age"))
                sb.appendLine("name=$name, age=$age (age > 25)")
            }
        }
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextDatabase", sb.toString())
    }

    private fun updateData() {
        sb.clear()
        sb.appendLine("=== 更新数据 ===\n")
        
        val db = dbHelper.writableDatabase
        
        // 方式1: update() 方法
        val values = ContentValues().apply {
            put("age", 26)
        }
        val rows = db.update("users", values, "name = ?", arrayOf("张三"))
        sb.appendLine("update() 更新了 $rows 行")
        
        // 方式2: execSQL()
        db.execSQL("UPDATE users SET age = 31 WHERE name = '李四'")
        sb.appendLine("execSQL() 更新成功")
        
        Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show()
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextDatabase", sb.toString())
    }

    private fun deleteData() {
        sb.clear()
        sb.appendLine("=== 删除数据 ===\n")
        
        val db = dbHelper.writableDatabase
        
        // 方式1: delete() 方法
        val rows = db.delete("users", "name = ?", arrayOf("王五"))
        sb.appendLine("delete() 删除了 $rows 行")
        
        // 方式2: execSQL()
        db.execSQL("DELETE FROM users WHERE name = '赵六'")
        sb.appendLine("execSQL() 删除成功")
        
        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextDatabase", sb.toString())
    }

    private fun deleteDatabase() {
        sb.clear()
        sb.appendLine("=== 删除数据库 ===\n")
        
        // 关闭 helper
        dbHelper.close()
        
        // 删除数据库
        val deleted = deleteDatabase(DB_NAME)
        sb.appendLine("删除 $DB_NAME: $deleted")
        
        val deletedRaw = deleteDatabase(DB_NAME_RAW)
        sb.appendLine("删除 $DB_NAME_RAW: $deletedRaw")
        
        sb.appendLine("\n剩余数据库:")
        databaseList().forEach { sb.appendLine("  - $it") }
        
        Toast.makeText(this, "数据库已删除", Toast.LENGTH_SHORT).show()
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextDatabase", sb.toString())
    }
    
    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}

/**
 * SQLiteOpenHelper 实现
 */
class UserDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    "user_db",
    null,
    1
) {
    
    companion object {
        private const val CREATE_TABLE = """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                age INTEGER
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 版本升级处理
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }
}
