package com.example.encrypews.firebase

import android.util.Log
import com.example.encrypews.activities.EditProfileActivity
import com.example.encrypews.activities.SignUpActivity
import com.example.encrypews.constants.Constants
import com.example.encrypews.models.Post
import com.example.encrypews.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class MyFireBaseDatabase {
   private val dbRef = Firebase.database.reference





    fun createUser(activity:SignUpActivity,user: User){
        val id = MyFireBaseAuth.getUserId()
        user.id = id
        dbRef.child(Constants.USERS).child(id).setValue(user).addOnSuccessListener {

            activity.userCreatedSuccesfully(true,user)

        }.addOnFailureListener{
            activity.userCreatedSuccesfully(false,null)
            Log.e("ECU",it.message.toString())
        }
    }

    suspend fun loadUser(id:String = MyFireBaseAuth.getUserId()) : DataSnapshot?{

        try{
            val data = dbRef.child(Constants.USERS).child(id).get().await()
            Log.d("data fetched","$data")
            return data
        }catch (e : Exception){
            Log.e("Error load user",e.message.toString())
            return null
        }

    }

    suspend fun getPosts(id:String=MyFireBaseAuth.getUserId()):List<Post>{
        val list = ArrayList<Post>()
        val ref= dbRef.child(Constants.POSTS).child(id)
        try{
            val snapshot = ref.get().await()
            Log.d("SnapShot","$snapshot")

            for( dsnapshot in snapshot.children){
                val post = dsnapshot.getValue(Post::class.java)
                if(post != null){
                    list.add(post)
                }
                Log.d("post","$post")
            }


//            Log.e("map","$map")

        }catch (e :Exception){
            Log.e("exception",e.message.toString())
        }

        return list
    }



    fun updateUser(activity: EditProfileActivity,userMap: HashMap<String,Any>){
        dbRef.child(Constants.USERS).child(MyFireBaseAuth.getUserId())
            .updateChildren(userMap).addOnSuccessListener {
                Log.d("User Update","Success")
                activity.userUpdateDone(true)
            }.addOnFailureListener{
                activity.userUpdateDone(false)
                Log.d("User Update Failed",it.message.toString())
            }
    }

    suspend fun followUser(id : String){
        dbRef.child(Constants.FOLLOW).child(MyFireBaseAuth.getUserId())
            .child(Constants.FOLLOWING).child(id).setValue(true).await()
           val task2= dbRef.child(Constants.FOLLOW).child(id)
               .child(Constants.FOLLOWERS).child(MyFireBaseAuth.getUserId()).setValue(true).await()

                Log.d("task2","$task2")
                Log.d("Success","Following Success")
    }

    suspend fun UnfollowUser(id : String){
        dbRef.child(Constants.FOLLOW).child(MyFireBaseAuth.getUserId())
            .child(Constants.FOLLOWING).child(id).removeValue().await()

        val task2= dbRef.child(Constants.FOLLOW).child(id)
            .child(Constants.FOLLOWERS).child(MyFireBaseAuth.getUserId()).removeValue().await()

        Log.d("task2","$task2")
        Log.d("Success","UnFollowing Success")

    }



    suspend fun uploadPost(post: Post) : Boolean{
        var bool = true
     val ref =   dbRef.child(Constants.POSTS).child(MyFireBaseAuth.getUserId())
        val postId = ref.push().key
        val postMap = HashMap<String,Any>()
        postMap[Constants.POSTID] = postId!!
        postMap[Constants.CAPTION]= post.caption
        postMap[Constants.IMAGEURL] = post.imageUrl
        postMap[Constants.PUBLISHEDBY]= post.publishedBy

         try {
             ref.child(postId).updateChildren(postMap).await()
         }catch (e : Exception){
             Log.d("Post up exc",e.message.toString())
             bool =false
         }
        return bool

    }



//    private fun addUserChangeListener(id:String) : DataSnapshot?{
//
//        dbRef.child(Constants.USERS)
//            .child(id).addValueEventListener(object: ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                        Constants._onlineUser.value= snapshot.getValue(User :: class.java)
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e("Database error",error.message)
//                }
//
//            })
//    }


    suspend fun getFollowers(id:String): List<String>{
       val ref= dbRef.child(Constants.FOLLOW).child(id).child(Constants.FOLLOWERS)
        val snapshot = ref.get().await()
        var list = ArrayList<String>()
       if(snapshot.value != null){
           for(ds in snapshot.children){
               if(ds.key != null){
                   list.add(ds.key!!)
               }
           }
       }
        return list
    }
    suspend fun getFollowing(id:String): List<String>{
        val ref= dbRef.child(Constants.FOLLOW).child(id).child(Constants.FOLLOWING)
        val snapshot = ref.get().await()
        var list = ArrayList<String>()
        if(snapshot.value != null){
            for(ds in snapshot.children){
                if(ds.key != null){
                    list.add(ds.key!!)
                }
            }
        }
        return list
    }



}