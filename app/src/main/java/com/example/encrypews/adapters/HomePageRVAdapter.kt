package com.example.encrypews.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.encrypews.R
import com.example.encrypews.Utils.Constants
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
import kotlinx.coroutines.*

class HomePageRVAdapter(private val context:Context)
    : RecyclerView.Adapter<HomePageRVAdapter.MyViewHolder>() {
    private var ref = Firebase.database.reference
    var list = ArrayList<Post>()
    private var onClickListener : ONCLICK? = null
    private lateinit var binding:ListPostBinding



    private var like:Boolean = false

    inner class MyViewHolder(val binding:ListPostBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = ListPostBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = with(holder) {
//        val post = list[position]
        val post = differ.currentList[position]
        val postid= post.postId
        Log.e("position","$position")

        var user = User()

        val likeRef = ref.child(Constants.LIKES).child(postid)

        val commentRef = ref.child(Constants.COMMENTS).child(postid)

        val saveRef = ref.child(Constants.SAVED_POSTS).child(MyFireBaseAuth.getUserId()).child(postid)

        saveRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value != null){
                    binding.ibSavePostHome.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_saved))
                    binding.ibSavePostHome.tag ="saved"
                }else{
                    binding.ibSavePostHome.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_save_alt_24))
                    binding.ibSavePostHome.tag ="not_saved"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SavePostError",error.message)
            }

        })




        likeRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("snapshot","${snapshot.children.count()}")
                binding.likes.text = context.getString(R.string.count_likes,snapshot.children.count())
                if(snapshot.child(MyFireBaseAuth.getUserId()).exists()){
                    binding.ibLikePostHome.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.heart_like))
                    binding.ibLikePostHome.tag ="liked"
                }else{
                    binding.ibLikePostHome.setImageDrawable(ContextCompat.getDrawable
                        (context,R.drawable.ic_baseline_favorite_border_26))
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
            user  = MyFireBaseDatabase().loadUser(post.publishedBy)
            withContext(Dispatchers.Main){
                binding.tvUsrIdPost.text = user.userName
                binding.captionUsername.text = user.userName
                if(user.userImage != ""){
                    Picasso.get().load(user.userImage).placeholder(R.color.offWhite).into(binding.usrImgPost)
                }else{
                    binding.usrImgPost.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.usr_image_place_holder))
                }
            }
        }
        Picasso.get().load(post.imageUrl).placeholder(R.color.offWhite).into(binding.ivPostImage)
        binding.captionContent.text = post.caption

        if(onClickListener != null){
            binding.captionUsername.setOnClickListener{
                onClickListener!!.onclickPublisher(post,user)
            }
            binding.ibLikePostHome.setOnClickListener{
                if(it.tag == "liked"){
                    onClickListener!!.onclickLike(post,false)
                }else{
                    onClickListener!!.onclickLike(post,true)
                }

            }

            binding.ibSavePostHome.setOnClickListener{
                if(it.tag =="saved"){
                    onClickListener!!.onclickSave(post,false)
                }else{
                    onClickListener!!.onclickSave(post,true)
                }
            }

            binding.ibCommentPostHome.setOnClickListener{
                onClickListener!!.onclickComment(post)
            }
            binding.tvComments.setOnClickListener{
                onClickListener!!.onclickComment(post)
            }
            binding.ibSendPostHome.setOnClickListener{
                onClickListener!!.onclickMessage(post,user)
            }
            binding.usrImgPost.setOnClickListener{
                onClickListener!!.onclickPublisher(post,user)
            }
            binding.tvUsrIdPost.setOnClickListener{
                onClickListener!!.onclickPublisher(post,user)
            }

            binding.morePostOptions.setOnClickListener{

                onClickListener!!.onclickMore(post,user)
            }

            binding.ivPostImage.setOnClickListener{
                if(like){
                    if(binding.ibLikePostHome.tag == "liked"){
                        onClickListener!!.onclickLike(post,false)
                    }else{
                        onClickListener!!.onclickLike(post,true)
                    }
                }else{
                    like = true

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(500)
                        like = false
                    }
                }


            }

        }


    }

    override fun getItemCount(): Int {
//        return list.size
        return differ.currentList.size
    }



    interface ONCLICK{
        fun onclickLike(post:Post,boolean: Boolean)
        fun onclickComment(post:Post)
        fun onclickPublisher(post:Post,user: User)
        fun onclickMessage(post:Post,user:User)
        fun onclickMore(post: Post,user: User)
        fun onclickSave(post:Post,boolean: Boolean)
    }

    fun setOnClickListener(listener:ONCLICK){
        this.onClickListener = listener
    }

    private val differCallback = object : DiffUtil.ItemCallback<Post>(){
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
           return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem== newItem
        }


    }

    val differ = AsyncListDiffer(this,differCallback)



}