package com.example.basic.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.basic.R
import com.example.basic.databinding.FragmentScreen2Binding
import com.example.basic.utils.PreferenceManager

class Walkthrough2 : Fragment() {

    private lateinit var binding: FragmentScreen2Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentScreen2Binding.inflate(inflater,container,false)

        binding.finishonBording.setOnClickListener{
            onBoardingFinished()
            findNavController().navigate(R.id.action_viewpager_to_loggedin)
        }
        return binding.root
    }
    private fun onBoardingFinished(){
        PreferenceManager.isOnBoardingCompleted = true

    }
}