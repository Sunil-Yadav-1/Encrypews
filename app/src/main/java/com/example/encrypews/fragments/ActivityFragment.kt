package com.example.encrypews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.encrypews.R
import com.example.encrypews.databinding.FragmentActivityBinding


class ActivityFragment : Fragment(R.layout.fragment_activity) {
        private var _binding:FragmentActivityBinding? = null
        private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentActivityBinding.inflate(inflater,container,false)
        val view = binding.root
        setupActionBar()
        return view

    }

    private fun setupActionBar(){
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.activityToolbar)
        }
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if(actionBar != null){
            actionBar.setTitle(R.string.activity_toolbar_title)
        }
    }


}