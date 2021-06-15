package com.example.encrypews.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.encrypews.fragments.OtherPostListFragment
import com.example.encrypews.fragments.PostsListFragment


class ProfilePostFragmentAdapter(fragment: Fragment,private val boolean: Boolean) : FragmentStateAdapter(fragment) {

     override fun createFragment(position: Int): Fragment {
         val fragment1 = PostsListFragment()
         val fragment2 = PostsListFragment()
         val fragmentOtherUser = OtherPostListFragment()
         fragment1.arguments  = Bundle().apply {
             putInt("object",position+1)
         }
         fragmentOtherUser.arguments  = Bundle().apply {
             putInt("object",position+1)
         }
         return if(position ==0&&boolean){
             fragment1
         }else if(position==0 && !boolean){
             fragmentOtherUser
         }else{
             fragment2
         }
     }

     override fun getItemCount(): Int {
         return  if(boolean){
             3
         }else
             1
     }
}