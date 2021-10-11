package com.jovel.appchangev10.model

import java.io.Serializable

data class Chat(
    val chatId: String? = null,
    val users: List<String>? = null
) : Serializable