package com.example.encrypews.adapters

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.encrypews.R
import com.example.encrypews.Utils.Constants
import com.example.encrypews.Utils.formatAsListItem
import com.example.encrypews.encryption.EncryptionDecryption
import com.example.encrypews.models.Inbox
import com.example.encrypews.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class InboxRVAdapter(private val context: Context, var list:List<Inbox>, private val currentUserId:String) :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var clickListener :ONCLICK? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = {id:Int -> LayoutInflater.from(parent.context).inflate(id,parent,false)}
        return when(viewType){
            NORMAL_VIEW ->{
                InboxViewHolder(inflater(R.layout.list_item_inbox))
            }
            NOT_WANTED_VIEW ->{
                EmptyViewHolder(inflater(R.layout.list_item_empty))
            }
            else ->{
                InboxViewHolder(inflater(R.layout.list_item_inbox))
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when(item.from){
            currentUserId->{
                Log.d("adptr cuid","currentuid")
            }else ->{
            Log.d("adptr cuid","currentnouid")

                    val ivInbox = holder.itemView.findViewById<ImageView>(R.id.civ_inbox)
                    val tvName = holder.itemView.findViewById<TextView>(R.id.tv_inbox_user_name)
                    val tvTime = holder.itemView.findViewById<TextView>(R.id.tv_inbox_time)
                    val tvCount = holder.itemView.findViewById<TextView>(R.id.tv_inbox_unread_count)
                    val tvmsg = holder.itemView.findViewById<TextView>(R.id.tv_inbox_last_chat)
                    holder.setIsRecyclable(false)
                    val msgReceived = EncryptionDecryption().decrypt(item.msg)
                    typingObserver(currentUserId,item.from,item,tvmsg,msgReceived)
                    if(item.imageUrl != ""){
                        Picasso.get().load(item.imageUrl).placeholder(R.drawable.usr_image_place_holder).into(ivInbox)
                    }else{
                        ivInbox.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.usr_image_place_holder))
                    }
                    tvName.text = item.userName
                    tvTime.text = item.time.formatAsListItem(context)
                    tvmsg.text = msgReceived
                    if(item.count != 0){
                        tvCount.visibility = View.VISIBLE
                        tvCount.text = item.count.toString()
                    }else{
                        tvCount.visibility = View.GONE
                    }

            holder.itemView.setOnClickListener {
                if (clickListener != null) {
                    clickListener!!.onClick(
                        User(
                            item.from,
                            item.name,
                            item.userName,
                            item.imageUrl
                        ),item
                    )
                }
            }
        }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = list[position]
        return when{
            (item.from == currentUserId) ->{
                 NOT_WANTED_VIEW
            }
            else ->{
                NORMAL_VIEW
            }
        }
    }

    inner class InboxViewHolder(view:View):RecyclerView.ViewHolder(view)
    inner class EmptyViewHolder(view:View):RecyclerView.ViewHolder(view)

    companion object{
        private val NORMAL_VIEW = 1
        private val NOT_WANTED_VIEW = 2
    }

    interface ONCLICK{
        fun onClick(userClicked:User,inboxItem:Inbox)
    }
    fun setOnClickListener(Listener:ONCLICK){
        clickListener = Listener
    }

    private fun typingObserver(ownUserId:String,friendId:String,item:Inbox,tvMsg:TextView,msgReceived:String){
        val ref = Firebase.database.reference.child(Constants.CHATS).child(friendId).child(ownUserId).child("typing")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val bool = snapshot.value  as Boolean?
                if(bool != null){
                    if(bool){
                        tvMsg.text  = context.resources.getString(R.string.Typing)
                        tvMsg.setTextColor(ContextCompat.getColor(context,R.color.green))
                    }else{
                        tvMsg.text  = msgReceived
                        tvMsg.setTextColor(ContextCompat.getColor(context,R.color.black))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("typingerr inbox",error.message)
            }

        })
    }


}