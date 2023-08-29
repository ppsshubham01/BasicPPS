package com.example.basic.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.basic.R
import com.example.basic.adapter.UserAdapter
import com.example.basic.databinding.FragmentUsersALLBinding
import com.example.basic.model.ConversationModel
import com.example.basic.model.Message
import com.example.basic.model.User
import com.example.basic.utils.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UsersALL : Fragment(), UserAdapter.OnItemClickListener {


    private val TAG: String = this.javaClass.simpleName
    private lateinit var currentId: String
    private lateinit var binding: FragmentUsersALLBinding
    private var auth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var databaseusers: MutableList<User> = mutableListOf()
    private var userAdapter: UserAdapter? = null
    private var dialog: ProgressDialog? = null
    private var user: User? = null
    private lateinit var googleSignIn: GoogleSignIn

    private lateinit var viewModel: UsersALLViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersALLBinding.inflate(inflater, container, false)
        currentId = FirebaseAuth.getInstance().uid.toString()

        googleSignIn = GoogleSignIn()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
//        databaseusers = ArrayList<User>()

        userAdapter = UserAdapter(
            requireActivity(),
            databaseusers,
            object : UserAdapter.OnItemClickListener {
                override fun onItemClick(user: User) {
                    val bundle = Bundle()
                    bundle.putString("name", user.name)
                    bundle.putString("image", user.profileImage)
                    bundle.putString("uid", user.uid)
//                    bundle.putSerializable("user",user)
                    findNavController().navigate(
                        R.id.action_usersALL_to_chatLayoutFragment,
                        bundle
                    )
                }
            })
        val layoutManager = LinearLayoutManager(requireContext())
        binding.VerticalRV.layoutManager = layoutManager
        binding.VerticalRV.adapter = userAdapter


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_usersALL_to_mainScreenFragment)
        }

//getusers data/Information Firebase

        database!!.reference.child("users")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {

                    user = snapshot.getValue(User::class.java)
                    Log.d("getUservalue", "${user}")

                    if (snapshot.exists()) {
                        databaseusers.clear()

// and add users that are not the current user
                        for (snapshot1 in snapshot.children) {
                            val user: User? = snapshot1.getValue(User::class.java)

//                             databaseusers!!.add(user)
                            user?.let { if (!user.uid.equals(FirebaseAuth.getInstance().uid)) databaseusers.add(it)}
                        }
// Sort user data by name
                        databaseusers!!.sortBy { it.name }

 // Notify the adapter that the data set has changed
                        userAdapter!!.notifyDataSetChanged()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UsersALLViewModel::class.java)
        // TODO: Use the ViewModel
    }



    override fun onItemClick(user: User) {
    //Change fragment here

    auth?.currentUser?.uid?.let {
        database!!.reference.child(it).child("conversations")
//                .orderByChild("conr")
            .orderByChild("").equ
            .startAt(user.uid)
            .endAt(user.uid)
            .addValueEventListener(object : ValueEventListener {
                //get datafrom firebase to display in chat
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {

                    Log.d(TAG, "conversation onDataChange: $snapshot")
//                    snapshot.children.forEach {
//                        val message: Message = it.getValue(Message::class.java)!!
//                        message.messageId = it.key
//                        messages.add(message)
//                    }

//                        val bundle = Bundle()
//
//                        bundle.putSerializable("user", user)
//                        bundle.putSerializable("conversation", null)
//                        bundle.putSerializable("conversation", conversationModel)
//                        val navController=findNavController()
//                        navController.navigate(R.id.action_usersALL_to_chatLayoutFragment, bundle)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}



}

























































//        // Setting up the RecyclerView adapter and populating user data
//        database!!.reference.child("users").addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    databaseusers!!.clear()
//                    for (snapshot1 in snapshot.children) {
//                        val user: User? = snapshot1.getValue(User::class.java)
//
//                        if (!user!!.uid.equals(FirebaseAuth.getInstance().uid)) databaseusers!!.add(user)
//                    }
//                    userAdapter!!.notifyDataSetChanged()
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {}
//        })