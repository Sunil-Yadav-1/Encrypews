package com.example.encrypews.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.encrypews.adapters.PostListProfileRVAdapter
import com.example.encrypews.databinding.FragmentPostsListBinding
import com.example.encrypews.models.Post
import com.example.encrypews.viewmodels.ProfileFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_OBJECT ="object"
class   PostsListFragment : Fragment() {

    private var _binding : FragmentPostsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : PostListProfileRVAdapter
    private lateinit var viewModel : ProfileFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPostsListBinding.inflate(inflater,container,false)
        val view = binding.root
        viewModel = ViewModelProvider(requireActivity()).get(ProfileFragmentViewModel::class.java)

        adapter = PostListProfileRVAdapter()

        binding.rvPostListFragment.layoutManager = GridLayoutManager(activity,3)

        binding.rvPostListFragment.adapter = adapter





        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel.postCount.observe(viewLifecycleOwner, Observer { data ->
//          adapter.posts = viewModel.posts as ArrayList<Post>
//            adapter.notifyDataSetChanged()
//        })
        viewModel.posts.observe(viewLifecycleOwner, Observer {list->
            adapter.posts = list as ArrayList<Post>
            adapter.notifyDataSetChanged()
        })
    }

}