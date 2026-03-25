package com.peter.listview.demo.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.peter.listview.demo.model.User

/**
 * SQLite 数据库辅助类
 * 用于 CursorAdapter 演示
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "listview_demo.db"
        private const val DATABASE_VERSION = 1

        // 表名
        const val TABLE_USERS = "users"

        // 列名
        const val COLUMN_ID = "_id"  // CursorAdapter 必须有 _id 列
        const val COLUMN_NAME = "name"
        const val COLUMN_PHONE = "phone"

        // 建表语句
        private const val CREATE_TABLE_USERS = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_USERS)
        // 插入示例数据
        insertSampleData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    /**
     * 插入示例数据
     */
    private fun insertSampleData(db: SQLiteDatabase) {
        val sampleUsers = listOf(
            User(0, "张三", "13800138001"),
            User(0, "李四", "13800138002"),
            User(0, "王五", "13800138003"),
            User(0, "赵六", "13800138004"),
            User(0, "钱七", "13800138005"),
            User(0, "孙八", "13800138006"),
            User(0, "周九", "13800138007"),
            User(0, "吴十", "13800138008"),
            User(0, "郑十一", "13800138009"),
            User(0, "王十二", "13800138010"),
            User(0, "Alice", "13900139001"),
            User(0, "Bob", "13900139002"),
            User(0, "Charlie", "13900139003"),
            User(0, "David", "13900139004"),
            User(0, "Eve", "13900139005")
        )

        sampleUsers.forEach { user ->
            val values = ContentValues().apply {
                put(COLUMN_NAME, user.name)
                put(COLUMN_PHONE, user.phone)
            }
            db.insert(TABLE_USERS, null, values)
        }
    }

    /**
     * 查询所有用户
     * @return Cursor 对象，调用者负责关闭
     */
    fun queryAllUsers(): Cursor {
        return readableDatabase.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_PHONE),
            null,
            null,
            null,
            null,
            "$COLUMN_NAME ASC"
        )
    }

    /**
     * 根据名字搜索用户
     * @param query 搜索关键词
     * @return Cursor 对象，调用者负责关闭
     */
    fun searchUsers(query: String): Cursor {
        val selection = "$COLUMN_NAME LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        return readableDatabase.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_PHONE),
            selection,
            selectionArgs,
            null,
            null,
            "$COLUMN_NAME ASC"
        )
    }

    /**
     * 添加用户
     * @return 新插入行的 rowId，如果出错返回 -1
     */
    fun addUser(name: String, phone: String): Long {
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_PHONE, phone)
        }
        return writableDatabase.insert(TABLE_USERS, null, values)
    }

    /**
     * 删除用户
     * @return 被删除的行数
     */
    fun deleteUser(id: Long): Int {
        return writableDatabase.delete(
            TABLE_USERS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    /**
     * 更新用户信息
     * @return 受影响的行数
     */
    fun updateUser(id: Long, name: String, phone: String): Int {
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_PHONE, phone)
        }
        return writableDatabase.update(
            TABLE_USERS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }
}
