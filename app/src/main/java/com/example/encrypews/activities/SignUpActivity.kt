package com.example.encrypews.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.encrypews.R
import com.example.encrypews.constants.Constants
import com.example.encrypews.databinding.ActivitySignUpBinding
import com.example.encrypews.firebase.MyFireBaseAuth
import com.example.encrypews.firebase.MyFireBaseDatabase
import com.example.encrypews.models.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAppFont()
        setSpanningString()
        enabledisablebutton()
        binding.btnSignUp.setOnClickListener{
            signUpUser()
        }
    }

    private fun signUpUser(){
        val name = binding.etSignUpUserName.text.toString().trim{it<=' '}
        val userName = binding.etSignUpUserId.text.toString().trim{it<=' '}
        val email= binding.etSignUpEmail.text.toString().trim{it<=' '}
        val password= binding.etSignUpPassword.text.toString()
        if(validateForm(name,userName,email,password)){
            binding.btnSignUp.startAnimation()
           MyFireBaseAuth.auth!!.createUserWithEmailAndPassword(email,password).addOnCompleteListener{ task->
               if(task.isSuccessful){
                   val user: User =User()
                   user.name = name
                   user.email = email
                   user.userName = userName
                   MyFireBaseDatabase().createUser(this,user)
               }else{
                   showSnackBar("Error ${task.exception!!.message.toString()}")
                   binding.btnSignUp.revertAnimation()
               }

           }

        }else{
            showSnackBar("Please Enter Valid Credentials")
        }
    }

     fun userCreatedSuccesfully(result:Boolean,userInfo : User?){
         binding.btnSignUp.revertAnimation()
         if(result){

             val intent = Intent(this@SignUpActivity,MainActivity::class.java)
             intent.putExtra(Constants.USER_ID,userInfo?.id)
             startActivity(intent)
             finish()
         }else{
             showSnackBar("Error Creating Account")
         }
    }

    private fun validateForm(name: String,userId:String,email:String,password:String) : Boolean{
        return when{
            TextUtils.isEmpty(name) ->{
                false
            }
            TextUtils.isEmpty(userId) ->{
                false
            }
            TextUtils.isEmpty(email) ->{
                false
            }
            TextUtils.isEmpty(password) ->{
                false
            }
            else ->{
                true
            }
        }
    }




    private fun setSpanningString() {
        val span = SpannableString(getString(R.string.sign_in_prompt_text))

        val clickableSpan = object : ClickableSpan(){
            override fun onClick(widget: View) {
                startActivity(Intent(this@SignUpActivity,SignInActivity::class.java))
                finish()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ds.linkColor
                ds.isUnderlineText= false
            }
        }

        span.setSpan(clickableSpan,span.length-7,span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvSignInPrompt.movementMethod = LinkMovementMethod.getInstance()
        binding.tvSignInPrompt.text = span
    }


    private fun setAppFont(){
        val typeface = Typeface.createFromAsset(assets,"billabong.ttf")
        binding.tvSignupAppName.typeface = typeface
        binding.tvSignupAppName.text= getString(R.string.app_name)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun enabledisablebutton() {
        var b1= false
        var b2 = false
        var b3= false
        var b4 = false
        binding.etSignUpUserName.addTextChangedListener(){
            if(!(it.isNullOrEmpty())){
                b1= true
            }else{
                b1 = false
            }
            if(b1&&b2&&b3&&b4){
                binding.btnSignUp.isEnabled = true
                binding.btnSignUp.background = getDrawable(R.drawable.shape_button_rounded_2)
                binding.btnSignUp.setTextColor(getColor(R.color.white))

            }else{
                binding.btnSignUp.isEnabled = false
                binding.btnSignUp.background = getDrawable(R.drawable.shape_button_rounded)
                binding.btnSignUp.setTextColor(getColor(R.color.grey))
            }
        }

        binding.etSignUpEmail.addTextChangedListener(){
            if(!(it.isNullOrEmpty())){
                b2= true
            }else{
                b2 = false
            }
            if(b1&&b2&&b3&&b4){
                binding.btnSignUp.isEnabled = true
                binding.btnSignUp.background = getDrawable(R.drawable.shape_button_rounded_2)
                binding.btnSignUp.setTextColor(getColor(R.color.white))

            }else{
                binding.btnSignUp.isEnabled = false
                binding.btnSignUp.background = getDrawable(R.drawable.shape_button_rounded)
                binding.btnSignUp.setTextColor(getColor(R.color.grey))
            }
        }

        binding.etSignUpUserId.addTextChangedListener(){
            if(!(it.isNullOrEmpty())){
                b3= true
            }else{
                b3 = false
            }
            if(b1&&b2&&b3&&b4){
                binding.btnSignUp.isEnabled = true
                binding.btnSignUp.background = getDrawable(R.drawable.shape_button_rounded_2)
                binding.btnSignUp.setTextColor(getColor(R.color.white))

            }else{
                binding.btnSignUp.isEnabled = false
                binding.btnSignUp.background = getDrawable(R.drawable.shape_button_rounded)
                binding.btnSignUp.setTextColor(getColor(R.color.grey))
            }
        }

        binding.etSignUpPassword.addTextChangedListener(){
            if(!(it.isNullOrEmpty())){
                b4= true
            }else{
                b4 = false
            }
            if(b1&&b2&&b3&&b4){
                binding.btnSignUp.isEnabled = true
                binding.btnSignUp.background = getDrawable(R.drawable.shape_button_rounded_2)
                binding.btnSignUp.setTextColor(getColor(R.color.white))

            }else{
                binding.btnSignUp.isEnabled = false
                binding.btnSignUp.background = getDrawable(R.drawable.shape_button_rounded)
                binding.btnSignUp.setTextColor(getColor(R.color.grey))
            }
        }

    }

    fun showSnackBar(string: String){
        val snackbar = Snackbar.make(findViewById(android.R.id.content),
            string,Snackbar.LENGTH_SHORT)
        val snackBarView = snackbar.view

        snackBarView.setBackgroundColor(ContextCompat.getColor(this,
            R.color.snackbar_error_color))
        snackbar.show()
    }

    private fun showToast(string : String){
        Toast.makeText(this@SignUpActivity,string,Toast.LENGTH_SHORT).show()
    }
}