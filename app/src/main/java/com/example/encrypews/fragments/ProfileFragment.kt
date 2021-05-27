package com.example.encrypews.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.encrypews.R
import com.example.encrypews.activities.EditProfileActivity
import com.example.encrypews.activities.SignInActivity
import com.example.encrypews.adapters.ProfilePostFragmentAdapter
import com.example.encrypews.Utils.Constants
import com.example.encrypews.activities.MainActivity
import com.example.encrypews.customdialogs.CustomDialogFragment
import com.example.encrypews.databinding.FragmentProfileBinding
import com.example.encrypews.databinding.ProgressDialogBinding
import com.example.encrypews.firebase.MyFireBaseAuth
import com.example.encrypews.viewmodels.ProfileFragmentViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding :FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var mprogressDialog: CustomDialogFragment?= null

    private lateinit var profilePostFragmentAdapter: ProfilePostFragmentAdapter
    private lateinit var actionBar : ActionBar
    private lateinit var viewModel : ProfileFragmentViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater,container,false)

        val view = binding.root
        setupActionBar()
        viewModel = ViewModelProvider(requireActivity()).get(ProfileFragmentViewModel :: class.java)
        viewModel.addPostsChangeListener()


//        viewModel.viewModelScope.launch (Dispatchers.IO){
//            viewModel.loadUser()
//
//            Log.d("viewModel","${viewModel.user.value}")
//        }
        viewModel._user.value = (activity as MainActivity).user
        viewModel.addUserChangeListener()

//        viewModel.postCount.observe(viewLifecycleOwner, Observer { data ->
//            binding.tvPostsCount.text = viewModel.postCount.value.toString()
//        })

        viewModel.posts.observe(viewLifecycleOwner, Observer { list ->
            binding.tvPostsCount.text = list.size.toString()
        })

        viewModel.followers.observe(viewLifecycleOwner, Observer { list ->
            binding.tvFollowersCount.text = list.size.toString()
        })

        viewModel.following.observe(viewLifecycleOwner, Observer { list ->
            binding.tvFollowingCount.text = list.size.toString()
        })




        viewModel.user.observe(viewLifecycleOwner, Observer { user->
          if(user != null){
              binding.tvUserName.text = user.name
              binding.tvUserBio.text = user.userBio
              actionBar.setTitle(user.userName)
              if(user.userImage != ""){
                  Picasso.get().load(user.userImage).into(binding.ivProfileImagePf)
              }
          }

        })




        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = listOf<Int>(R.drawable.ic_baseline_grid_on_24,R.drawable.reels_vector_asset,
            R.drawable.tag_vector_asset)


        profilePostFragmentAdapter = ProfilePostFragmentAdapter(this,true)
        binding.pager.adapter = profilePostFragmentAdapter
        binding.pager.isSaveEnabled = false


        TabLayoutMediator(binding.tabLayout,binding.pager){tab,position->
//            tab.text = "tab" + "${position+1}"
        }.attach()


        binding.tabLayout.getTabAt(0)!!.setIcon(items[0])
        binding.tabLayout.getTabAt(1)!!.setIcon(items[1])
        binding.tabLayout.getTabAt(2)!!.setIcon(items[2])

        binding.tvEditProfile.setOnClickListener{
            startEditProfileActivity()
        }



    }


    private fun startEditProfileActivity(){
        val user = viewModel.user.value
        Log.d("user passed","${user}")
        val intent = Intent(activity,EditProfileActivity::class.java)
        intent.putExtra(Constants.USEREP,user?.name.toString())
        intent.putExtra(Constants.USERNAMEEP,user?.userName.toString())
        intent.putExtra(Constants.BIOEP,user?.userBio.toString())
        intent.putExtra(Constants.USERIMURL,user?.userImage.toString())
        startActivity(intent)
    }



    private fun setupActionBar(){
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.profileToolbar)
        }
         actionBar = (activity as AppCompatActivity).supportActionBar!!
        actionBar.setTitle("")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_view_profile_items,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logOut ->{
                showcustomDialog("Signing Out..")
                val ref = Firebase.database.reference.child(Constants.USERS)
                ref.child(MyFireBaseAuth.getUserId()).child("deviceToken").setValue("").addOnCompleteListener {task->
                    if(task.isSuccessful){
                        MyFireBaseAuth.auth.signOut()
                        closeCustomDialog()
                        startActivity(Intent(activity,SignInActivity::class.java))
                        (activity as MainActivity).finish()
                        return@addOnCompleteListener
                    }
                    closeCustomDialog()
                    Toast.makeText(context,"Cannot SignOut right now",Toast.LENGTH_SHORT).show()
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }




//    private fun createAlertDialog(text:String){
//        val builder = context?.let { AlertDialog.Builder(it) }
//
//        builder
//    }

    private fun showcustomDialog(text:String){
        mprogressDialog= CustomDialogFragment().newInstance(text)
        val fragmentManager = activity?.supportFragmentManager
        mprogressDialog!!.isCancelable = false
        mprogressDialog!!.show(fragmentManager!!,"fragment_progress_dialog")

    }

    private fun closeCustomDialog(){
        if(mprogressDialog != null){
            mprogressDialog!!.dismiss()
        }
    }


}