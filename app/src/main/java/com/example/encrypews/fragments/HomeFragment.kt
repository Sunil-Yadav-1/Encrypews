package com.example.encrypews.fragments

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.encrypews.R
import com.example.encrypews.activities.CommentActivity
import com.example.encrypews.adapters.HomePageRVAdapter
import com.example.encrypews.constants.Constants
import com.example.encrypews.databinding.FragmentHomeBinding
import com.example.encrypews.models.Post
import com.example.encrypews.viewmodels.HomeFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

     private var _binding : FragmentHomeBinding? = null
     private val binding  get()= _binding!!

    private lateinit var viewModel : HomeFragmentViewModel
    private lateinit var adapter : HomePageRVAdapter

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



            override fun onclickPublisher(post: Post) {
                makeToast("useridother")
            }

            override fun onclickMessage(post: Post) {
                makeToast("Coming Soon")
            }

        })
        binding.rvHome.adapter = adapter


        viewModel.viewModelScope.launch (Dispatchers.IO){
            viewModel.getPostsForUser()
        }

        viewModel.posts.observe(viewLifecycleOwner, Observer { list->
            adapter.list = list as ArrayList<Post>
            adapter.notifyDataSetChanged()

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

                Toast.makeText(activity?.applicationContext,"Messaging Coming Soon",Toast.LENGTH_SHORT).show()
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

}