package com.example.encrypews.viewmodels


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.encrypews.Utils.Constants
import com.example.encrypews.firebase.MyFireBaseDatabase
import com.example.encrypews.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SearchFragmentViewModel : ViewModel() {
    private var _listUsers = MutableLiveData<List<User>>()

    val listUsers : LiveData<List<User>> get() = _listUsers

     fun searchUser(input: String){
         if(input != ""){
             val query = Firebase.database.reference.child(Constants.USERS)
                 .orderByChild(Constants.USERNAME).startAt(input).endAt(input+"\uf8ff")

             query.addValueEventListener(object : ValueEventListener{
                 override fun onDataChange(snapshot: DataSnapshot) {
                     Log.d("snapshot","${snapshot}")
                     val mUsers  = ArrayList<User>()
                     for(dsnapshot in snapshot.children){
                         val user = dsnapshot.getValue(User:: class.java)
                         if(user != null){
                             mUsers.add(user)
                         }
                     }
                     _listUsers.value =mUsers
                 }

                 override fun onCancelled(error: DatabaseError) {
                     Log.d("Error SFVM","${error.message}")
                 }

             })
         }

     }

   suspend fun followUser(id: String){
        MyFireBaseDatabase().followUser(id)
    }
    suspend fun UnfollowUser(id: String){
        MyFireBaseDatabase().UnfollowUser(id)
    }
}