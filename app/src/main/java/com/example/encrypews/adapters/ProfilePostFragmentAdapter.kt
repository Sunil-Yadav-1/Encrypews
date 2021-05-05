package com.example.encrypews.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.encrypews.fragments.PostsListFragment


class ProfilePostFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

     override fun createFragment(position: Int): Fragment {
         val fragment = PostsListFragment()
         fragment.arguments  = Bundle().apply {
             putInt("object",position+1)
         }
         return fragment
     }

     override fun getItemCount(): Int {
         return 3
     }
}