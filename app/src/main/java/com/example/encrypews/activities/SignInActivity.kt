package com.example.encrypews.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.encrypews.R
import com.example.encrypews.Utils.Constants
import com.example.encrypews.databinding.ActivitySignInBinding
import com.example.encrypews.firebase.MyFireBaseAuth
import com.example.encrypews.firebase.MyFireBaseDatabase
import com.example.encrypews.firebase.MyFirebaseMessagingService
import com.example.encrypews.models.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        if(MyFireBaseAuth.getUserId() != ""){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setTypeFace()
        setSpanningString()
        textChangeListener()
        binding.btnSignIn.setOnClickListener{
            signInUser()
        }


    }

    private fun signInUser() {
        val email = binding.etSignInEmail.text.toString().trim{it<=' '}
        val password = binding.etSignInPassword.text.toString().trim{it<=' '}
        if(validateForm(email,password)){
            binding.btnSignIn.startAnimation()
            MyFireBaseAuth.auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        getTokenandUpdate()
                        lifecycleScope.launch(Dispatchers.IO){
                            val user = MyFireBaseDatabase().loadUser()

                            withContext(Dispatchers.Main){
                                putInSharedPref(user)
                                binding.btnSignIn.revertAnimation()
                                startActivity(Intent(this@SignInActivity,MainActivity::class.java))
                                finish()
                            }
                        }


                    }else{
                        binding.btnSignIn.revertAnimation()
                        showSnackBar("Error  ${task.exception!!.message.toString()}")
                    }

            }
        }else{
            showSnackBar("Please Enter Valid Credentials")
        }
    }


    private fun setTypeFace(){
        val typeface = Typeface.createFromAsset(assets,"billabong.ttf")
        binding.tvSigninAppName.typeface =typeface
        binding.tvSigninAppName.text = getString(R.string.app_name)
    }

    private fun setSpanningString(){
        val span = SpannableString(getString(R.string.sign_up_prompt_text))

        val clickableSpan = object: ClickableSpan(){
            override fun onClick(widget: View) {
               startActivity(Intent(this@SignInActivity,SignUpActivity::class.java))
                finish()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText= false
                ds.color = ds.linkColor
            }
        }
        span.setSpan(clickableSpan,span.length-7,span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvPromptSignin.movementMethod = LinkMovementMethod.getInstance()
         binding.tvPromptSignin.text = span
    }


    private fun textChangeListener() {
        var boolean_one = false
        var boolean_two = false
        binding.etSignInEmail.addTextChangedListener{
            boolean_one = !it.isNullOrEmpty()
            if(boolean_one&&boolean_two){
                binding.btnSignIn.isEnabled = true
                binding.btnSignIn.background = getDrawable(R.drawable.shape_button_rounded_2)
                binding.btnSignIn.setTextColor(ContextCompat.getColor(this,R.color.white))

            }else{
                binding.btnSignIn.isEnabled = false
                binding.btnSignIn.background = getDrawable(R.drawable.shape_button_rounded)
                binding.btnSignIn.setTextColor(ContextCompat.getColor(this,R.color.grey))
            }
        }

        binding.etSignInPassword.addTextChangedListener{
            boolean_two = !it.isNullOrEmpty()
            if(boolean_one&&boolean_two){
                binding.btnSignIn.isEnabled = true
                binding.btnSignIn.background = getDrawable(R.drawable.shape_button_rounded_2)
                binding.btnSignIn.setTextColor(ContextCompat.getColor(this,R.color.white))

            }else{
                binding.btnSignIn.isEnabled = false
                binding.btnSignIn.background = getDrawable(R.drawable.shape_button_rounded)
                binding.btnSignIn.setTextColor(ContextCompat.getColor(this,R.color.grey))
            }
        }
    }


    private fun validateForm(email:String,password:String) : Boolean{
        return when{

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





    private fun showSnackBar(string: String){
        val snackbar = Snackbar.make(findViewById(android.R.id.content),
            string, Snackbar.LENGTH_SHORT)
        val snackBarView = snackbar.view

        snackBarView.setBackgroundColor(ContextCompat.getColor(this,
            R.color.snackbar_error_color))
        snackbar.show()
    }

    private fun putInSharedPref(user: User){
        val spref = getSharedPreferences(Constants.SHARED_PREFERENCE_USER, MODE_PRIVATE)
        val gson = Gson()
        val json = gson.toJson(user)
        spref.edit().apply{
            putString(Constants.SP_USER,json)
        }.apply()

    }
    private fun getTokenandUpdate() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("getToken", "Failed")
                return@addOnCompleteListener
            }
            val token = task.result
            val sharedPreferences = getSharedPreferences(
                Constants.SHARED_PREFERENCE_USER,
                MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putString(Constants.SP_TOKEN, token)
            editor.apply()
            editor.commit()

            val ref = Firebase.database.reference.child(Constants.USERS)
            ref.child(MyFireBaseAuth.getUserId()).child("deviceToken").setValue(token)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.e("tokenUpdate", "failedSignup")
                        return@addOnCompleteListener
                    }
                    Log.d("tokenUpdate", "Success")

                }

        }
    }


}