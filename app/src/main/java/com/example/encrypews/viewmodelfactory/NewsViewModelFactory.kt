package com.example.encrypews.viewmodelfactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.encrypews.repository.NewsRepository
import com.example.encrypews.viewmodels.NewsViewModel

class NewsViewModelFactory(val application: Application,
                           val newsRepository: NewsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(application,newsRepository) as T
    }
}