package com.example.basic.model

import java.io.Serializable

data class User(var uid: String? = null,
                var name: String? = null,
                var phoneNumber: String? = null,
                var profileImage: String? = null):Serializable

