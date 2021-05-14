package com.example.encrypews.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.encrypews.R
import com.example.encrypews.databinding.ActivityMainBinding
import com.example.encrypews.fragments.ActivityFragment
import com.example.encrypews.fragments.HomeFragment
import com.example.encrypews.fragments.ProfileFragment
import com.example.encrypews.fragments.SearchFragment
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var selectedItemId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        val database = Firebase.database
//        val myRef = database.getReference("message")
//        myRef.setValue("Hello, World!")
        val homeFragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.flmain,homeFragment)
        transaction.commit()
        selectedItemId  = R.id.home_nav

        val searchFragment = SearchFragment()
//        val addPostFragment = AddPostFragment()
        val activityFragment = ActivityFragment()
        val profileFragment = ProfileFragment()




        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            Log.d("selected item","$selectedItemId")
            when(it.itemId){
                R.id.home_nav -> {
                    selectedItemId = it.itemId
                    setCurrentFragment(homeFragment)
                }
                R.id.search_nav->{

                    selectedItemId = it.itemId
                    setCurrentFragment(searchFragment)
                }

                R.id.add_post_nav-> {
                    startActivity(Intent(this,AddPostActivity::class.java))
                }

                R.id.activity_nav->{
                    selectedItemId = it.itemId
                    setCurrentFragment(activityFragment)
                }
                R.id.profile_nav->{
                    selectedItemId = it.itemId
                    setCurrentFragment(profileFragment)
                }
                else  -> {
                    Toast.makeText(this,"Coming Soon",Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        binding.bottomNavigation.selectedItemId = selectedItemId

    }

    private fun setCurrentFragment(fragment : Fragment){
        clearbackStack()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.nav_default_pop_enter_anim,R.anim.nav_default_pop_exit_anim)
        transaction.replace(R.id.flmain,fragment)
        transaction.commit()
    }

    private fun clearbackStack(){
        val count = supportFragmentManager.backStackEntryCount
        for(i in 0 until count ){
            supportFragmentManager.popBackStack()
        }
    }


    override fun onResume() {
        super.onResume()
        val view = findViewById<View>(selectedItemId)
        view.performClick()

    }




}