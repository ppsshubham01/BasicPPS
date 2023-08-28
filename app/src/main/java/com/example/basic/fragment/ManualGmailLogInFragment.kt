package com.example.basic.fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.basic.databinding.FragmentManualGmailLogInBinding
import com.google.firebase.auth.FirebaseAuth


class ManualGmailLogInFragment : Fragment() {

    private lateinit var binding: FragmentManualGmailLogInBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentManualGmailLogInBinding.inflate(inflater, container, false)

        binding.btnSignUp.setOnClickListener {
            if (validateFields()) {
                signUpUser()
            }}
        binding.TextLogin.setOnClickListener {
            findNavController().navigate(ManualGmailLogInFragmentDirections.actionManualGmailLogInFragmentToGmailLoginFragment())
        }
        val callback: OnBackPressedCallback = object :OnBackPressedCallback(true)
        {
            override fun handleOnBackPressed() =
                findNavController().navigate(ManualGmailLogInFragmentDirections.actionManualGmailLogInFragmentToLoggedin())
        }
        requireActivity().onBackPressedDispatcher.addCallback (viewLifecycleOwner, callback)

            return binding.root
    }

    private fun signUpUser() {
        val email = binding.TextEmail.text.toString()
        val password = binding.TextPassword.text.toString()
        val cpassword = binding.ConfomPassword.text.toString()

        val auth = FirebaseAuth.getInstance()

        if (email.isNotEmpty() && password.isNotEmpty() && cpassword.isNotEmpty()) {
            if (password == cpassword) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        findNavController().navigate(ManualGmailLogInFragmentDirections.actionManualGmailLogInFragmentToTempFragment())
                    } else {
                        Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Password is not matching", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
        }
    }
        fun validateFields(): Boolean {
            var isValid = true

            val email = binding.TextEmail.text.toString()
            val password = binding.TextPassword.text.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Invalid email address", Toast.LENGTH_SHORT).show()
                isValid = false
            }
            if (password.length < 6) {
                Toast.makeText(requireContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            return isValid
        }


}