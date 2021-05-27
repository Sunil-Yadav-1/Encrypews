package com.example.encrypews.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.encrypews.R
import com.example.encrypews.Utils.Constants
import com.example.encrypews.adapters.InboxRVAdapter
import com.example.encrypews.databinding.ActivityInboxBinding
import com.example.encrypews.models.Inbox
import com.example.encrypews.models.User
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class InboxActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityInboxBinding.inflate(layoutInflater)
    }

    private val inboxList by lazy {
        mutableListOf<Inbox>()
    }

    private lateinit var ownUser:User


    private lateinit var inboxRef:DatabaseReference


    private  var finalList:List<Inbox> = ArrayList<Inbox>()

    private lateinit var adapter: InboxRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
         ownUser  = intent.getParcelableExtra<User>(Constants.OWN_USER)!!
        inboxRef= Firebase.database.reference.child(Constants.CHATS).child(ownUser.id)
        setUpActionBar()

        inboxRef.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.llPbar.visibility = View.GONE
                binding.rvInbox.visibility=View.VISIBLE
                if(inboxList.isNotEmpty()){
                    inboxList.sortWith(compareBy<Inbox>{it.time.time})
                    finalList = inboxList.reversed()
                    adapter.list = finalList
                    adapter.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        inboxRef.orderByKey().addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e("snapshot child","$snapshot")
                val inbox  = snapshot.getValue(Inbox::class.java)
                if (inbox != null) {
                    addInbox(inbox)
                }
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val inbox = snapshot.getValue(Inbox::class.java)
                Log.d("inbox childadded","$inbox")
                if (inbox != null) {
                    replaceInbox(inbox)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        binding.rvInbox.layoutManager = LinearLayoutManager(this)
        adapter= InboxRVAdapter(this,finalList,ownUser.id)
        adapter.setOnClickListener(object:InboxRVAdapter.ONCLICK{
            override fun onClick(userClicked: User,inboxItem:Inbox) {
                startChatActivity(userClicked,inboxItem)
            }

        })
        binding.rvInbox.adapter = adapter

    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarInbox)
        val actionBar = supportActionBar
        if(actionBar!= null){
            actionBar.setTitle(ownUser.userName)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }
        binding.toolbarInbox.setNavigationOnClickListener{
            onBackPressed()
        }

    }

    private fun addInbox(inbox:Inbox){
        inboxList.add(inbox)
        finalList = inboxList.reversed()
        adapter.list =finalList
        adapter.notifyDataSetChanged()
        Log.d("datasetchanged","$finalList")

    }


    private fun replaceInbox(inbox:Inbox){
        Log.e("ReplaceInbx","Called")
       val element = inboxList.find{
            it.from ==inbox.from
        }
        Log.d("inbox Element","$inbox")
        inboxList.remove(element)
        inboxList.add(inbox)
        finalList = inboxList.reversed()
        adapter.list = finalList
        adapter.notifyDataSetChanged()
        Log.e("finalListRpl","{$finalList}")
    }

    private fun startChatActivity(userClicked:User,inboxItem:Inbox){
        val intent = Intent(this,ChatActivity::class.java)
        intent.putExtra(Constants.FRIEND_USER,userClicked)
        intent.putExtra(Constants.INBOX_MESSAGE,inboxItem.msg)
        val json = Gson().toJson(inboxItem.time)
        intent.putExtra(Constants.INBOX_TIME,json)
        startActivity(intent)
    }


}