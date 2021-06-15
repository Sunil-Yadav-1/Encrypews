package com.example.encrypews.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.encrypews.R
import com.example.encrypews.databinding.UserListItemBinding
import com.example.encrypews.firebase.MyFireBaseAuth
import com.example.encrypews.models.User
import com.squareup.picasso.Picasso

class UserSearchRvAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListen : onClicklistener? = null
    val currentUserId = MyFireBaseAuth.getUserId()

    inner class MyViewHolder(view:View) : RecyclerView.ViewHolder(view)
    inner class EmptyViewHolder(view:View):RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = {id:Int -> LayoutInflater.from(parent.context).inflate(id,parent,false)}
        return when(viewType){
            NORMAL_VIEW -> MyViewHolder(inflater(R.layout.user_list_item))
            NOT_WANTED_VIEW->EmptyViewHolder(inflater(R.layout.list_item_empty))
            else ->MyViewHolder(inflater(R.layout.user_list_item))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = with(holder) {
//        val user = listUsers[position]
        val user = differ.currentList[position]
        if(user.id != currentUserId){
            val civList = itemView.findViewById<ImageView>(R.id.civ_list)
            val tvUserNameList = itemView.findViewById<TextView>(R.id.tv_user_name_list)
            val tvNameList = itemView.findViewById<TextView>(R.id.tv_name_list)
            if(user.userImage != ""){
                Picasso.get().load(user.userImage).placeholder(R.color.offWhite).into(civList)
            }else{
                civList.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.usr_image_place_holder))
            }
            tvNameList.setText(user.name)
            tvUserNameList.setText(user.userName)

            itemView.setOnClickListener{
                if(onClickListen != null){
                    onClickListen!!.onClickRv(differ.currentList[adapterPosition]) // using position instead of adapter positon was throwing
                    //error out of bound when users position change in results of search user
                    //my guess at this time is , as i am using diffutil and maybe the position was not set accurately
                }
            }
        }else{
            //do nothing
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
//        val user = listUsers[position]
        val user = differ.currentList[position]
        return if(user.id == currentUserId){
            NOT_WANTED_VIEW
        }else{
            NORMAL_VIEW
        }
    }

    interface onClicklistener{
        fun onClickRv(userClicked: User)
    }
    fun setOnClickListener(listener : onClicklistener){
        this.onClickListen = listener
    }

    companion object{
        private val NORMAL_VIEW = 1
        private val NOT_WANTED_VIEW = 2
    }

    private val differCallback = object :DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallback)
}