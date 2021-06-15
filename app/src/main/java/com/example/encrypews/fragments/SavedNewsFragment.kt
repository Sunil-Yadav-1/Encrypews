package com.example.encrypews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.encrypews.R
import com.example.encrypews.adapters.NewsAdapter
import com.example.encrypews.databinding.FragmentSavedNewsBinding
import com.example.encrypews.offlineDatabase.ArticleDatabase
import com.example.encrypews.repository.NewsRepository
import com.example.encrypews.viewmodelfactory.NewsViewModelFactory
import com.example.encrypews.viewmodels.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment : Fragment() {

    private lateinit var binding:FragmentSavedNewsBinding
    private lateinit var viewModel:NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private val TAG= "SavedNewsFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSavedNewsBinding.inflate(inflater,container,false)
        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        viewModel = ViewModelProvider(requireActivity(), NewsViewModelFactory(requireActivity().application,newsRepository))
            .get(NewsViewModel::class.java)
        setUpRecyclerView()
        newsAdapter.setOnItemClickListener { article->
            val bundle = Bundle().apply {
                putParcelable("article",article)
            }
            findNavController().navigate(R.id.action_activityFragment_to_articleFragment,bundle)
        }

        val itemTouchHelperCallback =object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view!!, "Succesfully deleted article",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer {
            newsAdapter.differ.submitList(it)
        })

        return binding.root
    }
    private fun setUpRecyclerView(){

        newsAdapter = NewsAdapter()
        binding.rvSavedNews.apply{
            layoutManager = LinearLayoutManager(activity)
            adapter =newsAdapter
        }

    }

}