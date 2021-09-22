package com.jovel.appchangev10.fragments_main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.databinding.FragmentProductBinding
import com.jovel.appchangev10.model.Product
import com.jovel.appchangev10.model.User
import com.squareup.picasso.Picasso

class ProductFragment : Fragment() {

    private lateinit var productBinding: FragmentProductBinding
    private lateinit var auth: FirebaseAuth
    private val args : ProductFragmentArgs by navArgs()
    private var user : User? = null
    private lateinit var fav : MutableList<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productBinding = FragmentProductBinding.inflate(inflater, container, false)

        val product = args.product
        auth = Firebase.auth
        val id = auth.currentUser?.uid
        val db = Firebase.firestore

        db.collection("users").get()
            .addOnSuccessListener {
                for (document in it) {
                    if (document.id == id) {
                        user = document.toObject()
                    }
                }
            }

        productBinding.likeTextView.setOnClickListener{
            if (user != null) {
                fav = user!!.favorites!!
                val documentUpdate = HashMap<String, MutableList<String>>()
                fav.add(product.id.toString())
                documentUpdate["favorites"] = fav
                if (id != null) {
                    db.collection("users").document(id).update(documentUpdate as Map<String, Any>).addOnSuccessListener {
                        Toast.makeText(requireContext(), "Deudor actualizado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        productBinding.toolbar3.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        loadOwnerFromFB(db, product)
        loadProductData(product)

        return productBinding.root
    }

    private fun loadOwnerFromFB(db: FirebaseFirestore, product: Product) {
        db.collection("users").get().addOnSuccessListener {
            for(document in it){
                if(document.id == product.idOwner){
                    val user : User = document.toObject()
                    productBinding.nameOwnerTextView.text = user.name
                    Picasso.get().load(user.urlProfileImage).into(productBinding.profileOwnerImageView)
                }
            }
        }
    }

    private fun loadProductData(product: Product) {
        with(productBinding) {
            titleTextView.text = product.title
            descriptionTextView.text = "Descripción : "+product.description
            locationTextView.text = product.ubication
            conditionTextView.text = "Condición : "+product.state
            nameOwnerTextView.text = product.idOwner
            loadCategoriesTextView.text = "Categorias : "+ product.categories
        }

        if (product.urlImage != null)
            Picasso.get().load(product.urlImage).into(productBinding.productImgImageView)
    }
}