package com.example.basic.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.basic.databinding.FragmentLoggedinBinding
import com.example.basic.utils.AppConstant
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLoggedinBinding
    lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var auth: FirebaseAuth
    private lateinit var numberEditText: EditText

    private lateinit var googleSignIn: com.example.basic.utils.GoogleSignIn

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoggedinBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        numberEditText = binding.userPhoneNumber
        binding.phoneProgressBar.visibility = View.INVISIBLE
        binding.CCP.registerCarrierNumberEditText(binding.userPhoneNumber)

        binding.otpBtn.setOnClickListener {
            val editPhoneNumber = numberEditText.text?.trim().toString()

            if (editPhoneNumber.isNotEmpty()) {
                if (editPhoneNumber.length == 10) {
                    val phoneNumber = "+91$editPhoneNumber"
                    binding.phoneProgressBar.visibility = View.VISIBLE

                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber) // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(requireActivity()) // Activity (for callback binding) here we use this=requiredActivity??!
                        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                        .build()

                    PhoneAuthProvider.verifyPhoneNumber(options)

                } else {
                    Toast.makeText(requireContext(), "Please enter a correct number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter a number", Toast.LENGTH_SHORT).show()
            }
        }

        googleSignIn = com.example.basic.utils.GoogleSignIn()
        mGoogleSignInClient= googleSignIn.googleSignInClient(requireContext())
        FirebaseAuth.getInstance()

        binding.btnGoogleLogin.setOnClickListener {
            signInGoogle()
            onBoardingFinished()

        }
        binding.btnEmailLogin.setOnClickListener {
            findNavController().navigate(LogInFragmentDirections.actionLoggedinToManualGmailLogInFragment())  }

        return binding.root
    }

    private fun onBoardingFinished(){
        val sharedPref=requireActivity().getSharedPreferences(AppConstant.SharedPrefrence_name, Context.MODE_PRIVATE)
        val editor=sharedPref.edit()
        editor.putBoolean(AppConstant.onBoarding_Finished,true)
        editor.apply()
    }
//GoogleSignIn Start
    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            findNavController().navigate(LogInFragmentDirections.actionLoggedinToTempFragment())
        }else{
            findNavController().navigate(LogInFragmentDirections.actionLoggedinToMainScreenFragment())

        }
    }

    companion object{
        const val RC_SIGN_IN = 1001
        const val EXTRA_NAME = "EXTRA NAME"
    }
//GOOGLESIGNIN END
//OTP START
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        requireContext(),
                        "Authenticate Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid

                    }
                    // Update UI
                }
            }
    }
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }
        override fun onVerificationFailed(e: FirebaseException) {
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")

            }
        }
        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            findNavController().navigate(
                LogInFragmentDirections.actionLoggedinToOTPActivity(
                    phoneNumber = binding.userPhoneNumber.text.toString(),
                    token = token.toString(),
                    verificationId = verificationId
                )
            )
            binding.phoneProgressBar.visibility = View.INVISIBLE
        }
    }
//OTP END
}
