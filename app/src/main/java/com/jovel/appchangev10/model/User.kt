package com.jovel.appchangev10.model

import com.jovel.appchangev10.ProviderType

data class User(
        var id: String? = null,
        var name: String? = null,
        var email: String? = null,
        var qualification: Double? = null,
        var changes: Int? = null,
        var phone: String? = null,
        var urlProfileImage: String? = null,
        var address: String? = null,
        var city: String? = null,
        //chats
        var favorites : MutableList<String>? = null,
        //var products: MutableList<Product>? = null,
        var provider: ProviderType? = null
        )