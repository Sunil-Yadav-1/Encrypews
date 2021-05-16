package com.example.encrypews.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.encrypews.firebase.MyFireBaseAuth
import com.example.encrypews.firebase.MyFireBaseDatabase
import com.example.encrypews.models.Post
import com.example.encrypews.models.User
import java.lang.Exception

class HomeFragmentViewModel: ViewModel() {
    private var _posts = MutableLiveData<List<Post>>()
    val posts : LiveData<List<Post>> get() = _posts

    suspend fun getPostsForUser(){
        try{
            val list = ArrayList<Post>()
            val followers =MyFireBaseDatabase().getFollowing(MyFireBaseAuth.getUserId())
            for(user in followers){
                val posts = MyFireBaseDatabase().getPosts(user)
                Log.d("hompg pst","$posts")
                list.addAll(posts)
            }

            Log.d("hompg posts","$list")
            _posts.postValue(list)
        }catch (e:Exception){
            Log.e("getPostsfrmUser",e.message.toString())
        }
    }

    fun likePost(postId:String){
        MyFireBaseDatabase().likePost(postId)
    }
    fun unlikePost(postId:String){
        MyFireBaseDatabase().unlikePost(postId)
    }
}