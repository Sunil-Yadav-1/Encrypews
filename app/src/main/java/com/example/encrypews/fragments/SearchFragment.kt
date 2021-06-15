package com.example.encrypews.fragments


import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.encrypews.R
import com.example.encrypews.adapters.UserSearchRvAdapter
import com.example.encrypews.databinding.FragmentSearchBinding
import com.example.encrypews.models.User
import com.example.encrypews.viewmodels.SearchFragmentViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchFragment : Fragment(R.layout.fragment_search) {
    private  var _binding:FragmentSearchBinding? = null
    private val binding  get() = _binding!!
    private lateinit var viewModel : SearchFragmentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        setupActionBar()
        viewModel = ViewModelProvider(this).get(SearchFragmentViewModel::class.java)

        val adapter = UserSearchRvAdapter(requireContext())
        binding.rvSearchFragment.layoutManager = LinearLayoutManager(activity)


        adapter.setOnClickListener(object : UserSearchRvAdapter.onClicklistener{

            override fun onClickRv(userClicked: User) {
                val user = userClicked
                val bundle =Bundle()
                val list = ArrayList<String>()
                list.add(user.id)
                list.add(user.userName)
                bundle.putStringArrayList("Bndl",list)
//                otherUserFragment.arguments = bundle
//                KeyBoardVisibilityUtil(binding.root,onKeyBoardShown).visibilityListener
                hideKeyBoard()
                findNavController().navigate(R.id.action_searchFragment_to_otherUserFragment,bundle)
            }


        })
        binding.rvSearchFragment.adapter = adapter

        viewModel.listUsers.observe(viewLifecycleOwner, Observer{ list ->
//            adapter.listUsers = list as ArrayList<User>
//            adapter.notifyDataSetChanged()
            adapter.differ.submitList(list)

        })

        return binding.root

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)

        val searchItem = menu.findItem(R.id.search_bar_menu)
        val searchView = searchItem.actionView  as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            var job:Job? = null
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    viewModel.searchUser(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    job?.cancel()
                    job = MainScope().launch {
                        delay(500L)
                        viewModel.searchUser(newText)
                    }

                }
                return true
            }

        })

    }

    private fun setupActionBar(){
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.searchToolbar)
        }
        (activity as AppCompatActivity).supportActionBar!!.title = ""





    }

//    val onKeyBoardShown: (Boolean)->Unit = { bool:Boolean ->
//        if(bool){
//            val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
//        }
//    }

    private fun hideKeyBoard(){
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // Check if no view has focus
        val currentFocusedView = activity?.currentFocus
        currentFocusedView?.let {
            inputMethodManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }



}