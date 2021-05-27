package com.example.encrypews.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.encrypews.R
import com.example.encrypews.Utils.formatAsTime
import com.example.encrypews.encryption.EncryptionDecryption
import com.example.encrypews.models.ChatEvent
import com.example.encrypews.models.DateHeader
import com.example.encrypews.models.Messages

class ChatActivityRVAdapter(private val list:MutableList<ChatEvent>,private val currentUId:String)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>()

{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TEXT_MESSAGE_RECEIVED ->{
               val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_recieve_message,parent,false)
                return  MessageViewHolder(view)
            }
            TEXT_MESSAGE_SENT->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_send_message,parent,false)
                return  MessageViewHolder(view)
            }
            DATE_HEADER ->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_date_header,parent,false)
                return  DateViewHolder(view)
            }
            else ->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_recieve_message,parent,false)
                return  MessageViewHolder(view)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = list[position]){
            is Messages->{
                if(item.senderId == currentUId){
                    val tvContentSend : TextView = holder.itemView.findViewById(R.id.tv_content_send)
                    val tvTimeSend :TextView = holder.itemView.findViewById(R.id.tv_time_send)
                    if(item.msg != "")
                        tvContentSend.text = EncryptionDecryption().decrypt(item.msg)
                    tvTimeSend.text = item.sentAt.formatAsTime()
                }else{
                    val tvContentReceive : TextView = holder.itemView.findViewById(R.id.tv_content_receive)
                    val tvTimeRecieve :TextView = holder.itemView.findViewById(R.id.tv_time_receive)
                    if(item.msg != "")
                        tvContentReceive.text = EncryptionDecryption().decrypt(item.msg)
                    tvTimeRecieve.text = item.sentAt.formatAsTime()
                }
            }
            is DateHeader->{
                val tvHeader = holder.itemView.findViewById<TextView>(R.id.tv_header)
                tvHeader.text = item.date

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(val event = list[position]){
            is Messages ->{
                if(event.senderId == currentUId){
                    TEXT_MESSAGE_SENT
                }else{
                    TEXT_MESSAGE_RECEIVED
                }
            }

            is DateHeader ->{
                DATE_HEADER
            }
            else->{
                UNSUPPORTED
            }
        }

    }

    companion object{
        private val UNSUPPORTED = -1
        private val TEXT_MESSAGE_RECEIVED = 0
        private val TEXT_MESSAGE_SENT = 1
        private val DATE_HEADER = 2
    }

//    inner class TextReceivedViewHolder(val binding:ListItemChatRecieveMessageBinding):RecyclerView.ViewHolder(binding.root)
//
//    inner class TextSentViewHolder(val binding:ListItemChatSendMessageBinding):RecyclerView.ViewHolder(binding.root)
//
//    inner class DateHeaderViewHolder(val binding : ListItemDateHeaderBinding):RecyclerView.ViewHolder(binding.root)

    inner class DateViewHolder(view:View):RecyclerView.ViewHolder(view)
    inner class MessageViewHolder(view: View):RecyclerView.ViewHolder(view)

}
