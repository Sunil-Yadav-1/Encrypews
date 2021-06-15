package com.example.encrypews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.encrypews.R
import com.example.encrypews.models.Article
import com.squareup.picasso.Picasso

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.MyViewHolder>(){

    inner class MyViewHolder(view:View):RecyclerView.ViewHolder(view)

    private val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean{
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

        val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_article_preview,parent,false))
    }

    override fun onBindViewHolder(holder: NewsAdapter.MyViewHolder, position: Int) {
        val article = differ.currentList[position]

        holder.itemView.apply {
            val ivArticleImage : ImageView =findViewById(R.id.ivArticleImage)
            val tvSource :TextView =findViewById(R.id.tvSource)
            val tvTitle : TextView = findViewById(R.id.tvTitle)
            val tvDescription : TextView = findViewById(R.id.tvDescription)
            val tvPublishedAt : TextView = findViewById(R.id.tvPublishedAt)
                Picasso.get().load(article.urlToImage).into(ivArticleImage)
            tvSource.text =article.source?.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
            setOnClickListener{
                onItemClickListener?.let{it(article)}
            }
        }
    }

    private var onItemClickListener: ((Article)->Unit)? = null

    fun setOnItemClickListener(listener :(Article) -> Unit){
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
       return   differ.currentList.size
    }

}