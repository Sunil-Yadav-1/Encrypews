package com.example.encrypews.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.encrypews.constants.Constants
import com.example.encrypews.firebase.MyFireBaseDatabase
import com.example.encrypews.models.Comment
import com.example.encrypews.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CommentActivityViewModel : ViewModel() {
    private var _commentList =MutableLiveData<List<Comment>>()
    val commentList: LiveData<List<Comment>> get() = _commentList

    private var _user = MutableLiveData<User>()
    val user : LiveData<User> get() = _user

    fun userListEventListener(postId:String){
       val ref = Firebase.database.reference.child(Constants.COMMENTS).child(postId)

       ref.addValueEventListener(object : ValueEventListener{
           override fun onDataChange(snapshot: DataSnapshot) {
                val list =ArrayList<Comment>()

               for(ds in snapshot.children){
                   val comment = ds.getValue(Comment::class.java)
                   if(comment != null){
                       list.add(comment)
                   }
               }
               Log.e("Snapshot comment","$list")
               _commentList.value = list
           }

           override fun onCancelled(error: DatabaseError) {
               Log.e("commentVm",error.message)
           }

       })
   }

    suspend fun postComment(comment:Comment){
        MyFireBaseDatabase().postComment(comment)
    }

    suspend fun loadUser(){
        _user.postValue(MyFireBaseDatabase().loadUser())
    }

}