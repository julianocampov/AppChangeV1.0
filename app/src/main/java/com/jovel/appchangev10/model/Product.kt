package com.jovel.appchangev10.model

data class Product (
        var id: String? = null,
        var idOwner: String? = null,
        var urlImage: String? = null,
        var title: String? = null,
        var description: String? = null,
        var city: String? = null,
        var preferences: MutableList<String>? = null,
        var categorys: MutableList<String>? = null,
        var state: String? = null
)