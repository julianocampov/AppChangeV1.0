package com.jovel.appchangev10.model

data class Product (
    var id: String,
    var idOwner: String,
    var urlImage: String,
    var title: String,
    var description: String,
    var city: String,
    var preferences: MutableList<String>,
    var categorys: MutableList<String>
        )