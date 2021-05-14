package com.example.encrypews.firebase

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.lang.Exception

object MyFireBaseAuth {
     val auth = FirebaseAuth.getInstance()


    fun getUserId() :String {
        return if(auth.currentUser !=null)
            auth.currentUser!!.uid
        else{
            ""
        }
    }

    suspend fun loginWithEmailandPass(email : String,password:String): AuthResult?{
        try{
            val data = auth.signInWithEmailAndPassword(email,password).await()
            return data
        }catch (e : Exception){
            return null
        }
    }

}