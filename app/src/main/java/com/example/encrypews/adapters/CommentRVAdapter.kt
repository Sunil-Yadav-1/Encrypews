package com.example.encrypews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.encrypews.databinding.ActivityCommentBinding
import com.example.encrypews.databinding.CommentListBinding
import com.example.encrypews.firebase.MyFireBaseDatabase
import com.example.encrypews.models.Comment
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentRVAdapter() : RecyclerView.Adapter<CommentRVAdapter.MyViewHolder>(){
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
                Picasso.get().load(user.userImage).into(binding.civList)
            }
        }

        binding.tvComment.text = comment.comment

    }

    override fun getItemCount(): Int {
       return listComments.size
    }
}