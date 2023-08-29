package com.example.basic.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable


data class Message(
    var messageId: String? = null,
    var message: String? = null,
    var senderId: String? = null,
    var receiverId: String? = null,
    var imageUrl: String? = null,
    var timestamp: Long = 0
): Serializable