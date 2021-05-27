package com.example.encrypews.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.encrypews.R
import com.example.encrypews.Utils.ResizeImage
import com.example.encrypews.databinding.FragmentAddPostBinding
import com.example.encrypews.firebase.MyFireBaseAuth
import com.example.encrypews.firebase.MyFireBaseDatabase
import com.example.encrypews.firebase.MyFireBaseStorage
import com.example.encrypews.models.Post
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddPostFragment : Fragment(R.layout.fragment_add_post) {
    private lateinit var binding:FragmentAddPostBinding
    private var imageUri : Uri? = null



    private var permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            granted ->
        Log.d("granted array","$granted")
        if(granted["android.permission.READ_EXTERNAL_STORAGE"]==true &&
            granted["android.permission.WRITE_EXTERNAL_STORAGE"]==true){
            imagePick()
        }else{
            Toast.makeText(requireContext(),"Please Give Permissions to Post Images", Toast.LENGTH_SHORT).show()
        }

    }

    private var CropImageActivityContracts = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity().setAspectRatio(100,100).getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return  if(CropImage.getActivityResult(intent) != null){
                CropImage.getActivityResult(intent).uri
            }else{
                null
            }
        }

    }

    private var cropImageResultLauncher = registerForActivityResult(CropImageActivityContracts){
            uri->
        if(uri != null){
            Log.d("uri picked","$uri")
            imageUri = uri
            loadImageUri()
        }


    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddPostBinding.inflate(inflater,container,false)
        val view = binding.root

        binding.ivAddPost.setOnClickListener{
            checkPermission()
        }

        binding.ivDoneAddPost.setOnClickListener{
            uploadImage()
        }

        binding.ivCloseAddPost.setOnClickListener{
            requireActivity().onBackPressed()
        }


        return view
    }

    private fun checkPermission(){
        permissionLauncher.launch(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    private fun imagePick(){
        cropImageResultLauncher.launch(null)
    }

    private fun loadImageUri(){
        if(imageUri != null)
            Log.d("ImageUri","$imageUri")
            Picasso.get().load(imageUri).into(binding.ivAddPost)
    }

    private fun uploadImage(){
        if(imageUri != null){
            binding.ivDoneAddPost.visibility = View.GONE
            binding.progressBarAddPost.visibility = View.VISIBLE
            lifecycleScope.launch(Dispatchers.IO){
                val uploadUri = ResizeImage(requireContext()).getCompressedImageUriFromUri(imageUri!!,400,false)
                val string = MyFireBaseStorage().uploadImg(uploadUri)
                if(string != ""){
                    val text = binding.etCaption.text.toString()
                    val post = Post(string,"",text, MyFireBaseAuth.getUserId())
                    val bool = MyFireBaseDatabase().uploadPost(post)
                    withContext(Dispatchers.Main){
                        binding.progressBarAddPost.visibility= View.GONE
                        binding.ivDoneAddPost.visibility = View.VISIBLE
                        if(bool ){
                            Toast.makeText(requireContext(),
                                "Post Uploaded Successfully",Toast.LENGTH_SHORT).show()
                                requireActivity().onBackPressed()
                        }else{
                            Toast.makeText(requireContext(),
                                "Something went wrong",Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }


        }else{
            Toast.makeText(requireContext(),"Please Pick an Image to Upload",Toast.LENGTH_SHORT).show()
        }
    }



}