package com.example.encrypews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.encrypews.R
import com.example.encrypews.databinding.FragmentArticleBinding
import com.example.encrypews.offlineDatabase.ArticleDatabase
import com.example.encrypews.repository.NewsRepository
import com.example.encrypews.viewmodelfactory.NewsViewModelFactory
import com.example.encrypews.viewmodels.NewsViewModel
import com.google.android.material.snackbar.Snackbar


class ArticleFragment : Fragment() {
    private lateinit var viewModel:NewsViewModel
    private lateinit var binding:FragmentArticleBinding
    val args: ArticleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentArticleBinding.inflate(inflater,container,false)
        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        viewModel = ViewModelProvider(requireActivity(), NewsViewModelFactory(requireActivity().application,newsRepository))
            .get(NewsViewModel::class.java)
        val article = args.article
        binding.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let {
                loadUrl(it)
            }
        }

        val webSettings  = binding.webView.settings
//        webSettings.javaScriptEnabled = true   // enabling this compromises security of app

        binding.fab.setOnClickListener{
            viewModel.saveArticle(article)
            view?.let { it1 -> Snackbar.make(it1,"Article Saved Successfully",Snackbar.LENGTH_SHORT).show() }
        }

        return binding.root
    }




}