package com.example.encrypews.customdialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.encrypews.databinding.BottomsheetdialoglayoutBinding
import com.example.encrypews.models.Post
import com.example.encrypews.models.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson

class CustomBottomSheetDialogFragment: BottomSheetDialogFragment() {
    private lateinit var binding: BottomsheetdialoglayoutBinding
    private var userOfPost:User = User()
    private var postByUser:Post = Post()

    fun newInstance(userOfPost:String,post:String):CustomBottomSheetDialogFragment{
        val frag = CustomBottomSheetDialogFragment()
        val bundle = Bundle()
        bundle.putString("userString",userOfPost)
        bundle.putString("post",post)
        frag.arguments =bundle
        return frag
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetdialoglayoutBinding.inflate(inflater,container,false)
        extractUserandPost()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSave.setOnClickListener{
            Toast.makeText(requireContext(),"SaveClicked",Toast.LENGTH_SHORT).show()
        }
        binding.tvUnfollow.setOnClickListener{
            Toast.makeText(requireContext(),"UnfollowClicked",Toast.LENGTH_SHORT).show()
        }
    }

    private fun extractUserandPost(){
        userOfPost = Gson().fromJson(arguments?.getString("userString"),User::class.java)
        postByUser = Gson().fromJson(arguments?.getString("post"),Post::class.java)
    }
}