package com.example.encrypews.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encrypews.Utils.Constants
import com.example.encrypews.Utils.isSameDay
import com.example.encrypews.firebase.MyFireBaseAuth
import com.example.encrypews.firebase.MyFireBaseDatabase
import com.example.encrypews.models.ChatEvent
import com.example.encrypews.models.DateHeader
import com.example.encrypews.models.Messages
import com.example.encrypews.models.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ChatActivityViewModel(user: User): ViewModel() {

    private var _friendUser = MutableLiveData<User>()
    val friendUser :LiveData<User> get() =_friendUser

        init{
            _friendUser.value = user
        }


     fun sendMessage(message:Messages,currentUser: User){
        MyFireBaseDatabase().sendMessage(message,friendUser.value!!,currentUser)
    }




    fun loadUser(){
        viewModelScope.launch {
            _friendUser.value = MyFireBaseDatabase().loadUser(friendUser.value!!.id)
        }

    }
}