package com.jovel.appchangev10.model

data class Message(
    var type: String? = null,
    //Type 0
    var message: String? = null,
    var from: String? = null,
    var date: String? = null,
    //Type 1
    var idProduct1: String? = null,
    var idProduct2: String? = null
)