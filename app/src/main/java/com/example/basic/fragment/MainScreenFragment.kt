package com.example.basic.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.basic.R
import com.example.basic.adapter.ConversationAdapter
import com.example.basic.adapter.UserAdapter
import com.example.basic.databinding.FragmentMainScreenBinding
import com.example.basic.model.ConversationModel
import com.example.basic.model.User
import com.example.basic.utils.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainScreenFragment : Fragment() , ConversationAdapter.OnItemClickListener {


    private val TAG: String = this.javaClass.simpleName
    private lateinit var currentId: String
    private lateinit var binding: FragmentMainScreenBinding // Replace with your actual binding class
    private var auth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var users: MutableList<ConversationModel> = mutableListOf<ConversationModel>()
    private var ConversationuserAdapter: ConversationAdapter? = null
    private var dialog: ProgressDialog? = null
    private var user: User? = null
    private lateinit var googleSignIn: GoogleSignIn


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentId = FirebaseAuth.getInstance().uid.toString()

        binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        googleSignIn = GoogleSignIn()

        binding.fabUsers.setOnClickListener {
            findNavController().navigate(MainScreenFragmentDirections.actionMainScreenFragmentToUsersALL())
        }

//        ....................Signout Btn.........................
        binding.signOutBtn.setOnClickListener {
            googleSignIn.googleSignInClient(requireContext()).signOut()
            auth!!.signOut()

            findNavController().navigate(R.id.action_mainScreenFragment_to_loggedin)

        }
//        object : UserAdapter.OnItemClickListener {
//            override fun onItemClick(user: User) {
//                val bundle = Bundle()
//                bundle.putString("name", user.name)
//                bundle.putString("image", user.profileImage)
//                bundle.putString("uid", user.uid)
//                findNavController().navigate(
//                    R.id.action_mainScreenFragment_to_chatLayoutFragment,
//                    bundle
//                )
//            }
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        ConversationuserAdapter = ConversationAdapter(
            requireActivity(),
            users!!,
            object : ConversationAdapter.OnItemClickListener {

                override fun onItemClick(conversationModel: ConversationModel) {
                    val bundle = Bundle()

//                    val bundle =bundleOf(
//                        "conversation" to conversationModel,
//                        "name" to  conversationModel.user?.name,
//                        "image" to  conversationModel.user?.profileImage,
//                        "uid" to  conversationModel.user?.uid,
//                    )
                    bundle.putSerializable("conversation",conversationModel)
                    bundle.putString("name", conversationModel.user?.name)
                    bundle.putString("image", conversationModel.user?.profileImage)
                    bundle.putString("uid", conversationModel.user?.uid)
                    Log.e("signout", "${user}")

//                    findNavController().navigate(MainScreenFragmentDirections.actionMainScreenFragmentToChatLayoutFragment(conversationModel))
                    findNavController().navigate(
                        R.id.action_mainScreenFragment_to_chatLayoutFragment, bundle
                    )
                }
            })


        val layoutManager = LinearLayoutManager(requireContext())
        binding.VerticalRV.layoutManager = layoutManager
        binding.VerticalRV.adapter = ConversationuserAdapter

        dialog = ProgressDialog(requireContext())
        dialog!!.setMessage("Updating Images...")
        dialog!!.setCancelable(false)

        // Getting the current user's info from Firebase
        database!!.reference.child("users")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) {}
            })


        // Setting up the RecyclerView adapter and populating user conversation data
        database!!.reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("conversations").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    users.clear()
                    for (snapshot1 in snapshot.children) {
                        Log.d(TAG, "onDataChange: $snapshot1")
                        val conversationModel: ConversationModel =
                            snapshot1.getValue(ConversationModel::class.java)!!
                        conversationModel.conversationUid = snapshot1.key!!
                        val otherUid =
                            conversationModel.members?.first() { it != FirebaseAuth.getInstance().uid }

                        database!!.reference.child("users").child(otherUid!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        val user: User = snapshot.getValue(User::class.java)!!
                                        conversationModel.user = user
                                        users.add(conversationModel)
                                        ConversationuserAdapter!!.notifyItemInserted(users.size - 1)

                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })
//                        conversationModel.conuser= snapshot1
//                         users!!.add(user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: currentId: $currentId")
        database!!.reference.child("presence")
            .child(currentId).setValue("Offline")
    }

    /**
     * @see UserAdapter
     */

    override fun onItemClick(userconversation: ConversationModel) {
        val bundle = Bundle()
        bundle.getSerializable("user")
        Log.e("signout11", "${bundle}")

        findNavController().navigate(R.id.action_mainScreenFragment_to_chatLayoutFragment, bundle)
    }




}
















































//override fun onDataChange(snapshot: DataSnapshot) {
//    if (snapshot.hasChild("lastMessage")) {
//        val conversationModel: ConversationModel = snapshot.getValue(ConversationModel::class.java)!!
//        conversationModel.lastMessage = snapshot.child("lastMessage").value as String
//        users!!.add(conversationModel)
//        ConversationuserAdapter!!.notifyDataSetChanged()
//    }
//}