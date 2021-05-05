package com.example.encrypews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.encrypews.R
import com.example.encrypews.databinding.FragmentPostsListBinding

private const val ARG_OBJECT ="object"
class PostsListFragment : Fragment() {

    private var _binding : FragmentPostsListBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPostsListBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            val textView = binding.text1
            textView.text = getInt(ARG_OBJECT).toString()
        }
    }

}