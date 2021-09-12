package com.jovel.appchangev10.fragments_main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.databinding.FragmentProductBinding
import com.jovel.appchangev10.model.Product
import com.squareup.picasso.Picasso

class ProductFragment : Fragment() {

    private lateinit var productBinding: FragmentProductBinding
    private lateinit var auth: FirebaseAuth

    private val args : ProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productBinding = FragmentProductBinding.inflate(inflater, container, false)

        val product = args.product

        productBinding.likeTextView.setOnClickListener{
            //TODO agregar el producto a los favoritos del usuario
            auth = Firebase.auth
            val id = auth.currentUser?.uid
            val db = Firebase.firestore
            val listFavorites: MutableList<Product> = arrayListOf()
            db.collection("users")
        }

        productBinding.descriptionTextView.text = product.title

        if (product.urlImage != null)
            Picasso.get().load(product.urlImage).into(productBinding.profileOwnerImageView)
            Picasso.get().load(product.urlImage).into(productBinding.productImgImageView)

        return productBinding.root
    }
}