package com.peter.room.demo.repository

import com.peter.room.demo.db.dao.ArticleDao
import com.peter.room.demo.db.entity.Article
import kotlinx.coroutines.flow.Flow

class ArticleRepository(private val articleDao: ArticleDao) {
    suspend fun insert(article: Article) = articleDao.insert(article)
    suspend fun insertAll(articles: List<Article>) = articleDao.insertAll(articles)
    suspend fun search(query: String) = articleDao.search(query)
    fun searchFlow(query: String): Flow<List<Article>> = articleDao.searchFlow(query)
    suspend fun searchLike(keyword: String) = articleDao.searchLike(keyword)
    suspend fun getAll() = articleDao.getAll()
    suspend fun deleteAll() = articleDao.deleteAll()
}
