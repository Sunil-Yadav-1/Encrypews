package com.example.encrypews.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.encrypews.constants.Constants
import com.example.encrypews.firebase.MyFireBaseAuth
import com.example.encrypews.firebase.MyFireBaseDatabase
import com.example.encrypews.models.Post
import com.example.encrypews.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ProfileFragmentViewModel: ViewModel() {

   private  var _user = MutableLiveData<User>()
    val user : LiveData<User>get() = _user

    private var _posts = ArrayList<Post>()
    val posts : List<Post> get() = _posts

     var _postCount =MutableLiveData<Int>()
    val postCount :LiveData<Int> get() = _postCount

    suspend fun getPosts(id:String = MyFireBaseAuth.getUserId()){
        Log.e("size bef","${postCount.value}")
        val posts = MyFireBaseDatabase().getPosts(id)
        Log.e("psots","$posts")
        _posts = posts as ArrayList<Post>
        _postCount.postValue(posts.size)

    }




    suspend fun loadUser(){
        _user.postValue(MyFireBaseDatabase().loadUser()?.getValue(User::class.java))
        Log.d("fetched user","${_user.value}")
    }



    fun addUserChangeListener(){
        val dbref = Firebase.database.reference
        dbref.child(Constants.USERS).child(MyFireBaseAuth
            .getUserId()).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                _user.postValue(snapshot.getValue(User :: class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DatabaseError","${error.message}")
            }

        })
    }




}