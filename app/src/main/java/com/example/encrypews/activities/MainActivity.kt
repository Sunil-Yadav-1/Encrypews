package com.example.encrypews.activities


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.encrypews.R
import com.example.encrypews.Utils.Constants
import com.example.encrypews.databinding.ActivityMainBinding
import com.example.encrypews.models.User
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    var _user : User = User()
    val user : User get() = _user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUser()
            // this peace of code is very important and should be written as it is , as it finds the nav controller
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
            val navController = navHostFragment.navController
            binding.bottomNavigation.setupWithNavController(navController)

    }




private fun getUser(){
    val sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_USER, MODE_PRIVATE)

    val json = sharedPreferences.getString(Constants.SP_USER,null)
    val gson = Gson()

    _user = gson.fromJson(json,User::class.java)
    Log.d("user mainActivity","$user")

}


}