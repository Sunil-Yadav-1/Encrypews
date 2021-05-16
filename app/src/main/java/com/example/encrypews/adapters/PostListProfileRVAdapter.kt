package com.example.encrypews.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.encrypews.databinding.PostsListItemBinding
import com.example.encrypews.models.Post
import com.squareup.picasso.Picasso

class PostListProfileRVAdapter : RecyclerView.Adapter<PostListProfileRVAdapter.MyViewHolder>(){
   var posts : ArrayList<Post> = ArrayList()




    inner class MyViewHolder(val binding : PostsListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        Log.e("oncreatevh","...")
        val binding = PostsListItemBinding
            .inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)= with(holder) {
        val post = posts[position]
        Picasso.get().load(post.imageUrl).into(binding.ivPostProfile)

    }

    override fun getItemCount(): Int {
        return posts.size
    }
}