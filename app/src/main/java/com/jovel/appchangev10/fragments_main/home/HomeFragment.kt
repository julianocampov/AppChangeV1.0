package com.jovel.appchangev10.fragments_main.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.databinding.FragmentHomeBinding
import com.jovel.appchangev10.model.Product

class HomeFragment : Fragment() {

    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var productsAdapter: ProductsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)


        productsAdapter = ProductsAdapter( )
        homeBinding.productsRecyclerView.apply {
            layoutManager = GridLayoutManager(this@HomeFragment.context, 2)
            adapter = productsAdapter
            setHasFixedSize(false)
        }

        loadFromFB()

        return homeBinding.root
    }

    private fun loadFromFB() {
        val db = Firebase.firestore
        db.collection("products").get().addOnSuccessListener { result ->
            homeBinding.pruebaTexTView.text = "ENTRE"
            var listProducts: MutableList<Product> = arrayListOf()
            for (document in result){
                Log.d("cosas", document.data.toString())
                listProducts.add(document.toObject<Product>())
            }
            productsAdapter.appendItems(listProducts)
        }
    }
}