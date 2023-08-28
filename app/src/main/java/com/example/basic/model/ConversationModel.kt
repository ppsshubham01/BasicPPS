package com.example.basic.model

import java.io.Serializable

data class ConversationModel(
    var user: User?=null,
    var lastMessage:Message?=null,
    var conversationUid:String?=null,
    val createdAt:Any?=null,
    val updatedAt:Any?=null,
    val members: List<String>?=null
):Serializable
