package com.example.encrypews.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.encrypews.R
import com.example.encrypews.Utils.Constants
import com.example.encrypews.activities.ChatActivity
import com.example.encrypews.activities.CommentActivity
import com.example.encrypews.activities.InboxActivity
import com.example.encrypews.activities.MainActivity
import com.example.encrypews.adapters.HomePageRVAdapter
import com.example.encrypews.customdialogs.CustomBottomSheetDialogFragment
import com.example.encrypews.customdialogs.CustomDialogFragment
import com.example.encrypews.databinding.FragmentHomeBinding
import com.example.encrypews.models.Post
import com.example.encrypews.models.User
import com.example.encrypews.viewmodels.HomeFragmentViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment(R.layout.fragment_home) {

     private var _binding : FragmentHomeBinding? = null
     private val binding  get()= _binding!!
    private var mprogressDialog:CustomBottomSheetDialogFragment? = null

    private lateinit var viewModel : HomeFragmentViewModel
    private lateinit var adapter : HomePageRVAdapter

    private lateinit var ownUser:User

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        val view = binding.root
        setupActionBar()
        viewModel = ViewModelProvider(requireActivity()).get(HomeFragmentViewModel::class.java)
        adapter = HomePageRVAdapter(requireActivity())
        binding.rvHome.layoutManager = LinearLayoutManager(activity)
        binding.rvHome.hasFixedSize()
        binding.rvHome.setItemViewCacheSize(20)
        binding.rvHome.isDrawingCacheEnabled = true
        binding.rvHome.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

        getOwnUser()

        adapter.setOnClickListener(object : HomePageRVAdapter.ONCLICK{
            override fun onclickLike(post: Post,boolean: Boolean) {
               if(boolean){
                   Log.d("likeclicked","liking post")
                   viewModel.likePost(post.postId)
               }else{
                   Log.d("likeclicked","Unliking post")
                   viewModel.unlikePost(post.postId)
               }
            }

            override fun onclickComment(post: Post) {
                loadCommentActivity(post)

            }



            override fun onclickPublisher(post: Post,user: User) {//for now opens the chat activity
                makeToast("Publisher Clicked")
            }

            override fun onclickMessage(post: Post,user:User) {
                gotoActivityChat(user)
            }

            override fun onclickMore(post: Post, user: User) {
                val userString = Gson().toJson(user)
                val postString = Gson().toJson(post)
                showcustomDialog(userString,postString)
            }

        })
        binding.rvHome.adapter = adapter
        binding.srvHome.setOnRefreshListener {
            getPostsUser()
        }

       getPostsUser()

        viewModel.posts.observe(viewLifecycleOwner, Observer { list->
            adapter.list = list as ArrayList<Post>
            adapter.notifyDataSetChanged()
            if(binding.srvHome.isRefreshing){
                binding.srvHome.isRefreshing = false
            }

        })

        return view

    }




    private fun setupActionBar(){
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.mainToolbar)
        }

       val actionbar = (activity  as AppCompatActivity).supportActionBar
        actionbar!!.setTitle(R.string.app_name)
//        binding.mainToolbar.inflateMenu(R.menu.message_home_fragment)
//        binding.mainToolbar.setTitle(R.string.app_name_secondary)
        val toolbar : androidx.appcompat.widget.Toolbar = binding.mainToolbar
        toolbar.changeTitleFont()

//        binding.mainToolbar.setOnMenuItemClickListener{
//            if(it.itemId == R.id.messaging_chat){
//                Toast.makeText(activity?.applicationContext,"msgs",Toast.LENGTH_SHORT).show()
//            }
//            true
//        }

    }

    private fun androidx.appcompat.widget.Toolbar.changeTitleFont(){
        for(i in 0 until childCount){
            val view = getChildAt(i)
            if(view is TextView && view.text == title){
                val typeface: Typeface = Typeface.createFromAsset(view.context.assets,"billabong.ttf")
                view.typeface = typeface
                break
            }
        }
    }





// this method is used to setup menu in toolbar in fragments , which require using toolbar as an action bar
    // other method is used in the main code in onCreateView

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.message_home_fragment,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.messaging_chat -> {

//                Toast.makeText(activity?.applicationContext,"Messaging Coming Soon",Toast.LENGTH_SHORT).show()
                gotoActivityInbox(ownUser)
                return true
            }

        }
//        return super.onOptionsItemSelected(item)
        return false
    }



    private fun makeToast(string : String){
        Toast.makeText(activity,string,Toast.LENGTH_SHORT).show()
    }

    private fun loadCommentActivity(post: Post){
        val intent = Intent(activity,CommentActivity::class.java)
        intent.putExtra(Constants.POSTEXTRA,post)
        startActivity(intent)

    }

    private fun gotoActivityInbox(user: User){
        val intent = Intent(activity,InboxActivity::class.java)
        intent.putExtra(Constants.OWN_USER,user)
        startActivity(intent)
    }
    private fun gotoActivityChat(user: User){
        val intent = Intent(activity,ChatActivity::class.java)
        intent.putExtra(Constants.FRIEND_USER,user)
        startActivity(intent)
    }

    private fun getPostsUser(){
        viewModel.viewModelScope.launch (Dispatchers.IO){
            viewModel.getPostsForUser()

        }
    }

    private fun getOwnUser(){
        val user:User = (activity as MainActivity).user
        ownUser = user
    }

    private fun showcustomDialog(userString:String,postString:String){
        mprogressDialog= CustomBottomSheetDialogFragment().newInstance(userString,postString)
        val fragmentManager = activity?.supportFragmentManager
        mprogressDialog!!.isCancelable = true
        mprogressDialog!!.show(fragmentManager!!,"fragment_progress_dialog")

    }

    private fun closeCustomDialog(){
        if(mprogressDialog != null){
            mprogressDialog!!.dismiss()
        }
    }

}