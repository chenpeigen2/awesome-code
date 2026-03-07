package com.peter.components.demo.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.util.Log

/**
 * 自定义 ContentProvider 示例
 * 
 * 知识点：
 * 1. 继承 ContentProvider 实现 CRUD 方法
 * 2. UriMatcher 匹配不同类型的 Uri
 * 3. MIME 类型定义
 * 4. 使用 SQLite 存储数据
 * 
 * Uri 格式：content://authority/path/id
 * - content://com.peter.components.demo.provider/users - 所有用户
 * - content://com.peter.components.demo.provider/users/1 - ID 为 1 的用户
 */
class UserProvider : ContentProvider() {

    companion object {
        private const val TAG = "UserProvider"
        const val AUTHORITY = "com.peter.components.demo.provider"
        
        // Uri 匹配码
        private const val MATCH_USERS = 1
        private const val MATCH_USER_ID = 2
        
        // MIME 类型
        const val MIME_TYPE_DIR = "vnd.android.cursor.dir/vnd.com.peter.user"
        const val MIME_TYPE_ITEM = "vnd.android.cursor.item/vnd.com.peter.user"
    }

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, "users", MATCH_USERS)
        addURI(AUTHORITY, "users/#", MATCH_USER_ID)
    }

    private lateinit var dbHelper: UserDbHelper

    override fun onCreate(): Boolean {
        dbHelper = UserDbHelper(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        
        return when (uriMatcher.match(uri)) {
            MATCH_USERS -> {
                db.query(
                    UserDbHelper.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            MATCH_USER_ID -> {
                val id = ContentUris.parseId(uri)
                db.query(
                    UserDbHelper.TABLE_NAME,
                    projection,
                    "${UserDbHelper.COLUMN_ID} = ?",
                    arrayOf(id.toString()),
                    null,
                    null,
                    sortOrder
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }.apply {
            setNotificationUri(context!!.contentResolver, uri)
        }
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            MATCH_USERS -> MIME_TYPE_DIR
            MATCH_USER_ID -> MIME_TYPE_ITEM
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        
        return when (uriMatcher.match(uri)) {
            MATCH_USERS -> {
                val id = db.insert(UserDbHelper.TABLE_NAME, null, values)
                if (id > 0) {
                    context!!.contentResolver.notifyChange(uri, null)
                    ContentUris.withAppendedId(uri, id)
                } else null
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = dbHelper.writableDatabase
        val count = when (uriMatcher.match(uri)) {
            MATCH_USERS -> {
                db.delete(UserDbHelper.TABLE_NAME, selection, selectionArgs)
            }
            MATCH_USER_ID -> {
                val id = ContentUris.parseId(uri)
                db.delete(
                    UserDbHelper.TABLE_NAME,
                    "${UserDbHelper.COLUMN_ID} = ?",
                    arrayOf(id.toString())
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        
        if (count > 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val db = dbHelper.writableDatabase
        val count = when (uriMatcher.match(uri)) {
            MATCH_USERS -> {
                db.update(UserDbHelper.TABLE_NAME, values, selection, selectionArgs)
            }
            MATCH_USER_ID -> {
                val id = ContentUris.parseId(uri)
                db.update(
                    UserDbHelper.TABLE_NAME,
                    values,
                    "${UserDbHelper.COLUMN_ID} = ?",
                    arrayOf(id.toString())
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        
        if (count > 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return count
    }
}

/**
 * 数据库帮助类
 */
class UserDbHelper(context: android.content.Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "users.db"
        private const val DATABASE_VERSION = 1
        
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_AGE = "age"
        const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_AGE INTEGER,
                $COLUMN_CREATED_AT INTEGER
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}
