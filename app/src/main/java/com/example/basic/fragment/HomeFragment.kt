package com.example.basic.fragment

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.basic.R
import com.example.basic.databinding.FragmentTempBinding
import com.example.basic.utils.GoogleSignIn
import com.example.basic.model.User
import com.example.basic.utils.AppConstant
import com.example.basic.utils.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentTempBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignIn: GoogleSignIn

    private val TAG: String = "thisisthe"
    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var selectedImage: Uri? = null
    private var dialog: ProgressDialog? = null
    private val STORAGE_PERMISSION_CODE = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTempBinding.inflate(inflater, container, false)

        dialog = ProgressDialog(requireContext())
        dialog!!.setMessage("Updating Profile")
        dialog!!.setCancelable(false)
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        googleSignIn = GoogleSignIn()

//        requireActivity().Bar?.hide()
        binding.UserImageView.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openImagePicker()
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            }
        }

        binding.setupProfileBtn.setOnClickListener {
            val name: String = binding.nameBox.text.toString()
            if (name.isEmpty()) {
                binding.nameBox.error = "Please type a name!"
            } else {
                dialog!!.show()
                Log.d(TAG, "Image upload successful:${selectedImage}")

                if (selectedImage != null) {
                    Log.d(TAG, "Image upload successful:")

                    val reference = storage!!.reference.child("Profile").child(auth.uid!!).child(".jpg")
//                    val imageRef = storage!!.reference.child("Profile").child("MizPXgsflQVx4SAdymzSgKd5YdG3"+".jpg")\

                    reference.putFile(selectedImage!!).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                task.result.storage.downloadUrl.addOnCompleteListener { uri ->
                                    if (uri.isSuccessful) {
                                        val imageUrl = uri.result.toString()
                                        val uid = auth.uid
                                        val phone = auth.currentUser!!.phoneNumber
                                        val enteredName: String = binding.nameBox.text.toString()

                                        val user = User(uid, enteredName, phone, imageUrl)

                                        Log.d(TAG, "Image upload successful: ${task.result}")
                                        Log.d(TAG, "Download URL: $imageUrl")

                                        database!!.reference.child("users").child(uid!!).setValue(user)
                                            .addOnCompleteListener { databaseTask ->
                                                if (databaseTask.isSuccessful) {

                                                    findNavController().navigate(HomeFragmentDirections.actionTempFragmentToMainScreenFragment())
                                                    dialog!!.dismiss()

//                                                    PreferenceManager.isOnBoardingCompleted=true
////                                                    setUpProfileFinished()
//                                                     val sharedPref=requireActivity().getSharedPreferences(AppConstant.SharedPrefrence_name, Context.MODE_PRIVATE)
//                                                            val editor=sharedPref.edit()
//                                                            editor.putBoolean(AppConstant.profile_screen_shown,true)
//                                                            editor.apply()
//                                                    Log.d(TAG, "User Profile Setup Done.")
                                                } else {
                                                    Log.e(TAG, "Database storage error: ${databaseTask.exception}")
                                                    //database storage failure
                                                    dialog!!.dismiss()
                                                }
                                            }
                                    } else {
                                        Log.e(TAG, "Download URL retrieval error: ${uri.exception}")
                                        dialog!!.dismiss()
                                    }                                }
                            } else {
                                Log.e(TAG, "Image upload error: ${task.exception}")
                                dialog!!.dismiss()
                            }
                        }
                }
            }
        }
        return binding.root
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker()
            } else {
                // Handle permission denial
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/*"
        startActivityForResult(intent, 45)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 45 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            selectedImage = data.data
            binding.UserImageView.setImageURI(selectedImage)
        }
    }

//    private fun setUpProfileFinished(){
//        val sharedPref=requireActivity().getSharedPreferences(AppConstant.SharedPrefrence_name, Context.MODE_PRIVATE)
//        val editor=sharedPref.edit()
//        editor.putBoolean(AppConstant.onBoarding_Finished,true)
//        editor.apply()
//
//        val user = Firebase.auth.currentUser
//        if (user!=null){
////            don't go back
//        }
//    }

}