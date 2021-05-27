package com.example.encrypews.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.encrypews.R
import com.example.encrypews.adapters.CommentRVAdapter
import com.example.encrypews.Utils.Constants
import com.example.encrypews.databinding.ActivityCommentBinding
import com.example.encrypews.firebase.MyFireBaseAuth
import com.example.encrypews.models.Comment
import com.example.encrypews.models.Post
import com.example.encrypews.viewmodels.CommentActivityViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentActivity : AppCompatActivity() {
    private lateinit var post: Post
    private lateinit var binding:ActivityCommentBinding
    private lateinit var viewModel:CommentActivityViewModel
    private lateinit var adapter : CommentRVAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(intent.hasExtra(Constants.POSTEXTRA)){
            post = intent.getParcelableExtra<Post>(Constants.POSTEXTRA)!!
        }
        binding = ActivityCommentBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(CommentActivityViewModel::class.java)
        adapter = CommentRVAdapter(this)
        binding.rvComments.layoutManager = LinearLayoutManager(this)
        binding.rvComments.hasFixedSize()
        binding.rvComments.setItemViewCacheSize(20)
        binding.rvComments.isDrawingCacheEnabled = true
        binding.rvComments.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        binding.rvComments.adapter = adapter
        setUpActionBar()
        setContentView(binding.root)
        viewModel.viewModelScope.launch (Dispatchers.IO){
            viewModel.loadUser()
        }

        viewModel.user.observe(this, Observer {
            if(it.userImage !=""){
                Picasso.get().load(it.userImage).placeholder(R.color.grey).into(binding.civComment)
            }
        })



        binding.ibPost.setOnClickListener{
            val text = binding.etComment.text.toString()
            if(validateform(text)){
                binding.ibPost.visibility = View.GONE
                binding.commentPbar.visibility= View.VISIBLE
                postComment(text,post.postId)
            }
        }
        viewModel.userListEventListener(post.postId)

        viewModel.commentList.observe(this, Observer { list->
            Log.e("observer comment","$list")
            adapter.listComments = list as ArrayList<Comment>
            adapter.notifyDataSetChanged()

        })
    }

    private fun validateform(text:String):Boolean{
        return when{
            TextUtils.isEmpty(text)->{
                showSnackBar("Please write something!")
                false
            }
            else ->{
                true
            }
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarComment)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }
        binding.toolbarComment.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    private fun postComment(text: String,postId:String){
        val comment = Comment(MyFireBaseAuth.getUserId(),text,postId)
        viewModel.viewModelScope.launch (Dispatchers.IO){
            viewModel.postComment(comment)
            withContext(Dispatchers.Main){
                binding.commentPbar.visibility= View.GONE
                binding.ibPost.visibility =View.VISIBLE
                binding.etComment.setText("")
            }
        }
    }

    private fun showSnackBar(string: String){
        val snackbar = Snackbar.make(findViewById(android.R.id.content),string,
            Snackbar.LENGTH_SHORT)
        val snackBarView  = snackbar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_error_color))
        snackbar.show()
    }
}