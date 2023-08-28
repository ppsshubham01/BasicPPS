package com.example.basic.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.basic.R
import com.example.basic.databinding.CustomdilogeforgetpasswordBinding
import com.example.basic.databinding.FragmentGmailLoginBinding
import com.example.basic.databinding.FragmentManualGmailLogInBinding
import com.google.firebase.auth.FirebaseAuth


class GmailLoginFragment : Fragment() {

    private lateinit var binding: FragmentGmailLoginBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentGmailLoginBinding.inflate(inflater, container, false)

        binding.btnLogIn.setOnClickListener{
           if( validateFields()){
               login()
           }
        }
//ForgetPassWord
        binding.forgetPassword.setOnClickListener {
            val dialogBinding= CustomdilogeforgetpasswordBinding.inflate(inflater)
            val myDialog=Dialog(requireActivity())
            myDialog.setContentView(dialogBinding.root)

            myDialog.setCancelable(true)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialog.show()

            dialogBinding.SendEmail.setOnClickListener {
                if(dialogBinding.etEmail.text.toString().isNotEmpty()){
                    FirebaseAuth.getInstance().sendPasswordResetEmail(dialogBinding.etEmail.text.toString())
                }
                myDialog.dismiss()
            }
        }
        binding.TextSignup.setOnClickListener {
            findNavController().navigate(GmailLoginFragmentDirections.actionGmailLoginFragmentToManualGmailLogInFragment())
        }

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true)
        {
            override fun handleOnBackPressed() =
                findNavController().navigate(GmailLoginFragmentDirections.actionGmailLoginFragmentToManualGmailLogInFragment())
        }
        requireActivity().onBackPressedDispatcher.addCallback (viewLifecycleOwner, callback)



        return binding.root
    }
    // Login function
    private fun login() {
        val email = binding.TextEmail.text.toString()
        val password = binding.TextPassword.text.toString()

        val auth = FirebaseAuth.getInstance()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(GmailLoginFragmentDirections.actionGmailLoginFragmentToMainScreenFragment())
                } else {
                    Toast.makeText(requireContext(), "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    // Validation
    private fun validateFields(): Boolean {
        var isValid = true

        val email = binding.TextEmail.text.toString()
        val password = binding.TextPassword.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Enter valid email", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        if (password.length < 6) {
            Toast.makeText(requireContext(), "Enter valid password", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }
}