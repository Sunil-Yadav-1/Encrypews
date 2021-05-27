package com.example.encrypews.firebase

import android.util.Log
import com.example.encrypews.activities.EditProfileActivity
import com.example.encrypews.activities.SignUpActivity
import com.example.encrypews.Utils.Constants
import com.example.encrypews.models.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

    suspend fun loadUser(id:String = MyFireBaseAuth.getUserId()) : User{

        return try{
            val dataSnapshot= dbRef.child(Constants.USERS).child(id).get().await()
            Log.d("data fetched","$dataSnapshot")

            val data = dataSnapshot.getValue(User::class.java)
            if(data != null){

                data
            }else{
                User()
            }
        }catch (e : Exception){
            Log.e("Error load user",e.message.toString())
            User()
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
       try{
           val ref= dbRef.child(Constants.FOLLOW).child(id).child(Constants.FOLLOWERS)
           val snapshot = ref.get().await()
           val list = ArrayList<String>()
           if(snapshot.value != null){
               for(ds in snapshot.children){
                   if(ds.key != null){
                       list.add(ds.key!!)
                   }
               }
           }
           Log.d("followers $id","$list")
           return list
       }catch (e:Exception){
           Log.e("getFollowers",e.message.toString())
           return ArrayList()
       }
    }
    suspend fun getFollowing(id:String): List<String>{
       try{
           val ref= dbRef.child(Constants.FOLLOW).child(id).child(Constants.FOLLOWING)
           val snapshot = ref.get().await()
           val list = ArrayList<String>()
           if(snapshot.value != null){
               for(ds in snapshot.children){
                   if(ds.key != null){
                       list.add(ds.key!!)
                   }
               }
           }
           Log.d("following $id","$list")
           return list
       }catch (e:Exception){
           Log.e("getFollowing",e.message.toString())
           return ArrayList<String>()
       }
    }

     fun likePost(idPost:String,idUser:String = MyFireBaseAuth.getUserId()){
        val ref = dbRef.child(Constants.LIKES).child(idPost)
        ref.child(idUser).setValue(true)
        Log.d("post liked","By $idUser to $idPost")
    }

    fun unlikePost(idPost:String,idUser:String = MyFireBaseAuth.getUserId()){
        val ref = dbRef.child(Constants.LIKES).child(idPost)
        ref.child(idUser).removeValue()
        Log.d("post unliked","Unliked")
    }

    suspend fun postComment(comment:Comment){
        val ref = dbRef.child(Constants.COMMENTS).child(comment.postId)
       try{
           val key = ref.push().key
           if (key != null) {
               comment.key = key
           }
           if (key != null) {
               ref.child(key).setValue(comment).await()
           }
       }catch (e:Exception){
           Log.e("postComment",e.message.toString())
       }

    }


//    suspend fun getNoOfLikes(id: String): Int {
//        val ref = dbRef.child(Constants.LIKES).child(id)
//        val snapshot = ref.get().await()
//        return snapshot.children.count()
//    }
//
//    suspend fun getNoOfComments(id:String):Int{
//        val ref = dbRef.child(Constants.COMMENTS).child(id)
//        val snapshot = ref.get().await()
//        return  snapshot.children.count()
//    }

// Chat messaging functions

     fun sendMessage(message:Messages,friendUser:User,currentUser: User){
         val friendId = friendUser.id
        val mref = dbRef.child(Constants.MESSAGES).child(getMsgId(friendId))
        message.senderId = MyFireBaseAuth.getUserId()
        val key = mref.push().key
        if(key != null){
            message.msgId = key
           mref.child(key).setValue(message).addOnSuccessListener {
               updateLastMessage(message,friendUser,currentUser)
           }.addOnFailureListener{exception ->
               Log.e("sendMessageFBRD",exception.localizedMessage!!)
           }

        }
    }

    private fun updateLastMessage(message:Messages,friendUser: User,currentUser: User){
        val friendId = friendUser.id
        val inbox =  Inbox(message.msg,friendId,friendUser.name,friendUser.userName,friendUser.userImage,chatOpen = true)
        val chatRef1 = dbRef.child(Constants.CHATS).child(MyFireBaseAuth.getUserId()).child(friendId)
        val chatRef2 = dbRef.child(Constants.CHATS).child(friendId).child(MyFireBaseAuth.getUserId())

        chatRef1.setValue(inbox).addOnSuccessListener {
            chatRef2.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(Inbox::class.java)
                    Log.e("value","$value")
                    inbox.apply {
                        from = currentUser.id
                        name= currentUser.name
                        userName = currentUser.userName
                        imageUrl = currentUser.userImage
                        count = 1
                        chatOpen = false
                    }
                    value?.let {
                        if(it.from == message.senderId){
                            inbox.count = value.count+1
                        }
                        inbox.chatOpen = value.chatOpen
                    }

                    chatRef2.setValue(inbox)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("updtLstMsg",error.message)
                }


            })
        }

    }



    private fun getMsgId(friendId: String):String{
        val currentId = MyFireBaseAuth.getUserId()
        if(currentId > friendId){
            return friendId+currentId
        }else{
            return currentId+friendId
        }
    }

}