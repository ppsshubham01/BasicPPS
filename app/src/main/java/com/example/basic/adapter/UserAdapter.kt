package com.example.basic.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basic.R
import com.example.basic.databinding.ItemProfileBinding
import com.example.basic.model.ConversationModel
import com.example.basic.model.User

class UserAdapter(var context: Context, var userList: MutableList<User>, val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

//..............inner class UserViewHolder that extends RecyclerView.ViewHolder.
// .............It holds references to the views in your list item layout using data binding.
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ItemProfileBinding = ItemProfileBinding.bind(itemView)

    }
//.................his method is called when the RecyclerView needs a new ViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false)
        return UserViewHolder(v)
    }
//.................This method binds data to the views of the ViewHolder. It gets called for each item in the list.
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.username.text = user.name

        Log.d("Profile", user.toString())
        Log.d("ProfileImageURL", user.profileImage.toString())

        Glide.with(context).load(user.profileImage).placeholder(R.drawable.user)
            .into(holder.binding.profile)

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(user)
        }
    }
//.................This method returns the number of items in the list.
    override fun getItemCount(): Int = userList.size

    interface OnItemClickListener{
        fun onItemClick(user: User)
    }
}