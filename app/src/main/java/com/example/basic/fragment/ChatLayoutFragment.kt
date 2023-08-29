package com.example.basic.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.basic.R
import com.example.basic.adapter.MesageAdapter
import com.example.basic.databinding.FragmentChatLayoutBinding
import com.example.basic.model.ConversationModel
import com.example.basic.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.Date


class ChatLayoutFragment : Fragment() {

    private var conversation: ConversationModel? = null

    //    private lateinit var conversationModel: ConversationModel
    private lateinit var binding: FragmentChatLayoutBinding
    private lateinit var adapter: MesageAdapter
    private var messagelist: MutableList<Message> = ArrayList()
    private var conversationRoom: String? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var dialog: ProgressDialog
    private lateinit var sendUID: String
    private lateinit var receiverUID: String

    private val args : ChatLayoutFragmentArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChatLayoutBinding.inflate(inflater, container, false)

        messagelist = ArrayList()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        dialog = ProgressDialog(requireContext())
        dialog.setMessage("Uploading Image...!")
        dialog.setCancelable(false)

//get name /profile uid
        conversation = arguments?.getSerializable("conversation") as ConversationModel?
//        conversation = args.conversation

        val name = arguments?.getString("name")
        val profile = arguments?.getString("image")
        sendUID = FirebaseAuth.getInstance().currentUser!!.uid
        receiverUID = arguments?.getString("uid")!!
//        val user = arguments?.getSerializable("user",ConversationModel::class.java)
        Log.d("ChatlayoutFragment1", receiverUID)



        binding.name.text = name
        Glide.with(this).load(profile).placeholder(R.drawable.icimage_placeholder)
            .into(binding.profile01)

        //....... Retrieving user's presence status from Firebase
        database.reference.child("presence").child(receiverUID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.getValue(String::class.java)
                        if (status == "offline") {
                            binding.status.visibility = View.GONE
                        } else {
                            binding.status.text = status
                            binding.status.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })


//        // Create an OnBackPressedCallback to handle the back button press
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

//                requireActivity().supportFragmentManager.popBackStack()
                findNavController().navigate(ChatLayoutFragmentDirections.actionChatLayoutFragmentToMainScreenFragment())
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        binding.imageViewBack.setOnClickListener {
//            requireActivity().supportFragmentManager.popBackStack()
//            findNavController().popBackStack()
            findNavController().navigate(ChatLayoutFragmentDirections.actionChatLayoutFragmentToMainScreenFragment())
        }

//        database.reference.child("users").child("conversation")

        conversationRoom = conversation?.conversationUid ?: (sendUID + "_" + receiverUID)

//        intialized message adapter for recyclerview in layout....................................................................>>>>>>>>>>>>.#############
        adapter = MesageAdapter(requireContext(), messagelist)
        binding.ChatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.ChatRecyclerView.adapter = adapter

        Log.d("sendroom", "${conversationRoom}")
        // Chats Messages From Database

        database.reference.child("conversations")
            .child(conversationRoom!!)
            .addValueEventListener(object : ValueEventListener {
                //get datafrom firebase to display in chat
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messagelist.clear()

//                    snapshot.children.forEach {
//                        val message: Message = it.getValue(Message::class.java)!!
//                        message.messageId = it.key
//                        messages.add(message)
//                    }

                    for (snapshot1 in snapshot.children) {

                        val message: Message? = snapshot1.getValue(Message::class.java)
                        Log.d("chat1", "${snapshot1}")
                        messagelist.add(message!!)
                        Log.d("chat2", "${message}")
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        sendmsg()


//..Attaching an image
        binding.attachment.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 2)

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_chatLayoutFragment_to_mainScreenFragment)

//            requireActivity().dispatchTouchEvent()
        }
    }

    private fun sendmsg() {

        // Sending a text message
        binding.msgSentBtn.setOnClickListener {


            val conversationNode = database.reference.child("conversations").child(conversationRoom!!)
            val messageKey = conversationNode.push().key

//           create a message object
            val messageTxt: String = binding.messageBox.text.toString()
            val message = Message(
                messageId = null,
                message = messageTxt,
                senderId = sendUID,
                receiverId = receiverUID,
                imageUrl = "",
                timestamp = System.currentTimeMillis()
            )

//            val lastMsgOBJ = HashMap<String, Any>()
//            lastMsgOBJ["lastMessage"] = message
//            lastMsgOBJ["updatedAt"] = ServerValue.TIMESTAMP


            messageKey?.let { it1 ->
                message.messageId = messageKey
                conversationNode.child(it1).setValue(message)
            }

            //create conversation if not exist
            if (conversation == null) {
                conversation = ConversationModel(
                    lastMessage = message,
                    updatedAt = ServerValue.TIMESTAMP,
                    conversationUid = conversationRoom,
                    members = arrayListOf(sendUID, receiverUID),
                    createdAt = ServerValue.TIMESTAMP,
                    user = null
                )
            }
            else{
                conversation?.updatedAt=ServerValue.TIMESTAMP
                conversation?.lastMessage=message
            }
            database.reference.child("users").child(sendUID).child("conversations")
                .child(conversationRoom!!).setValue(conversation)
            database.reference.child("users").child(receiverUID).child("conversations")
                .child(conversationRoom!!).setValue(conversation)

            //Updating last message and time in the chat rooms


            Log.d("ChatLayoutFragment", "sendRoom: ${conversationRoom} ")

// ............................................................Storing the message in the sender and receiver chat rooms
            database.reference.child("conversations").child(conversationRoom!!).child(messageKey!!).setValue(message).addOnSuccessListener {

//                    database.reference.child("conversations").child(conversationRoom!!).child(messageKey).child(randomKey).push()
//                        .setValue(message)
                }

            binding.messageBox.setText("")

        }
    }

    private fun receiveMsg() {


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 25) {
            if (data != null) {
                // Check data contains a Uri(image data)
                if (data.data != null) {
                    val selectedImage = data.data // Get the selected image URI
                    val calendar = Calendar.getInstance()

                    // Create a reference to the Firebase Storage location for storing the image
                    val reference = storage.reference.child("conversations")
                        .child(calendar.timeInMillis.toString() + "")
                    dialog.show()

                    // Upload the selected image to Firebase Storage
                    reference.putFile(selectedImage!!).addOnCompleteListener { task ->
                        dialog.dismiss()
                        if (task.isSuccessful) {

                            reference.downloadUrl.addOnSuccessListener { uri ->
                                val filePath = uri.toString() // URL of the uploaded image
                                val messageTxt: String = binding.messageBox.text.toString()
                                val date = Date()


                                val message = Message(
                                    messageId = null,
                                    message = messageTxt,
                                    senderId = sendUID,
                                    imageUrl = "",
                                    timestamp = date.time
                                )
                                message.message = "photo"
                                message.imageUrl = filePath
                                binding.messageBox.setText("")

                                val randomKey = database.reference.push().key
                                // For update last message info in the chat
                                val lastMsgObj = HashMap<String, Any>()
                                lastMsgObj["lastMsg"] = message.message!!
                                lastMsgObj["lastMsgTime"] = date.time

                                database.reference.child("conversations").updateChildren(lastMsgObj)
                                database.reference.child("conversations").child(receiverUID)
                                    .updateChildren(lastMsgObj)

// Add the message to the sender's chat messages
                                database.reference.child("conversations").child(sendUID)
                                    .child(randomKey!!)
                                    .setValue(message).addOnSuccessListener {
                                        // add the message to the receiver's chat messages
                                        database.reference.child("conversations").child(receiverUID)
                                            .child(randomKey)
                                            .setValue(message).addOnSuccessListener {
                                                // Message added successfully
                                            }
                                    }
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database.reference.child("presence")
            .child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database.reference.child("presence")
            .child(currentId!!).setValue("Offline")
    }


}


//fun sendMessage(recipientId: String, message: String) {
//    // Get the current user's ID.
//    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
//
//    // Create a message object.
//    val messageObject = Message(
//        senderId = currentUserId,
//        recipientId = recipientId,
//        body = message
//    )
//
//    // Save the message to the database.
//    FirebaseFirestore.getInstance().collection("messages").add(messageObject)
//
//    // Send a notification to the recipient.
//    val notification = Notification("New message from ${currentUserId}", message)
//    FirebaseMessaging.getInstance().send(notification)
//}


//        usertypignStaus()

//    private fun usertypignStaus() {
//        val handler = Handler()
//        binding.messageBox.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                val enteredText = p0.toString()
//
//                if (enteredText.isNotEmpty()) {
//                    database.reference.child("Presence").child(sendUID!!)
//                        .setValue("typing...")
//                } else {
//                    database.reference.child("Presence").child(sendUID!!)
//                        .setValue("Online")
//                }
//                handler.removeCallbacksAndMessages(null)
//                handler.postDelayed(userStoppedTyping, 1000)
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//
//
//            }
//
//            // Runnable to set user's status to "Online" after typing stops
//            var userStoppedTyping = Runnable {
//                database.reference.child("Presence")
//                    .child(sendUID!!)
//                    .setValue("Online")
//            }
//
//        })
//
//
//    }




