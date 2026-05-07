package com.peter.room.demo.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.peter.room.demo.db.entity.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<Article>)

    // FTS MATCH query — the primary search method for FTS
    @Query("SELECT * FROM articles WHERE articles MATCH :query")
    suspend fun search(query: String): List<Article>

    // FTS MATCH with Flow for reactive observation
    @Query("SELECT * FROM articles WHERE articles MATCH :query")
    fun searchFlow(query: String): Flow<List<Article>>

    // Traditional LIKE query for comparison
    @Query("SELECT * FROM articles WHERE title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%'")
    suspend fun searchLike(keyword: String): List<Article>

    @Query("SELECT * FROM articles")
    suspend fun getAll(): List<Article>

    @Query("DELETE FROM articles")
    suspend fun deleteAll()
}
