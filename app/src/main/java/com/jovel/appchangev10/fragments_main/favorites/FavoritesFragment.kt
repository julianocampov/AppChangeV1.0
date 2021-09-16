package com.jovel.appchangev10.fragments_main.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.databinding.FragmentFavoritesBinding
import com.jovel.appchangev10.fragments_main.home.HomeFragmentDirections
import com.jovel.appchangev10.fragments_main.home.ProductsAdapter
import com.jovel.appchangev10.model.Product
import com.jovel.appchangev10.model.User


class FavoritesFragment : Fragment() {

    private lateinit var favoritesBinding : FragmentFavoritesBinding
    private lateinit var favoritesAdapter: ProductsAdapter
    private lateinit var auth: FirebaseAuth
    private var listFavorites: MutableList<Product> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        favoritesBinding = FragmentFavoritesBinding.inflate(inflater, container, false)

        favoritesAdapter = ProductsAdapter ( onItemClicked = {onFavoriteItemClicked(it)})
        favoritesBinding.favoritesRecyclerView.apply {
            layoutManager = GridLayoutManager(this@FavoritesFragment.context, 2)
            adapter = favoritesAdapter
            setHasFixedSize(false)
        }

        loadFromDB()

        return favoritesBinding.root
    }

    private fun onFavoriteItemClicked(favorite: Product) {
        listFavorites.clear()
        findNavController().navigate(FavoritesFragmentDirections.actionNavigationFavoritesToProductFragment(favorite))
    }

    private fun loadFromDB() {
        auth = Firebase.auth
        val id = auth.currentUser?.uid
        val db = Firebase.firestore
        db.collection("users").get().addOnSuccessListener { it1 ->
            for(document in it1){
                if(document.id == id){
                    val user : User = document.toObject()
                    db.collection("products").get().addOnSuccessListener { it2 ->
                        for (prod in it2){
                            val product : Product = prod.toObject()
                            if (user.favorites?.contains(product.id) == true){
                                Log.d("aqws", product.title.toString())
                                listFavorites.add(product)
                            }
                        }
                        favoritesAdapter.appendItems(listFavorites)
                    }
                }
            }
        }
    }
}