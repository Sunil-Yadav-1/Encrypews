package com.example.encrypews.customdialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.encrypews.databinding.BottomsheetdialoglayoutBinding
import com.example.encrypews.models.Post
import com.example.encrypews.models.User
import com.example.encrypews.viewmodels.HomeFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomBottomSheetDialogFragment: BottomSheetDialogFragment() {
    private lateinit var binding: BottomsheetdialoglayoutBinding
    private var userOfPost: User? = null
    private var postByUser: Post?= null
    private lateinit var viewModel:HomeFragmentViewModel

    fun newInstance(userOfPost:String,post:String):CustomBottomSheetDialogFragment{
        val frag = CustomBottomSheetDialogFragment()
        val bundle = Bundle()
        bundle.putString("userString",userOfPost)
        bundle.putString("postString",post)
        frag.arguments =bundle
        return frag
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetdialoglayoutBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(requireActivity()).get(HomeFragmentViewModel::class.java)
        extractUserandPostId()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSave.setOnClickListener{
            Toast.makeText(requireContext(),"SaveClicked",Toast.LENGTH_SHORT).show()
        }
        binding.tvUnfollow.setOnClickListener{

            lifecycleScope.launch(Dispatchers.IO){
                if(userOfPost != null){
                    viewModel.unFollowUser(userOfPost!!.id)
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(),"Unfollowed ${userOfPost!!.userName}",Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }else{
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(),"Something went wrong!!",Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
            }
        }
    }

    private fun extractUserandPostId(){
        userOfPost = Gson().fromJson(arguments?.getString("userString"),User::class.java)
        postByUser = Gson().fromJson(arguments?.getString("postString"),Post::class.java)
    }
}