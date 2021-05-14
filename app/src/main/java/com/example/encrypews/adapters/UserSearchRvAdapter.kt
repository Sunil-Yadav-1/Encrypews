package com.example.encrypews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.encrypews.databinding.UserListItemBinding
import com.example.encrypews.models.User
import com.squareup.picasso.Picasso

class UserSearchRvAdapter : RecyclerView.Adapter<UserSearchRvAdapter.MyViewHolder>() {
    private var onClickListen : onClicklistener? = null
    var listUsers = ArrayList<User>()

    inner class MyViewHolder(val binding:UserListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = UserListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = with(holder) {
        val user = listUsers[position]
       Picasso.get().load(user.userImage).into(binding.civList)
        binding.tvNameList.setText(user.name)
        binding.tvUserNameList.setText(user.userName)

        itemView.setOnClickListener{
            if(onClickListen != null){
                onClickListen!!.onClickRv(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return listUsers.size
    }

    interface onClicklistener{
        fun onClickRv(position: Int)
    }
    fun setOnClickListener(listener : onClicklistener){
        this.onClickListen = listener
    }
}