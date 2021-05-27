package com.example.encrypews.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.encrypews.Utils.Constants
import com.example.encrypews.databinding.ActivitySplashScreenBinding
import com.example.encrypews.firebase.MyFireBaseAuth
import com.example.encrypews.firebase.MyFireBaseDatabase
import com.example.encrypews.models.User
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashScreen : AppCompatActivity() {
    private lateinit var user:User
    private val binding by lazy{
        ActivitySplashScreenBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val typeface:Typeface = Typeface.createFromAsset(assets,"billabong.ttf")
        binding.tvAppName.typeface = typeface
        lifecycleScope. launch {
            delay(1000)
            withContext(Dispatchers.Main){
                loadActivity()
            }
        }

    }

    private fun loadActivity(){
        if(MyFireBaseAuth.getUserId() == "" ){
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            lifecycleScope.launch(Dispatchers.IO) {
                user = MyFireBaseDatabase().loadUser()
                if(user != User()){
                    val sharedPreferences : SharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_USER,
                        MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    val gson:Gson = Gson()
                    val myStringUser :String = gson.toJson(user)
                    editor.apply{
                        putString(Constants.SP_USER,myStringUser)
                    }.apply()
                    editor.commit()
                    val intent = Intent(this@SplashScreen,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else {
                    val sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_USER,
                        MODE_PRIVATE)
                    val gson = Gson()
                     user = gson.fromJson(sharedPreferences.getString(Constants.SP_USER,null),User::class.java)
                    if(user == null || user==User()){
                        val intent = Intent(this@SplashScreen,SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        val intent = Intent(this@SplashScreen,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
            }
        }
    }
}