package com.example.basic.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basic.R
import com.example.basic.databinding.DeleteLayoutBinding
import com.example.basic.databinding.ReceiveMsgBinding
import com.example.basic.databinding.SendMsgBinding
import com.example.basic.fragment.ChatLayoutFragment
import com.example.basic.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MesageAdapter(var context: Context, messages: MutableList<Message>?) :    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    lateinit var message: ArrayList<Message>
    val ITEM_SENT = 1
    val ITEM_RECEIVE = 2



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Inflate layout based on message sender type
        return if (viewType == ITEM_SENT) {
            val view = LayoutInflater.from(context).inflate(R.layout.send_msg, parent, false)
            SendMsgHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.receive_msg, parent, false)
            ReceiveMsgHolder(view)
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = message[position]

        if (holder.javaClass == SendMsgHolder::class.java) {
            // Handle sent message
            val viewHolder = holder as SendMsgHolder
            viewHolder.binding.sendMessage.text = message.message

        } else {
            // Handle received message
            val viewHolder = holder as ReceiveMsgHolder
            viewHolder.binding.receiverMSG.text = message.message
        }

//        holder.itemView.setOnClickListener {
////                here write a loic for interface deletete layout and call in chatlayout
//        }
    }
    override fun getItemCount(): Int = message.size

    // ViewHolder for sent messages
    inner class SendMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: SendMsgBinding = SendMsgBinding.bind(itemView)
    }
    // ViewHolder for received messages
    inner class ReceiveMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ReceiveMsgBinding = ReceiveMsgBinding.bind(itemView)
    }
    init {
        if (messages != null) {
            this.message = messages as ArrayList<Message>
        }

    }

    override fun getItemViewType(position: Int): Int {
        val message = message[position]

        // Check if the message sender is the current user or another user
        return if (FirebaseAuth.getInstance().uid == message.senderId) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }



//    fun loadMessages(messages: MutableList<Message>) {
//        this.message = messages as ArrayList<Message>
//        notifyDataSetChanged()
//    }

//    fun addFirst(message: Message) {
//        message.add(0, message)
//        notifyDataSetChanged()
//    }

}








































































//// ........................................................................................................For delete
//            viewHolder.itemView.setOnLongClickListener {
//                // Set up a dialog for message deletion
//                val view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
//                val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
//
//                false
//            }
//// ........................................................................................................END delete







// Check if the message is an image
//if (message.message == "photo") {
//    // Show image placeholder and hide text message views
//    viewHolder.binding.imagePlaceHolder.visibility = View.VISIBLE
//    viewHolder.binding.sendMessage.visibility = View.GONE
//    viewHolder.binding.mLinear.visibility = View.GONE
//    // Load image using Glide library
//    Glide.with(context)
//        .load(message.imageUrl)
//        .placeholder(R.drawable.icimage_placeholder)
//        .into(viewHolder.binding.imagePlaceHolder)
//}


//if (message.message == "photo") {
//    // Show image placeholder and hide text message views
//    viewHolder.binding.imagePlaceHolder.visibility = View.VISIBLE
//    viewHolder.binding.receiverMSG.visibility = View.GONE
//    viewHolder.binding.receiverMLinear.visibility = View.GONE
//    // Load image using Glide library
//    Glide.with(context)
//        .load(message.imageUrl)
//        .placeholder(R.drawable.icimage_placeholder)
//        .into(viewHolder.binding.imagePlaceHolder)
//}





//                val dialog = AlertDialog.Builder(context)
//                    .setTitle("Delete Message")
//                    .setMessage("This is...")
//                    .setView(binding.root)
//                    .create()
//
//                binding.everyoneDelete.setOnClickListener {
//                    // Logic for deleting message
//                    message.message = "This message is removed"
//                    message.messageId?.let { messageId ->
//                        val chatReference =
//                            FirebaseDatabase.getInstance().reference.child("chats")
//                        chatReference.child(sendRoom).child("message").child(messageId)
//                            .setValue(message)
//                        chatReference.child(receiverRoom).child("message").child(messageId)
//                            .setValue(message)
//                    }
//                    dialog.dismiss()
//                }
//                binding.forMeDelete.setOnClickListener {
//                    // Logic for deleting message for the sender only
//                    message.messageId?.let { messageId ->
//                        val chatReference =FirebaseDatabase.getInstance().reference.child("chats")
//                        chatReference.child(sendRoom).child("message").child(messageId)
//                            .setValue(null)
//                    }
//                    dialog.dismiss()
//                }
//                binding.cancel.setOnClickListener { dialog.dismiss() }
//                dialog.show()


