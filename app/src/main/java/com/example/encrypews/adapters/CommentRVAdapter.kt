package com.example.encrypews.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.encrypews.R
import com.example.encrypews.databinding.ActivityCommentBinding
import com.example.encrypews.databinding.CommentListBinding
import com.example.encrypews.firebase.MyFireBaseDatabase
import com.example.encrypews.models.Comment
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentRVAdapter(val context:Context) : RecyclerView.Adapter<CommentRVAdapter.MyViewHolder>(){
    var listComments = ArrayList<Comment>()
    inner class MyViewHolder(val binding: CommentListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CommentListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int): Unit = with(holder) {
        val comment = listComments[position]
        CoroutineScope(Dispatchers.IO).launch {
            val user = MyFireBaseDatabase().loadUser(comment.publishedBy)
            withContext(Dispatchers.Main){
                binding.tvUserNameList.text = user.userName
                if(user.userImage != ""){
                    Picasso.get().load(user.userImage).placeholder(R.drawable.usr_image_place_holder).into(binding.civList)
                }else{
                    binding.civList.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.usr_image_place_holder))
                }
            }
        }

        binding.tvComment.text = comment.comment

    }

    override fun getItemCount(): Int {
       return listComments.size
    }
}