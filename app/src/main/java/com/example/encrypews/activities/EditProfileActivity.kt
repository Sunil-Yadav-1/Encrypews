package com.example.encrypews.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.encrypews.R
import com.example.encrypews.constants.Constants
import com.example.encrypews.databinding.ActivityEditProfileBinding
import com.example.encrypews.firebase.MyFireBaseDatabase
import com.example.encrypews.firebase.MyFireBaseStorage
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage


class EditProfileActivity : AppCompatActivity() {
//    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
//        if(result.resultCode == Activity.RESULT_OK){
//            val data = result.data
//        }
//    }

    private var croppedImageUri : Uri? = null
    private var uploadedImageUrl : String = ""
    private lateinit var binding:ActivityEditProfileBinding

    private var userName: String =""
    private  var nameUser : String=""
    private  var userBio : String=""



    //to use cropImage library in our edit profile
    private val cropImageActivityContracts = object: ActivityResultContract<Any?,Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity().setAspectRatio(1,1).getIntent(this@EditProfileActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent).uri
        }

    }

        //launcher for crop image  activity contract
    private var cropImageActivityContractLauncher = registerForActivityResult(cropImageActivityContracts){
        it.let{uri->
            croppedImageUri = uri
            loadImageUri()
        }
    }

    //launcher to check for permissions
    private var permissionContractLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){granted->
        if(granted){
            imagePick()
        }else{
            Toast.makeText(this,"Please give Image permission!!",Toast.LENGTH_SHORT).show()
        }

    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(intent.hasExtra(Constants.USEREP)){
            nameUser = intent.getStringExtra(Constants.USEREP).toString()
        }
        if(intent.hasExtra(Constants.USERNAMEEP)){
            userName = intent.getStringExtra(Constants.USERNAMEEP).toString()
        }
        if(intent.hasExtra(Constants.BIOEP)){
            userBio = intent.getStringExtra(Constants.BIOEP).toString()
        }
        if(intent.hasExtra(Constants.USEREP)){
            uploadedImageUrl = intent.getStringExtra(Constants.USERIMURL).toString()
        }

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        populateActivity()

        binding.civImageEditProfile.setOnClickListener{
            checkPermission()
        }
        binding.tvChangeProfileDp.setOnClickListener{
            checkPermission()
        }

        binding.ivDone.setOnClickListener{
            it.visibility = GONE
            binding.progressBarEditProfile.visibility = VISIBLE
            if(croppedImageUri != null){
                MyFireBaseStorage().uploadImage(this,croppedImageUri)
            }else{
                uploadDetails()
            }
        }

        binding.ivClose.setOnClickListener{
            finish()
        }


    }

    private fun checkPermission(){
        permissionContractLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }


    private fun imagePick(){
//        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        resultLauncher.launch(intent)
        cropImageActivityContractLauncher.launch(null)
    }

    private fun loadImageUri(){
        if(croppedImageUri != null){
            Picasso.get().load(croppedImageUri).into(binding.civImageEditProfile)
        }
    }
    private fun loadImage(){
        if(uploadedImageUrl != ""){
            Picasso.get().load(uploadedImageUrl).placeholder(R.drawable.usr_image_place_holder).into(binding.civImageEditProfile)
        }
    }

    private fun validateForm(name:String,userName:String) : Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showSnackBar("Name can not be empty")
                false
            }
            TextUtils.isEmpty(userName)->{
                showSnackBar("UserName can not be empty")
                false
            }else->{
                true
            }
        }
    }

    private  fun uploadDetails(){
        val name = binding.etNameEp.text.toString()
        val uname = binding.etUserNameEp.text.toString()
        val bio = binding.etBioEp.text.toString()

        if(name != nameUser || uname!= userName || bio != userBio || uploadedImageUrl!=""){
            if(validateForm(name,uname)) {


                    val userMap = HashMap<String, Any>()
                userMap[Constants.NAME] = name
                userMap[Constants.USERNAME] = uname
                userMap[Constants.USERBIO] = bio
                userMap[Constants.USERIMAGE] = uploadedImageUrl

                MyFireBaseDatabase().updateUser(this, userMap)
            }
        }else{
            userUpdateDone(true)
        }



    }

    private fun populateActivity(){
        binding.etNameEp.setText(nameUser)
        binding.etUserNameEp.setText(userName)
        binding.etBioEp.setText(userBio)
        loadImage()

    }

    private fun showSnackBar(string: String){
        val snackbar = Snackbar.make(findViewById(android.R.id.content),string,
        Snackbar.LENGTH_SHORT)
        val snackBarView  = snackbar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_error_color))
        snackbar.show()
    }

    fun userUpdateDone(success:Boolean){
        binding.progressBarEditProfile.visibility = GONE
        binding.ivDone.visibility = VISIBLE
        if(success){
            finish()
        }else{
            showSnackBar("Error Updating Details")
        }
    }

    fun imageUploaded(result:Boolean,url: String){
        if(result){
            uploadedImageUrl = url
            uploadDetails()

        }else{
            showSnackBar("Error in Uploading Image")
            uploadDetails()
        }
    }


}