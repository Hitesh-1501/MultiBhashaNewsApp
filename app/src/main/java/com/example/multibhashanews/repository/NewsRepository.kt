package com.example.multibhashanews.repository

import com.example.multibhashanews.api.RetrofitInstance
import com.example.multibhashanews.database.ArticleDatabase
import com.example.multibhashanews.model.Article

class NewsRepository(val db: ArticleDatabase){
    suspend fun getHeadLines(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getHeadLines(countryCode,pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber:Int) =
        RetrofitInstance.api.searchQuery(searchQuery,pageNumber)

    suspend fun getCategoryHeadLines(category:String, pageNumber:Int) =
        RetrofitInstance.api.getCategoryHeadLines(category,pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upset(article)

    fun getFavouriteNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}