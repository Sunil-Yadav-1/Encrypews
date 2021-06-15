package com.example.encrypews.repository

import com.example.encrypews.apis.RetrofitInstance
import com.example.encrypews.models.Article
import com.example.encrypews.models.NewsResponse
import com.example.encrypews.offlineDatabase.ArticleDatabase
import retrofit2.Response

class NewsRepository(val db:ArticleDatabase) {

    suspend fun getBreakingNews(countryCode:String,pageNumber:Int):Response<NewsResponse>{
      return  RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)
    }

    suspend fun searchForNews(searchQuery:String,pageNumber: Int):Response<NewsResponse>{
        return RetrofitInstance.api.searchForNews(searchQuery,pageNumber)
    }

    suspend fun upsert(article:Article)=
        db.getArticleDao().upsert(article)

    suspend fun deleteArticle(article: Article) =
        db.getArticleDao().deleteArticle(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()
}