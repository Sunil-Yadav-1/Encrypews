package com.example.encrypews.adapters

import android.content.Context
import android.content.ReceiverCallNotAllowedException
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.encrypews.databinding.ListPostBinding

class HomePageRVAdapter(private val context:Context)
    : RecyclerView.Adapter<HomePageRVAdapter.MyViewHolder>() {

    inner class MyViewHolder(binding:ListPostBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}