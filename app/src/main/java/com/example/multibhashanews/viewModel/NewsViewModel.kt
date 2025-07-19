package com.example.multibhashanews.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.multibhashanews.model.Article
import com.example.multibhashanews.model.NewsResponse
import com.example.multibhashanews.repository.NewsRepository
import com.example.multibhashanews.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app:Application, val newsRepository:NewsRepository): AndroidViewModel(app) {

    val headlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headLinesPage = 1
    var headLinesResponse: NewsResponse? = null

    var searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null
    var newSearchQuery : String? = null
    var oldSearchQuery : String? = null


    val categoryHeadLines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var categoryPage = 1
    var categoryResponse: NewsResponse? = null
    var currentCategory: String? = null

    init {
        getHeadLines("us")
    }
    // Used for pagination when scroolling
    fun getHeadLines(countryCode: String) = viewModelScope.launch {
        headLinesInternet(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }

    // USE THIS FOR PULL-TO-REFRESH
    fun refreshHeadlines(countryCode: String) = viewModelScope.launch {
        // 1. Reset the page counter to the beginning
        headLinesPage = 1
        // 2. Clear out the old list of articles
        headLinesResponse = null
        // 3. Fetch the fresh data from page 1
        // Assuming you have a function like this that makes the actual API call
        headLinesInternet(countryCode)
    }
    fun getCategory(category: String) = viewModelScope.launch {
        // Check if this is a new category. If so, reset everything.
        if(currentCategory != category){
            currentCategory = category
            categoryPage = 1
            categoryResponse = null
        }
        categoryNewsInternet(category)
    }

    // Create a separate function for refreshing
    fun refreshCategory(category: String) = viewModelScope.launch {
        // Force a reset for the refresh
        currentCategory = category
        categoryPage = 1
        categoryResponse = null
        categoryNewsInternet(category)
    }

    private fun handleHeadLinesResource(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                headLinesPage++
                if(headLinesResponse == null){
                    headLinesResponse = resultResponse
                }else{
                    val oldArticles = headLinesResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(headLinesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {resultResponse->
                if(searchNewsResponse == null || newSearchQuery != oldSearchQuery){
                    searchNewsPage = 2
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                }else{
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return  Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    private fun getCategoryNews(response: Response<NewsResponse>):Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse->
                categoryPage++
                if(categoryResponse == null){
                    categoryResponse = resultResponse
                }else{
                    val oldArticles = categoryResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return  Resource.Success(categoryResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    suspend fun addToFavourites(article: Article) =
        newsRepository.upsert(article)


    fun getFavouriteItems() = newsRepository.getFavouriteNews()

    suspend fun deleteFavouriteArticle(article: Article) =
        newsRepository.deleteArticle(article)


    fun internetConnection(context: Context): Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }?:false
        }
    }
    private suspend fun headLinesInternet(countryCode: String){
        headlines.postValue(Resource.Loading())
        try {
            if(internetConnection(this.getApplication())){
                val response = newsRepository.getHeadLines(countryCode, pageNumber = headLinesPage)
                headlines.postValue(handleHeadLinesResource(response))
            }else{
                headlines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> headlines.postValue(Resource.Error("Unable To Connect"))
                else -> headlines.postValue(Resource.Error("No Signal"))
            }
        }
    }
    private suspend fun searchNewsInternet(searchQuery: String){
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if(internetConnection(this.getApplication())){
                val response = newsRepository.searchNews(searchQuery,searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Unable to  connect"))
                else -> searchNews.postValue(Resource.Error("No Signal"))
            }
        }
    }
    private suspend fun categoryNewsInternet(category: String){
        categoryHeadLines.postValue(Resource.Loading())
        try {
            if(internetConnection(this.getApplication())){
                val response = newsRepository.getCategoryHeadLines(category,categoryPage)
                categoryHeadLines.postValue(getCategoryNews(response))
            }else{
                categoryHeadLines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> categoryHeadLines.postValue(Resource.Error("Unable To Connect"))
                else -> categoryHeadLines.postValue(Resource.Error("Unexpected error: ${t.localizedMessage}"))
            }
        }
    }
}