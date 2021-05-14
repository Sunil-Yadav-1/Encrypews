package com.example.encrypews.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.encrypews.firebase.MyFireBaseDatabase
import com.example.encrypews.models.User

class HomeFragmentViewModel: ViewModel() {
    private var _posts = MutableLiveData<List<User>>()
    val posts : LiveData<List<User>> get() = _posts

//    suspend fun getPostsForUser(){
//    }
}