package com.example.encrypews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.encrypews.R
import com.example.encrypews.adapters.PostListProfileRVAdapter
import com.example.encrypews.adapters.ProfilePostFragmentAdapter
import com.example.encrypews.databinding.FragmentOtherProfileListBinding
import com.example.encrypews.models.Post
import com.example.encrypews.viewmodels.OtherProfileFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class OtherPostListFragment : Fragment() {
    private lateinit var viewModel:OtherProfileFragmentViewModel
    private var _binding:FragmentOtherProfileListBinding? = null
    private val binding: FragmentOtherProfileListBinding get() = _binding!!
    private lateinit var adapter: PostListProfileRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOtherProfileListBinding.inflate(inflater,container,false)
        val view = binding.root
        adapter = PostListProfileRVAdapter()
        viewModel = ViewModelProvider(requireActivity()).get(OtherProfileFragmentViewModel::class.java)
        binding.rvOtherPostListFragment.layoutManager = GridLayoutManager(activity,3)
        binding.rvOtherPostListFragment.adapter = adapter




        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.postCount.observe(viewLifecycleOwner, Observer { value->
//            if(value != 0){
//               adapter.posts = viewModel.posts as ArrayList<Post>
//                adapter.notifyDataSetChanged()
//            }
//        })

        viewModel.posts.observe(viewLifecycleOwner, Observer { list->
//            adapter.posts = list as ArrayList<Post>
//            adapter.notifyDataSetChanged()
            adapter.differ.submitList(list)
        })
    }


}