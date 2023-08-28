package com.example.basic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basic.R
import com.example.basic.databinding.ItemProfileBinding
import com.example.basic.model.ConversationModel

class ConversationAdapter(
    var context: Context,val conversationuserList: MutableList<ConversationModel>, val  onItemClickListener: OnItemClickListener
     ):
    RecyclerView.Adapter<ConversationAdapter.ConversatiobViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversatiobViewHolder {
        val cv = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false)
        return ConversatiobViewHolder(cv)
    }

    override fun onBindViewHolder(holder: ConversatiobViewHolder, position: Int) {
        val chatuser = conversationuserList[position]

        holder.binding.clientSideMesage.text = chatuser.lastMessage?.message

        holder.binding.username.text = chatuser.user?.name
        Glide.with(context).load(chatuser.user?.profileImage).placeholder(R.drawable.user)
            .into(holder.binding.profile)

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(chatuser)
        }
    }
    override fun getItemCount(): Int =conversationuserList.size



    inner class ConversatiobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ItemProfileBinding = ItemProfileBinding.bind(itemView)
    }
    interface OnItemClickListener{
        fun onItemClick(user: ConversationModel)
    }
}


