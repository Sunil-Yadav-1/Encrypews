package com.example.encrypews.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.encrypews.Utils.Resource
import com.example.encrypews.applicationclass.NewsApplication
import com.example.encrypews.models.Article
import com.example.encrypews.models.NewsResponse
import com.example.encrypews.repository.NewsRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(val app: Application,val newsRepository: NewsRepository):AndroidViewModel(app) {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage:Int = 1
    var breakingNewsResponse:NewsResponse? = null
    var searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage:Int = 1
    var searchNewsResponse:NewsResponse? = null
    var newSearchQuery:String? = null
    var oldSearchQuery:String? = null

    fun getBreakingNews(countryCode:String){
        viewModelScope.launch {
            safeBreakingNewsCall(countryCode)
        }
    }

    fun searchForNews(searchQuery:String){
        viewModelScope.launch {
            safeSearchForNewsCall(searchQuery)
        }
    }

    fun saveArticle(article: Article)=
        viewModelScope.launch {
            newsRepository.upsert(article)
        }

    fun deleteArticle(article: Article) =
        viewModelScope.launch {
            newsRepository.deleteArticle(article)
        }

    fun getSavedNews() =
        newsRepository.getSavedNews()

    private fun handleBreakingNews(response:Response<NewsResponse>):Resource<NewsResponse>{
       if(response.isSuccessful){
           response.body()?.let {resultResponse->
               breakingNewsPage++
               if(breakingNewsResponse == null){
                   breakingNewsResponse= resultResponse
               }else{
                   val oldarticles = breakingNewsResponse?.articles
                   val newarticles = resultResponse.articles
                   oldarticles?.addAll(newarticles)
               }
               return Resource.Success(breakingNewsResponse?:resultResponse)
           }
       }
        return Resource.Error(null,response.message())

    }

    private fun handleSearchNews(response:Response<NewsResponse>):Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {resultResponse->
                if(searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                } else {
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(null,response.message())

    }

  private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
                breakingNews.postValue(handleBreakingNews(response))
            }else{
                breakingNews.postValue(Resource.Error(null,"No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException->{
                    breakingNews.postValue(Resource.Error(null,"Network Failure"))
                }
                else ->{
                    breakingNews.postValue(Resource.Error(null,"Conversion Failure"))
                }
            }
        }
    }

    private suspend fun safeSearchForNewsCall(searchQuery: String){
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = newsRepository.searchForNews(searchQuery,searchNewsPage)
                searchNews.postValue(handleSearchNews(response))
            }else{
                searchNews.postValue(Resource.Error(null,"No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException->{
                    searchNews.postValue(Resource.Error(null,"Network Failure"))
                }
                else ->{
                    searchNews.postValue(Resource.Error(null,"Conversion Failure"))
                }
            }
        }
    }

    private fun hasInternetConnection():Boolean{
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI)-> true
                capabilities.hasTransport(TRANSPORT_CELLULAR)->true
                capabilities.hasTransport(TRANSPORT_ETHERNET)->true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return  when(type){
                    TYPE_WIFI -> true
                    TYPE_ETHERNET->true
                    TYPE_MOBILE->true
                    else -> false
                }
            }
        }
        return  false
    }
}