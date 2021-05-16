package com.example.encrypews.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.encrypews.R

import com.example.encrypews.databinding.ActivityAddPostBinding
import com.example.encrypews.firebase.MyFireBaseAuth
import com.example.encrypews.firebase.MyFireBaseDatabase
import com.example.encrypews.firebase.MyFireBaseStorage
import com.example.encrypews.fragments.HomeFragment
import com.example.encrypews.models.Post
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddPostActivity : AppCompatActivity() {

    private var imageUri : Uri? = null
    private lateinit var binding:ActivityAddPostBinding

    private var permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            granted ->
        if(granted){
            imagePick()
        }else{
            Toast.makeText(this,"Please Give Permissions to Post Images", Toast.LENGTH_SHORT).show()
        }

    }

    private var CropImageActivityContracts = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity().setAspectRatio(100,161).getIntent(this@AddPostActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent).uri
        }

    }

    private var cropImageResultLauncher = registerForActivityResult(CropImageActivityContracts){
            uri->
        imageUri = uri
        loadImageUri()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivAddPost.setOnClickListener{
            checkPermission()
        }

        binding.ivDoneAddPost.setOnClickListener{
            uploadImage()
        }

        binding.ivCloseAddPost.setOnClickListener{
            finish()
        }
    }

    private fun checkPermission(){
        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun imagePick(){
        cropImageResultLauncher.launch(null)
    }

    private fun loadImageUri(){
        if(imageUri != null)
            Picasso.get().load(imageUri).into(binding.ivAddPost)
    }

    private fun uploadImage(){
        if(imageUri != null){
            binding.ivDoneAddPost.visibility = View.GONE
            binding.progressBarAddPost.visibility = View.VISIBLE
            lifecycleScope.launch(Dispatchers.IO){
                val string = MyFireBaseStorage().uploadImg(imageUri)
                if(string != ""){
                    val text = binding.etCaption.text.toString()
                    val post = Post(string,"",text,MyFireBaseAuth.getUserId())
                    val bool = MyFireBaseDatabase().uploadPost(post)
                    withContext(Dispatchers.Main){
                        binding.progressBarAddPost.visibility= View.GONE
                        binding.ivDoneAddPost.visibility = View.VISIBLE
                        if(bool ){
                            Toast.makeText(this@AddPostActivity,
                                "Post Uploaded Successfully",Toast.LENGTH_SHORT).show()
                            finish()
                        }else{
                            Toast.makeText(this@AddPostActivity,
                                "Something went wrong",Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }


        }else{
            Toast.makeText(this,"Please Pick an Image to Upload",Toast.LENGTH_SHORT).show()
        }
    }


}