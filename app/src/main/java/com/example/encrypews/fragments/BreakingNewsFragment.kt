package com.example.encrypews.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.encrypews.R
import com.example.encrypews.Utils.Constants
import com.example.encrypews.Utils.Resource
import com.example.encrypews.adapters.NewsAdapter
import com.example.encrypews.databinding.FragmentBreakingNewsBinding
import com.example.encrypews.offlineDatabase.ArticleDatabase
import com.example.encrypews.repository.NewsRepository
import com.example.encrypews.viewmodelfactory.NewsViewModelFactory
import com.example.encrypews.viewmodels.NewsViewModel


class BreakingNewsFragment : Fragment() {

    private lateinit var binding:FragmentBreakingNewsBinding
    private lateinit var viewModel:NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private val TAG= "BreakingNewsFragment"

    private var isLoading = false
    private var isScrolling = false
    private var isLastPage = false

    val ourScrollListener = object :RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLastPage&&!isLoading
            val isAtLastItem = firstVisibleItemPosition+visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >=0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible
                    &&isScrolling
            if(shouldPaginate){
                viewModel.getBreakingNews("in")
                isScrolling = false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBreakingNewsBinding.inflate(inflater,container,false)
        val newsRepository =NewsRepository(ArticleDatabase(requireContext()))
        viewModel = ViewModelProvider(requireActivity(),NewsViewModelFactory(requireActivity().application,newsRepository))
            .get(NewsViewModel::class.java)
        viewModel.getBreakingNews("in")
        setUpRecyclerView()
        newsAdapter.setOnItemClickListener { article->
            val bundle = Bundle().apply {
                putParcelable("article",article)
            }
            findNavController().navigate(R.id.action_activityFragment_to_articleFragment,bundle)
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { resource->
            when(resource){
                is Resource.Success ->{
                    hideProgressBar()
                    resource.data?.let { newsResponse->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults/Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if(isLastPage){
                            binding.rvBreakingNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error ->{
                    hideProgressBar()
                    resource.message?.let{message->
                        Toast.makeText(requireContext(),"An Error Occurred: $message",Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading ->{
                    showProgressBar()
                }
            }

        })


        return binding.root
    }

    private fun setUpRecyclerView(){

        newsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply{
            layoutManager = LinearLayoutManager(activity)
            adapter =newsAdapter
            addOnScrollListener(ourScrollListener)
        }

    }


    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.GONE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }


}