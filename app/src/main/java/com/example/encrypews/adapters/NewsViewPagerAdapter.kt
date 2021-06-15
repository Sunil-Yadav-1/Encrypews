package com.example.encrypews.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.encrypews.fragments.BreakingNewsFragment
import com.example.encrypews.fragments.SavedNewsFragment
import com.example.encrypews.fragments.SearchNewsFragment

class NewsViewPagerAdapter(fragment:Fragment):FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        val breakingNewsFragment = BreakingNewsFragment()
        val searchNewsFragment = SearchNewsFragment()
        val savedNewsFragment = SavedNewsFragment()

        return when(position){
            0 -> breakingNewsFragment
            1 -> searchNewsFragment
            2 -> savedNewsFragment
            else -> savedNewsFragment
        }
    }
}