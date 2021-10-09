package com.jovel.appchangev10.fragments_main.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.databinding.FragmentMyProductsBinding
import com.jovel.appchangev10.fragments_main.home.ProductsAdapter
import com.jovel.appchangev10.model.Product

class MyProductsFragment : Fragment() {

    private lateinit var myProductsBinding: FragmentMyProductsBinding
    private lateinit var myProductsAdapter: ProductsAdapter
    private val args : MyProductsFragmentArgs by navArgs()
    private lateinit var otherProduct: Product

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        myProductsBinding = FragmentMyProductsBinding.inflate(inflater, container, false)
        otherProduct = args.otherProduct

        myProductsAdapter = ProductsAdapter(onItemClicked = {onProductClicked(it)})

        myProductsBinding.myProductsRV.apply {
            layoutManager = GridLayoutManager(this@MyProductsFragment.context, 2)
            adapter = myProductsAdapter
            setHasFixedSize(false)
        }

        loadProductsFromFB()

        return myProductsBinding.root
    }

    private fun loadProductsFromFB() {
        val auth = Firebase.auth
        val id = auth.currentUser?.uid
        val db = Firebase.firestore

        db.collection("users").document(id!!).collection("products").get().addOnSuccessListener { result ->
            val listProducts: MutableList<Product> = arrayListOf()
            for (document in result) {
                val product: Product = document.toObject()
                listProducts.add(product)
            }
            myProductsAdapter.appendItems(listProducts)
        }
    }

    private fun onProductClicked(product: Product) {
        val auth = Firebase.auth
        val myId = auth.currentUser?.uid
        val db = Firebase.firestore
        val idOtherUser = otherProduct.idOwner

        db.collection("users").document(myId!!).collection("chats")
        db.collection("users").document(idOtherUser!!)
        TODO("pendiente")

    }
}