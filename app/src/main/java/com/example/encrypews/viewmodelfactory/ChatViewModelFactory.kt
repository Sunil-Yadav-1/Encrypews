package com.example.encrypews.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.encrypews.models.User
import com.example.encrypews.viewmodels.ChatActivityViewModel

class ChatViewModelFactory(private val user : User) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatActivityViewModel(user) as T
    }
}