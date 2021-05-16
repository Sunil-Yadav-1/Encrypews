package com.example.encrypews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.encrypews.R
import com.example.encrypews.adapters.ProfilePostFragmentAdapter
import com.example.encrypews.databinding.FragmentOtherUserBinding
import com.example.encrypews.viewmodels.OtherProfileFragmentViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class OtherUserFragment : Fragment() {
    private var _binding:FragmentOtherUserBinding? = null
    private val binding get() = _binding!!
    private  var userName:String =""
    private lateinit var userId:String
    private  var actionBar: ActionBar? = null

    private lateinit var profilePostFragmentAdapter: ProfilePostFragmentAdapter
    private lateinit var viewModel : OtherProfileFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments

        if(bundle != null){
            val list = bundle.getStringArrayList("Bndl")
            userId = list!!.get(0)
            userName = list!!.get(1)
        }

    }

//    override fun onDestroyView() {
//        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.flmain,SearchFragment())?.commit()
//        super.onDestroyView()
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOtherUserBinding.inflate(inflater,container,false)
        val view = binding.root
        setUpActionBar()
        viewModel = ViewModelProvider(requireActivity()).get(OtherProfileFragmentViewModel::class.java)

        viewModel.addValueEventListener(userId)

        viewModel.viewModelScope.launch(Dispatchers.IO){
            viewModel.loadUser(userId)
//            viewModel.getPosts(userId)
            viewModel.isFollowed(userId)
            withContext(Dispatchers.Main){
                val user = viewModel.user.value
                if(user!= null){
                    binding.tvUserName.text = user.name
                    binding.tvUserBio.text = user.userBio
                    if(user.userImage != ""){
                        Picasso.get().load(user.userImage).into(binding.ivProfileImagePf)
                    }
                }
            }
        }


//        viewModel.postCount.observe(viewLifecycleOwner, Observer { value->
//            binding.tvPostsCount.text = value.toString()
//        })
        viewModel.posts.observe(viewLifecycleOwner, Observer { list->
            binding.tvPostsCount.text = list.size.toString()
        })

        viewModel.isfollowed.observe(viewLifecycleOwner, Observer { bool->
            if(bool){
                binding.btnFollow.background = ResourcesCompat
                    .getDrawable(resources,R.drawable.shape_button_drawable,resources.newTheme())
                binding.btnFollow.setTextColor(ResourcesCompat.getColor(resources,R.color.black,resources.newTheme()))
                binding.btnFollow.setText(R.string.following)
            }else{
                binding.btnFollow.background = ResourcesCompat
                    .getDrawable(resources,R.drawable.shape_button_rounded_2,resources.newTheme())
                binding.btnFollow.setTextColor(ResourcesCompat.getColor(resources,R.color.white,resources.newTheme()))
                binding.btnFollow.text = getString(R.string.follow)
            }

        })

        viewModel.following.observe(viewLifecycleOwner, Observer { list ->
            binding.tvFollowingCount.text = list.size.toString()
        })
        viewModel.followers.observe(viewLifecycleOwner, Observer { list->
            binding.tvFollowersCount.text = list.size.toString()
        })

        if(actionBar != null){
            actionBar!!.setTitle(userName)
        }

        binding.btnFollow.setOnClickListener{
            if(viewModel.isfollowed.value == null || viewModel.isfollowed.value == false){
                binding.btnFollow.startAnimation()
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    viewModel.followUser(userId)
                    withContext(Dispatchers.Main){

                        binding.btnFollow.revertAnimation{
                            binding.btnFollow.background = ResourcesCompat
                                .getDrawable(resources,R.drawable.shape_button_drawable,resources.newTheme())
                            binding.btnFollow.setTextColor(ResourcesCompat.getColor(resources,R.color.black,resources.newTheme()))
                            binding.btnFollow.setText(R.string.following)
                        }

                    }
                }
            }else if(viewModel.isfollowed.value == true){
                binding.btnFollow.startAnimation()
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    viewModel.unfollowUser(userId)
                    withContext(Dispatchers.Main){

                        binding.btnFollow.revertAnimation{
                            binding.btnFollow.background = ResourcesCompat
                                .getDrawable(resources,R.drawable.shape_button_rounded_2,resources.newTheme())
                            binding.btnFollow.setTextColor(ResourcesCompat.getColor(resources,R.color.white,resources.newTheme()))
                            binding.btnFollow.text = getString(R.string.follow)
                        }

                    }
                }
            }



        }


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = listOf<Int>(R.drawable.ic_baseline_grid_on_24,R.drawable.reels_vector_asset,
            R.drawable.tag_vector_asset)


        profilePostFragmentAdapter = ProfilePostFragmentAdapter(this,false)
        binding.pager.adapter = profilePostFragmentAdapter
        binding.pager.isSaveEnabled = false


        TabLayoutMediator(binding.tabLayout,binding.pager){tab,position->
//            tab.text = "tab" + "${position+1}"
        }.attach()


        binding.tabLayout.getTabAt(0)!!.setIcon(items[0])




    }

    private fun setUpActionBar(){
        (activity as AppCompatActivity).setSupportActionBar(binding.profileToolbar)
        actionBar = (activity as AppCompatActivity).supportActionBar
        if(actionBar != null){
            actionBar!!.setTitle("")
        }
    }


}