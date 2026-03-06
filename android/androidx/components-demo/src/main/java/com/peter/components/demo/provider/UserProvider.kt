package com.peter.components.demo.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.util.Log

/**
 * 自定义 ContentProvider 示例
 *
 * ═══════════════════════════════════════════════════════════════
 * ContentProvider 核心概念
 * ═══════════════════════════════════════════════════════════════
 *
 * ContentProvider 是 Android 四大组件之一
 * 用于在不同应用间共享数据
 *
 * 特点：
 * 1. 统一的数据访问接口
 * 2. 支持跨进程访问
 * 3. 支持权限控制
 * 4. 支持 MIME 类型
 *
 * URI 结构：
 * content://authority/path/id
 *
 * 示例：
 * content://com.peter.components.demo.provider/users/1
 *
 * ═══════════════════════════════════════════════════════════════
 * 必须实现的方法
 * ═══════════════════════════════════════════════════════════════
 *
 * onCreate()：初始化 Provider
 * query()：查询数据
 * insert()：插入数据
 * update()：更新数据
 * delete()：删除数据
 * getType()：返回 MIME 类型
 */
class UserProvider : ContentProvider() {

    companion object {
        private const val TAG = "UserProvider"

        // Authority - 在 Manifest 中声明
        const val AUTHORITY = "com.peter.components.demo.provider"

        // 表名
        const val TABLE_USERS = "users"

        // URI 匹配码
        private const val CODE_USERS = 1
        private const val CODE_USER_ID = 2

        // MIME 类型
        const val MIME_TYPE_DIR = "vnd.android.cursor.dir/vnd.com.peter.user"
        const val MIME_TYPE_ITEM = "vnd.android.cursor.item/vnd.com.peter.user"

        // UriMatcher 用于匹配 URI
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            // content://authority/users -> 匹配所有用户
            addURI(AUTHORITY, "users", CODE_USERS)
            // content://authority/users/# -> 匹配单个用户（# 表示数字 ID）
            addURI(AUTHORITY, "users/#", CODE_USER_ID)
        }

        /**
         * 内容 URI
         */
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/users")
    }

    private lateinit var dbHelper: UserDbHelper

    override fun onCreate(): Boolean {
        Log.d(TAG, "onCreate")
        dbHelper = UserDbHelper(requireContext())
        return true
    }

    /**
     * 查询数据
     *
     * @param uri 内容 URI
     * @param projection 查询的列
     * @param selection WHERE 条件
     * @param selectionArgs WHERE 参数
     * @param sortOrder 排序
     * @return Cursor 查询结果
     */
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d(TAG, "query: $uri")

        val db = dbHelper.readableDatabase
        val cursor = when (uriMatcher.match(uri)) {
            CODE_USERS -> {
                // 查询所有用户
                db.query(
                    TABLE_USERS,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            CODE_USER_ID -> {
                // 查询单个用户
                val id = ContentUris.parseId(uri)
                db.query(
                    TABLE_USERS,
                    projection,
                    "_id = ?",
                    arrayOf(id.toString()),
                    null,
                    null,
                    sortOrder
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        // 注册观察者，数据变化时自动更新
        cursor.setNotificationUri(context?.contentResolver, uri)

        return cursor
    }

    /**
     * 插入数据
     *
     * @param uri 内容 URI
     * @param values 要插入的数据
     * @return 新插入行的 URI
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d(TAG, "insert: $uri")

        val db = dbHelper.writableDatabase

        when (uriMatcher.match(uri)) {
            CODE_USERS -> {
                val id = db.insert(TABLE_USERS, null, values)
                if (id > 0) {
                    // 通知数据变化
                    context?.contentResolver?.notifyChange(uri, null)
                    return ContentUris.withAppendedId(uri, id)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        return null
    }

    /**
     * 更新数据
     */
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Log.d(TAG, "update: $uri")

        val db = dbHelper.writableDatabase
        val count = when (uriMatcher.match(uri)) {
            CODE_USERS -> {
                db.update(TABLE_USERS, values, selection, selectionArgs)
            }
            CODE_USER_ID -> {
                val id = ContentUris.parseId(uri)
                db.update(TABLE_USERS, values, "_id = ?", arrayOf(id.toString()))
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }

        return count
    }

    /**
     * 删除数据
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.d(TAG, "delete: $uri")

        val db = dbHelper.writableDatabase
        val count = when (uriMatcher.match(uri)) {
            CODE_USERS -> {
                db.delete(TABLE_USERS, selection, selectionArgs)
            }
            CODE_USER_ID -> {
                val id = ContentUris.parseId(uri)
                db.delete(TABLE_USERS, "_id = ?", arrayOf(id.toString()))
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }

        return count
    }

    /**
     * 返回 MIME 类型
     *
     * 用于标识数据类型：
     * - vnd.android.cursor.dir/... 表示多条数据
     * - vnd.android.cursor.item/... 表示单条数据
     */
    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            CODE_USERS -> MIME_TYPE_DIR
            CODE_USER_ID -> MIME_TYPE_ITEM
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    /**
     * 数据库帮助类
     */
    class UserDbHelper(context: Context) : SQLiteOpenHelper(
        context,
        "user_provider.db",
        null,
        1
    ) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE $TABLE_USERS (
                    _id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT,
                    age INTEGER
                )
            """.trimIndent())
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            onCreate(db)
        }
    }
}
