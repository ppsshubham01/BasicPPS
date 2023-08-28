package com.example.basic.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.basic.R
import com.example.basic.databinding.FragmentOtpfragmentBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit


class OTPFragment : Fragment() {

    private lateinit var binding: FragmentOtpfragmentBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var otp: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String
    private lateinit var verificationId: String

    private val args: OTPFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOtpfragmentBinding.inflate(inflater, container, false)
        auth = Firebase.auth

        binding.OTPProgressBar.visibility = View.INVISIBLE
//        addTextChangeListener()
        resendOTPTvVisibility()

// Pass data between Fragments: Pass Data to Destination using Safe Args in Android
        binding.usernumber.text = args.phoneNumber
        this@OTPFragment.verificationId = args.verificationId

        binding.textResendOTP.setOnClickListener {
            resendVerificationCode()
            resendOTPTvVisibility()
        }
        binding.otpVerifyBtn.setOnClickListener {
            //collect otp from all the edit texts
            val typedOTP = binding.otpView.text.toString()

            if (typedOTP.isNotEmpty()) {
                if (typedOTP.length == 6) {

//                    if (isAutofillEnabled()) {
////                        val credential =PhoneAuthProvider.getCredentialFromAutofill(requireContext(), typedOTP)
////                        signInWithPhoneAuthCredential(credential)
//                    } else {
                        val credential: PhoneAuthCredential =
                            PhoneAuthProvider.getCredential(verificationId, typedOTP)
                        binding.OTPProgressBar.visibility = View.VISIBLE
                        signInWithPhoneAuthCredential(credential)
//                    }
                } else {
                    Toast.makeText(requireContext(), "Please Enter Correct OTP", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(), "Please Enter OTP", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return binding.root
    }

//    private fun isAutofillEnabled(): Boolean {
//        val context = requireContext()
////        return autofillUtils.isAutofillEnabled(context)
//    }
    private fun resendOTPTvVisibility() {

        binding.textResendOTP.visibility = View.VISIBLE
        binding.textResendOTP.isEnabled = false

        Handler(Looper.myLooper()!!).postDelayed({
            binding.textResendOTP.visibility = View.INVISIBLE
            binding.textResendOTP.isEnabled = true

        }, 60000)
    }

    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)// OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }



    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            credential.smsCode?.let {code->

            }
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

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            this@OTPFragment.verificationId = verificationId

            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            findNavController().navigate(
                LogInFragmentDirections.actionLoggedinToOTPActivity(
                    phoneNumber = binding.usernumber.text.toString(),
                    token = token.toString(),
                    verificationId = verificationId
                )
            )
            binding.OTPProgressBar.visibility = View.INVISIBLE
        }
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    binding.OTPProgressBar.visibility = View.VISIBLE
                    Toast.makeText(
                        requireContext(),
                        "Authenticate Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    sendToMain()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
                binding.OTPProgressBar.visibility = View.VISIBLE
            }
    }
    private fun sendToMain() {
        findNavController().navigate(R.id.action_OTP_Activity_to_mainScreenFragment)
    }
    }

