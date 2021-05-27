package com.example.encrypews.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.encrypews.Utils.Constants
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

    var _user = MutableLiveData<User>()
    val user : LiveData<User>get() = _user

    private var _posts = MutableLiveData<List<Post>>()
    val posts : LiveData<List<Post>> get() = _posts

//     var _postCount =MutableLiveData<Int>()
//    val postCount :LiveData<Int> get() = _postCount

    private var _followers =MutableLiveData<List<String>>()
    val followers : LiveData<List<String>> get() = _followers

    private var _following = MutableLiveData<List<String>>()
    val following : LiveData<List<String>> get() = _following

//    suspend fun getPosts(id:String = MyFireBaseAuth.getUserId()){
//        Log.e("size bef","${postCount.value}")
//        val posts = MyFireBaseDatabase().getPosts(id)
//        Log.e("psots","$posts")
//        _posts = posts as ArrayList<Post>
//        _postCount.postValue(posts.size)
//
//    }




    suspend fun loadUser(){
        _user.postValue(MyFireBaseDatabase().loadUser())
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

    fun addPostsChangeListener(id:String = MyFireBaseAuth.getUserId()){
        val dbref = Firebase.database.reference.child(Constants.FOLLOW).child(id)
        val ref1 = dbref.child(Constants.FOLLOWING)
        val ref2 = dbref.child(Constants.FOLLOWERS)
        val ref3 = Firebase.database.reference.child(Constants.POSTS).child(id)

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
                Log.e("flwng err DS in PFVM",error.message)
            }

        })

        ref2.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("snapshot followers PFVM","$snapshot")
                val list = ArrayList<String>()
                for(ds in snapshot.children){
                    if(ds.key != null){
                        list.add(ds.key!!)
                    }
                }

                _followers.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("flwrs err DS PFVM",error.message)
            }
        })

        ref3.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("posts snapshot","$snapshot")
                var list = ArrayList<Post>()
                for(ds in snapshot.children){
                    val post = ds.getValue(Post::class.java)
                    if(post != null){
                        list.add(post)
                    }
                }
                _posts.value =list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("errPstsDb",error.message)
            }

        })
    }




}