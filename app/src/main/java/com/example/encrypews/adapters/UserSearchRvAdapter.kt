package com.example.encrypews.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.encrypews.R
import com.example.encrypews.databinding.UserListItemBinding
import com.example.encrypews.models.User
import com.squareup.picasso.Picasso

class UserSearchRvAdapter(val context: Context) : RecyclerView.Adapter<UserSearchRvAdapter.MyViewHolder>() {
    private var onClickListen : onClicklistener? = null
    var listUsers = ArrayList<User>()

    inner class MyViewHolder(val binding:UserListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = UserListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = with(holder) {
        val user = listUsers[position]
       if(user.userImage != ""){
           Picasso.get().load(user.userImage).placeholder(R.color.offWhite).into(binding.civList)
       }else{
           binding.civList.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.usr_image_place_holder))
       }
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

    companion object{
        private val NORMAL_VIEW = 1
        private val NOT_WANTED_VIEW = 2
    }
}