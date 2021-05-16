package com.example.encrypews.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.encrypews.R
import com.example.encrypews.constants.Constants
import com.example.encrypews.databinding.ListPostBinding
import com.example.encrypews.firebase.MyFireBaseAuth
import com.example.encrypews.firebase.MyFireBaseDatabase
import com.example.encrypews.models.Post
import com.example.encrypews.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomePageRVAdapter(private val context:Context)
    : RecyclerView.Adapter<HomePageRVAdapter.MyViewHolder>() {
    private var ref = Firebase.database.reference
    var list = ArrayList<Post>()
    private var onClickListener : ONCLICK? = null
    private lateinit var binding:ListPostBinding

    inner class MyViewHolder(val binding:ListPostBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = ListPostBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = with(holder) {
        val post = list[position]
        val postid= post.postId
        Log.e("position","$position")


        val likeRef = ref.child(Constants.LIKES).child(postid)

        val commentRef = ref.child(Constants.COMMENTS).child(postid)

        likeRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("snapshot","${snapshot.children.count()}")
                binding.likes.text = context.getString(R.string.count_likes,snapshot.children.count())
                if(snapshot.child(MyFireBaseAuth.getUserId()).exists()){
                    binding.ibLikePostHome.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.heart_like))
                    binding.ibLikePostHome.tag ="liked"
                }else{
                    binding.ibLikePostHome.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_favorite_border_24))
                    binding.ibLikePostHome.tag ="like"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("likeRef", error.message)
            }

        })

        commentRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.tvComments.text = context.getString(R.string.view_comments,snapshot.children.count())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("likeRef", error.message)
            }

        })




        CoroutineScope(Dispatchers.IO).launch {
           val user : User = MyFireBaseDatabase().loadUser(post.publishedBy)
            withContext(Dispatchers.Main){
                binding.tvUsrIdPost.text = user.userName
                binding.captionUsername.text = user.userName
                Picasso.get().load(user.userImage).into(binding.usrImgPost)
            }
        }
        Picasso.get().load(post.imageUrl).placeholder(R.color.grey).into(binding.ivPostImage)
        binding.captionContent.text = post.caption

        if(onClickListener != null){
            binding.captionUsername.setOnClickListener{
                onClickListener!!.onclickPublisher(post)
            }
            binding.ibLikePostHome.setOnClickListener{
                if(it.tag == "liked"){
                    onClickListener!!.onclickLike(post,false)
                }else{
                    onClickListener!!.onclickLike(post,true)
                }

            }
            binding.ibCommentPostHome.setOnClickListener{
                onClickListener!!.onclickComment(post)
            }
            binding.tvComments.setOnClickListener{
                onClickListener!!.onclickComment(post)
            }
            binding.ibSendPostHome.setOnClickListener{
                onClickListener!!.onclickMessage(post)
            }
            binding.usrImgPost.setOnClickListener{
                onClickListener!!.onclickPublisher(post)
            }

        }


    }

    override fun getItemCount(): Int {
        return list.size
    }



    interface ONCLICK{
        fun onclickLike(post:Post,boolean: Boolean)
        fun onclickComment(post:Post)
        fun onclickPublisher(post:Post)
        fun onclickMessage(post:Post)

    }

    fun setOnClickListener(listener:ONCLICK){
        this.onClickListener = listener
    }



}