package com.example.encrypews.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.encrypews.R
import com.example.encrypews.Utils.Constants
import com.example.encrypews.Utils.isSameDay
import com.example.encrypews.adapters.ChatActivityRVAdapter
import com.example.encrypews.viewmodelfactory.ChatViewModelFactory
import com.example.encrypews.databinding.ActivityChatBinding
import com.example.encrypews.encryption.EncryptionDecryption
import com.example.encrypews.firebase.MyFireBaseAuth
import com.example.encrypews.models.*
import com.example.encrypews.viewmodels.ChatActivityViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.coroutines.*
import java.util.*

class ChatActivity : AppCompatActivity() {
    private var _typingInfo = MutableLiveData<Boolean>()
    private val typingInfo:LiveData<Boolean> = _typingInfo
    private val binding by lazy {
       ActivityChatBinding.inflate(layoutInflater)
    }

    private var speechRecognizerContract = object :ActivityResultContract<Any?,ArrayList<String>?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())
            return intent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): ArrayList<String>? {
            if(resultCode == Activity.RESULT_OK){
                return intent?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            }else{
                Toast.makeText(this@ChatActivity,"Failed to get speech to text",Toast.LENGTH_SHORT).show()
            }
        return null
        }

    }

    private val speechRecognizerLauncher = registerForActivityResult(speechRecognizerContract){list->
        if(list != null && list.isNotEmpty()){
            binding.etMessage.setText(list.get(0))
        }

    }

    private lateinit var friendUserByInbox : User
         


    private lateinit var messageInbox:String
    private lateinit var timeInbox:Date
    private lateinit var listener:ChildEventListener
    private lateinit var listenerTwo:ValueEventListener
    private lateinit var adapter:ChatActivityRVAdapter
    private var currentUser = User()
    private val viewModel by lazy{
        ViewModelProvider(this, ChatViewModelFactory(friendUserByInbox!!)).get(ChatActivityViewModel::class.java)
    }

    private val messageList by lazy{
        mutableListOf<ChatEvent>()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(GoogleEmojiProvider())
        setContentView(binding.root)
        friendUserByInbox =intent.getParcelableExtra<User>(Constants.FRIEND_USER)!! //dont use user image and bio as when loaded through inbox activity
        //we dont have that info so it will be null her
        friendUserByInbox?.let { setUpActionBar(it) }
        viewModel.loadUser() // gets the friend user from db

        viewModel.friendUser.observe(this, Observer {
            friendUserByInbox = it

            if(messageInbox != ""){
                Firebase.database.reference.child(Constants.CHATS).child(currentUser.id)
                    .child(friendUserByInbox!!.id).child("imageUrl").setValue(friendUserByInbox!!.userImage)
            }
        })

        messageInbox =
            if(intent.hasExtra(Constants.INBOX_MESSAGE))
                intent.getStringExtra(Constants.INBOX_MESSAGE)!!
            else
                ""
        val gson = Gson()
            timeInbox = if(intent.hasExtra(Constants.INBOX_TIME)) {
                gson.fromJson(intent.getStringExtra(Constants.INBOX_TIME), Date::class.java)
            }else{
                Date()
            }


//        CoroutineScope(Dispatchers.IO).launch{
//           currentUser = viewModel.loadUser()
//            withContext(Dispatchers.Main){
//                adapter = ChatActivityRVAdapter(messageList,currentUser.id)
//                binding.rvChat.layoutManager = LinearLayoutManager(this@ChatActivity)
//                binding.rvChat.adapter = adapter
//                listentoMsgs(friendUser!!.id)
//            }
//        }
                getUser()
                setchatOpen(true)
                adapter = ChatActivityRVAdapter(messageList,currentUser.id)
                binding.rvChat.layoutManager = LinearLayoutManager(this@ChatActivity)
                binding.rvChat.adapter = adapter
                 listentoMsgs(friendUserByInbox!!.id)


        binding.ivBtnSend.setOnClickListener{
            binding.etMessage.text?.apply {
                if(this.isNotEmpty()){
                    sendMessage(this.toString())
                    this.clear()
                }
            }
        }

        binding.etMessage.addTextChangedListener{
            val refTwo = Firebase.database.reference.child(Constants.CHATS).child(currentUser.id).child(friendUserByInbox!!.id)
                .child("typing")
            refTwo.setValue(true)
            CoroutineScope(Dispatchers.IO).launch {
                delay(2000)
                refTwo.setValue(false)
            }
        }
        binding.ivSpchTxt.setOnClickListener{
            speechRecognizerLauncher.launch(null)
        }

        typingInfo.observe(this, Observer { bool->
            if(bool){
                binding.tvToolbarName.text = resources.getString(R.string.Typing)
                binding.tvToolbarName.setTextColor(ContextCompat.getColor(this,R.color.green))
            }else{
                binding.tvToolbarName.text = friendUserByInbox!!.name
                binding.tvToolbarName.setTextColor(ContextCompat.getColor(this,R.color.black))
            }
        })

        val emojiPopup = EmojiPopup.Builder.fromRootView(binding.root).build(binding.etMessage)
        binding.ivEmoji.setOnClickListener{
            emojiPopup.toggle()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMessage(text:String){
        val encodedMsg = EncryptionDecryption().encrypt(text)
        Log.d("encoded msg",encodedMsg)
        Log.d("decoded msg",EncryptionDecryption().decrypt(encodedMsg))
        val message = Messages(encodedMsg,"","")
        viewModel.sendMessage(message,currentUser)


    }


  private  fun listentoMsgs(friendID: String){
        val ref = Firebase.database.reference.child(Constants.MESSAGES).child(getId(friendID))
        val singleListener = ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value != null){
                    markasRead()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
      val refTwo = Firebase.database.reference.child(Constants.CHATS).child(friendID).child(currentUser.id).child("typing")
      listenerTwo = refTwo.addValueEventListener(object :ValueEventListener{
          override fun onDataChange(snapshot: DataSnapshot) {
              Log.d("snapshot typing","$snapshot")
              if(snapshot.value != null)
                 _typingInfo.value = snapshot.value as Boolean
          }

          override fun onCancelled(error: DatabaseError) {
              Log.e("Typing error",error.message)
          }

      })

        listener= ref.orderByKey().addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val msg = snapshot.getValue(Messages::class.java)
                if (msg != null) {
                    addMessage(msg)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //do nothing
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //do nothing
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //do nothing
            }

            override fun onCancelled(error: DatabaseError) {
                //do nothing
            }

        })
    }

    private fun addMessage(msg:Messages){
        messageInbox = msg.msg
        timeInbox = msg.sentAt
        val lastEvent = messageList.lastOrNull()
        Log.d("lastEvent","$lastEvent")
        if((lastEvent!=null && !lastEvent.sentAt.isSameDay(msg.sentAt)) || lastEvent == null){
            messageList.add(
                DateHeader(msg.sentAt,this)
            )
        }
        messageList.add(msg)
        Log.d("message LIst","$messageList")
        adapter.notifyItemInserted(messageList.size-1)
        binding.rvChat.scrollToPosition(messageList.size-1)

    }

    private fun markasRead(){
        val ref1= Firebase.database.reference.child(Constants.CHATS).child(currentUser.id).child(friendUserByInbox!!.id)
        Log.d("ref1","$ref1")
        ref1.child(Constants.COUNT).setValue(0)

    }


   private fun getId(friendID: String):String{
        val currentId = MyFireBaseAuth.getUserId()
        if(currentId>friendID){
            return friendID+currentId
        }else{
            return currentId+friendID
        }
    }

    private fun setUpActionBar(user: User){
        setSupportActionBar(binding.toolbarChat)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionBar.setTitle("")
        }
        binding.tvToolbarUsername.text = user.userName
        binding.tvToolbarName.text = user.name
        if(user.userImage != ""){
            Picasso.get().load(user.userImage).into(binding.civToolabar)
        }
        binding.toolbarChat.setNavigationOnClickListener {
            onBackPressed()

        }
    }

    private fun getUser(){
        val sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_USER, MODE_PRIVATE)

        val json = sharedPreferences.getString(Constants.SP_USER,null)
        val gson = Gson()

        currentUser = gson.fromJson(json,User::class.java)

    }
    private fun setchatOpen(bool:Boolean){
        val inbox = if(bool){
            Inbox(messageInbox!!,friendUserByInbox!!.id,friendUserByInbox!!.name,
                friendUserByInbox!!.userName,friendUserByInbox!!.userImage,chatOpen = true,time = timeInbox)
        }else{
            Inbox(messageInbox!!,friendUserByInbox!!.id,friendUserByInbox!!.name,
                friendUserByInbox!!.userName,friendUserByInbox!!.userImage,chatOpen = false,time = timeInbox)
        }
        val ref = Firebase.database.reference.child(Constants.CHATS).child(currentUser.id).child(
            friendUserByInbox.id)
        Log.d("chat Ref","$ref")
        ref.setValue(inbox)

    }

    override fun onDestroy() {
        val ref = Firebase.database.reference.child(Constants.MESSAGES).child(getId(friendUserByInbox!!.id))
        val refTwo = Firebase.database.reference.child(Constants.CHATS).child(friendUserByInbox!!.id).child(currentUser.id)
        val refThree =Firebase.database.reference.child(Constants.CHATS).child(currentUser.id).child(friendUserByInbox!!.id)
        setchatOpen(false)
        ref.removeEventListener(listener)
        refTwo.child("typing").removeEventListener(listenerTwo)
        if(messageInbox ==""){
            refThree.removeValue()
        }
        if(messageList.isNotEmpty()){
            markasRead()
        }
        super.onDestroy()
    }

    override fun onPause() {
        setchatOpen(false)
        super.onPause()
    }

    override fun onResume() {
        setchatOpen(true)
        super.onResume()
    }

}