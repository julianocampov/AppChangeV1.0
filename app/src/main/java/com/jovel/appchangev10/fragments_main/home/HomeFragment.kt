package com.jovel.appchangev10.fragments_main.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.databinding.FragmentHomeBinding
import com.jovel.appchangev10.model.Category
import com.jovel.appchangev10.model.Product

class HomeFragment : Fragment() {

    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)


        productsAdapter = ProductsAdapter( onItemClicked = {onProductItemClicked(it)} )
        homeBinding.productsRecyclerView.apply {
            layoutManager = GridLayoutManager(this@HomeFragment.context, 2)
            adapter = productsAdapter
            setHasFixedSize(false)
        }

        categoriesAdapter = CategoriesAdapter( onItemClicked = {onCategoryItemClicked(it)})
        homeBinding.categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeFragment.context, RecyclerView.HORIZONTAL, false)
            adapter = categoriesAdapter
            setHasFixedSize(false)
        }

        loadFromFB()

        return homeBinding.root
    }

    private fun onCategoryItemClicked(category: Category) {
        //TODO filtrar los productos por categoria
    }

    private fun onProductItemClicked(product: Product) {
        //TODO ir a fragment producto
        findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToProductFragment(product))
    }

    private fun loadFromFB() {
        val db = Firebase.firestore
        db.collection("products").get().addOnSuccessListener { result ->
            var listProducts: MutableList<Product> = arrayListOf()
            for (document in result){
                Log.d("cosas", document.data.toString())
                listProducts.add(document.toObject<Product>())
            }
            productsAdapter.appendItems(listProducts)
        }
        db.collection("categories").get().addOnSuccessListener { result ->
            var listCategories : MutableList<Category> = arrayListOf()
            for (document in result){
                val c : Category = document.toObject()
                c.let { listCategories.add(it) }
            }
            categoriesAdapter.appendItems(listCategories)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}