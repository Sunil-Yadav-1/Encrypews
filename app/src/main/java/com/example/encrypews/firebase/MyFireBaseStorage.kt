package com.example.encrypews.firebase

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import com.example.encrypews.activities.EditProfileActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class MyFireBaseStorage {
    private val storageRef = Firebase.storage.reference

    fun uploadImage(activity:Activity,uri:Uri?  ) {
        var imageUrl =""
        val storageReference = storageRef.child("USER_IMAGE_"+
                System.currentTimeMillis()+"."+getFileExtension(activity,uri)
        )

        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot->
            Log.d("Uploaded Image Url",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uniqueRI->
                imageUrl = uniqueRI.toString()

                Log.d("Downloadable image URl",imageUrl)
                if(activity is EditProfileActivity)
                     activity.imageUploaded(true,imageUrl)

            }.addOnFailureListener{
                exception->
                Log.e("Error FirebaseStorage",exception.message!!)
                if(activity is EditProfileActivity)
                    activity.imageUploaded(false,imageUrl)
            }

        }

    }


    private fun getFileExtension(activity:Activity, uri : Uri?) : String?{
        Log.d("extension",MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!)).toString())
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    suspend fun uploadImg(uri: Uri?) : String{
        var imgUrl = ""
        if(uri != null){
            val storageReference = storageRef.child("POST_"+System.currentTimeMillis())
            val taskSnapshot = storageReference.putFile(uri).await()
            val dwndbleUrl = taskSnapshot.metadata?.reference?.downloadUrl?.await()
            imgUrl = dwndbleUrl.toString()
            Log.d("suspend dwnd ur","$imgUrl")
        }
        return imgUrl
    }
}


