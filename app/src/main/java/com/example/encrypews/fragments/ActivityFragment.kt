package com.example.encrypews.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.encrypews.R
import com.example.encrypews.activities.MainActivity
import com.example.encrypews.adapters.NewsViewPagerAdapter
import com.example.encrypews.databinding.FragmentActivityBinding
import com.example.encrypews.viewmodels.NewsViewModel
import com.google.android.material.tabs.TabLayoutMediator


class ActivityFragment : Fragment(R.layout.fragment_activity) {
        private var _binding:FragmentActivityBinding? = null
        private val binding get() = _binding!!

         lateinit var viewModel:NewsViewModel






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentActivityBinding.inflate(inflater,container,false)
        val view = binding.root
        val newsViewPagerAdapter = NewsViewPagerAdapter(this)
        binding.pagerActivityFragment.adapter = newsViewPagerAdapter
        //binding.pagerActivityFragment.isSaveEnabled = false

        TabLayoutMediator(binding.tabLayoutActivityFragment,binding.pagerActivityFragment){tab,position->
            when(position){
                0->tab.icon =ContextCompat.getDrawable(requireContext(),R.drawable.ic_outline_article_24)
                1->tab.icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_search_24)
                2->tab.icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_saved_article)
            }
        }.attach()


        viewModel = (activity as MainActivity).newsViewModel






        return view

    }






}