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
import kotlinx.coroutines.tasks.await

class OtherProfileFragmentViewModel : ViewModel() {
    private  var _user = MutableLiveData<User>()
    val user : LiveData<User>
        get() = _user

    private var _posts = ArrayList<Post>()
    val posts : List<Post> get() = _posts

    private var _followers =MutableLiveData<List<String>>()
    val followers : LiveData<List<String>> get() = _followers

    private var _following = MutableLiveData<List<String>>()
    val following : LiveData<List<String>> get() = _following

    private var _isfollowed = MutableLiveData<Boolean>()
    val isfollowed : LiveData<Boolean> get() = _isfollowed

    var _postCount = MutableLiveData<Int>()
    val postCount : LiveData<Int> get() = _postCount





    suspend fun getPosts(id:String = MyFireBaseAuth.getUserId()){
        Log.e("size bef","${postCount.value}")
        val posts = MyFireBaseDatabase().getPosts(id)
        Log.e("psots","$posts")
        _posts = posts as ArrayList<Post>
        _postCount.postValue(posts.size)

    }




    suspend fun loadUser(id:String = MyFireBaseAuth.getUserId()){
        _user.postValue(MyFireBaseDatabase().loadUser(id)?.getValue(User::class.java))
        Log.d("fetched user","${_user.value}")
    }

    suspend fun followUser(id:String){
        MyFireBaseDatabase().followUser(id)
        _isfollowed.postValue(true)

    }

    suspend fun unfollowUser(id: String){
        MyFireBaseDatabase().UnfollowUser(id)
        _isfollowed.postValue(false)
    }

  suspend  fun isFollowed(id:String) {
        Log.d("id","$id")
        Log.d("user","${MyFireBaseAuth.getUserId()}")
        val ref = Firebase.database.reference.child(Constants.FOLLOW)
            .child(id).child(Constants.FOLLOWERS).child(MyFireBaseAuth.getUserId())

//            orderByValue().equalTo(true,MyFireBaseAuth.getUserId())
        val ans = ref.get().await()
      Log.d("answer","$ans")
        if(ans.value != null){
            _isfollowed.postValue(true)
        }else{
            _isfollowed.postValue(false)
        }

//        ref.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.value != null){
//                    Log.d("snapshot ","$snapshot")
//                    for(ds in snapshot.children){
//                        _isfollowed.value = ds.value as Boolean?
//                    }
//                }
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("ErrorDb","followError"+ error.message)
//            }
//
//        })
    }

    suspend fun getFollowers(id:String){
        MyFireBaseDatabase().getFollowers(id)
    }
    suspend fun getFollowing(id:String){
        MyFireBaseDatabase().getFollowing(id)
    }

    fun addValueEventListener(id:String){
        val dbref = Firebase.database.reference.child(Constants.FOLLOW).child(id)
        val ref1 = dbref.child(Constants.FOLLOWING)
        val ref2 = dbref.child(Constants.FOLLOWERS)

        ref1.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("snapshot following","$snapshot")
                val list = ArrayList<String>()
                for(ds in snapshot.children){
                    if(ds.key != null){
                        list.add(ds.key!!)
                    }
                }

                _following.value = list

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("flwng err DS",error.message)
            }

        })

        ref2.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("snapshot followers","$snapshot")
                val list = ArrayList<String>()
                for(ds in snapshot.children){
                    if(ds.key != null){
                        list.add(ds.key!!)
                    }
                }

                _followers.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("flwrs err DS",error.message)
            }
        })
    }

}