package com.jovel.appchangev10.model

import java.io.Serializable

data class Product (
        var id: String? = null,
        var idOwner: String? = null,
        var urlImage: String? = null,
        var title: String? = null,
        var description: String? = null,
        var ubication: String? = null,
        var preferences: MutableList<String>? = null,
        var categories: MutableList<String>? = null,
        var state: String? = null
) : Serializable
