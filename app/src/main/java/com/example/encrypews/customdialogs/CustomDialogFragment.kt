package com.example.encrypews.customdialogs

import android.app.Dialog
import android.os.Binder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.encrypews.databinding.ProgressDialogBinding

class CustomDialogFragment():DialogFragment() {
    private lateinit var binding:ProgressDialogBinding
    fun newInstance(text:String):CustomDialogFragment{
        val frag = CustomDialogFragment()
        val bundle = Bundle()
        bundle.putString("text",text)
        frag.arguments = bundle
        return frag
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProgressDialogBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = arguments?.getString("text")
        binding.tvProgressText.text = text
    }
}