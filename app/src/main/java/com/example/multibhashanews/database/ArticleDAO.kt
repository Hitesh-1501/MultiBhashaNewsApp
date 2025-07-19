package com.example.multibhashanews.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.multibhashanews.model.Article

@Dao
interface ArticleDAO{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upset(article: Article)

    @Query("SELECT * FROM articles")
    fun getAllArticles():LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}