package com.example.encrypews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.encrypews.R
import com.example.encrypews.adapters.ProfilePostFragmentAdapter
import com.example.encrypews.databinding.FragmentProfileBinding
import com.google.android.material.tabs.TabLayoutMediator


class ProfileFragment : Fragment() {

    private var _binding :FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profilePostFragmentAdapter: ProfilePostFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        val view = binding.root
        setupActionBar()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val items = listOf<Int>(R.drawable.ic_baseline_grid_on_24,R.drawable.reels_vector_asset,
            R.drawable.tag_vector_asset)


        profilePostFragmentAdapter = ProfilePostFragmentAdapter(this)
        binding.pager.adapter = profilePostFragmentAdapter
        binding.pager.isSaveEnabled = false


        TabLayoutMediator(binding.tabLayout,binding.pager){tab,position->
//            tab.text = "tab" + "${position+1}"
        }.attach()


        binding.tabLayout.getTabAt(0)!!.setIcon(items[0])
        binding.tabLayout.getTabAt(1)!!.setIcon(items[1])
        binding.tabLayout.getTabAt(2)!!.setIcon(items[2])
        super.onViewCreated(view, savedInstanceState)
    }




    private fun setupActionBar(){
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.profileToolbar)
        }
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if(actionBar != null){
            actionBar.setTitle(R.string.activity_toolbar_title)
        }
    }


}