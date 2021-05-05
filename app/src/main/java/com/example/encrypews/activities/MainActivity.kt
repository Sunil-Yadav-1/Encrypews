package com.example.encrypews.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.encrypews.R
import com.example.encrypews.databinding.ActivityMainBinding

import com.example.encrypews.fragments.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.flmain,homeFragment)
        transaction.commit()

        val searchFragment = SearchFragment()
        val addPostFragment = AddPostFragment()
        val activityFragment = ActivityFragment()
        val profileFragment = ProfileFragment()

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home_nav -> setCurrentFragment(homeFragment)
                R.id.search_nav->setCurrentFragment(searchFragment)

                R.id.add_post_nav->setCurrentFragment(addPostFragment)

                R.id.activity_nav->setCurrentFragment(activityFragment)
                R.id.profile_nav->setCurrentFragment(profileFragment)
                else  -> {
                    Toast.makeText(this,"Coming Soon",Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

    }

    private fun setCurrentFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flmain,fragment)
        transaction.commit()
    }




}