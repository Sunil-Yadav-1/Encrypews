package com.example.encrypews.fragments


import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.encrypews.R
import com.example.encrypews.adapters.UserSearchRvAdapter
import com.example.encrypews.databinding.FragmentSearchBinding
import com.example.encrypews.models.User
import com.example.encrypews.viewmodels.SearchFragmentViewModel


class SearchFragment : Fragment() {
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
        val otherUserFragment = OtherUserFragment()
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        setupActionBar()
        viewModel = ViewModelProvider(this).get(SearchFragmentViewModel::class.java)

        val adapter = UserSearchRvAdapter()
        binding.rvSearchFragment.layoutManager = LinearLayoutManager(activity)


        adapter.setOnClickListener(object : UserSearchRvAdapter.onClicklistener{

            override fun onClickRv(position: Int) {
                val user = viewModel.listUsers.value?.get(position)
                var bundle =Bundle()
                val list = ArrayList<String>()
                list.add(user!!.id)
                list.add(user!!.userName)
                bundle.putStringArrayList("Bndl",list)
                otherUserFragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.flmain,otherUserFragment)?.addToBackStack("tag")?.commit()
            }


        })
        binding.rvSearchFragment.adapter = adapter

        viewModel.listUsers.observe(viewLifecycleOwner, Observer{ list ->
            adapter.listUsers = list as ArrayList<User>
            adapter.notifyDataSetChanged()

        })

        return binding.root

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)

        val searchItem = menu.findItem(R.id.search_bar_menu)
        val searchView = searchItem.actionView  as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    viewModel.searchUser(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.searchUser(newText)
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


}