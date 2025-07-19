package com.example.multibhashanews.api

import com.example.multibhashanews.model.NewsResponse
import com.example.multibhashanews.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi{
    @GET("v2/top-headlines")
    suspend fun getHeadLines(
        @Query("country")
        countryCode:String = "us",
        @Query("page")
        pageNumber:Int = 1,
        @Query("apiKey")
        apiKey:String = API_KEY
    ):Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchQuery(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber:Int = 1,
        @Query("apiKey")
        apiKey:String = API_KEY
    ):Response<NewsResponse>

    @GET("v2/top-headlines")
    suspend fun getCategoryHeadLines(
        @Query("category")
        category:String,
        @Query("page")
        pageNumber:Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ):Response<NewsResponse>
}