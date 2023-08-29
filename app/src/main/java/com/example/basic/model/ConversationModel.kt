package com.example.basic.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.io.Serializable

data class ConversationModel(
    var user: User? = null,
    var lastMessage: Message? = null,
    var conversationUid: String? = null,
    val createdAt:  Any? = null,
    var updatedAt:  Any? = null,
    val members: ArrayList<String>? = null
) : Serializable
