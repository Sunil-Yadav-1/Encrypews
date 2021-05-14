package com.example.encrypews.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.encrypews.R
import com.example.encrypews.activities.AddPostActivity
import com.example.encrypews.databinding.FragmentAddPostBinding
import com.theartofdev.edmodo.cropper.CropImage
import java.util.jar.Manifest


class AddPostFragment : Fragment() {
    private lateinit var binding:FragmentAddPostBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(activity,AddPostActivity::class.java))
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddPostBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }



}