package com.example.encrypews.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.encrypews.R
import com.example.encrypews.databinding.FragmentSearchBinding



class SearchFragment : Fragment() {
    private  var _binding:FragmentSearchBinding? = null
    private val binding  get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        setupActionBar()
        return binding.root

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu,menu)
        (activity as AppCompatActivity).supportActionBar?.elevation = 0f
        super.onCreateOptionsMenu(menu, inflater)

    }

    private fun setupActionBar(){
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.searchToolbar)
        }

        Toast.makeText(activity?.applicationContext,"APPBARSET",Toast.LENGTH_SHORT).show()




    }


}