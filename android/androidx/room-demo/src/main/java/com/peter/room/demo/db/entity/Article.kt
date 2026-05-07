package com.peter.room.demo.db.entity

import androidx.room.Entity
import androidx.room.Fts4

@Fts4
@Entity(tableName = "articles")
data class Article(
    val title: String,
    val content: String
)
