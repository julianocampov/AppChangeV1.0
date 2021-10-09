package com.jovel.appchangev10.fragments_main.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.databinding.FragmentFavoritesBinding
import com.jovel.appchangev10.fragments_main.home.ProductsAdapter
import com.jovel.appchangev10.model.Product
import com.jovel.appchangev10.model.User


class FavoritesFragment : Fragment() {

    private lateinit var favoritesBinding: FragmentFavoritesBinding
    private lateinit var favoritesAdapter: ProductsAdapter
    private lateinit var auth: FirebaseAuth
    private var listFavorites: MutableList<Product> = arrayListOf()
    private lateinit var fav: MutableList<String>
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        favoritesBinding = FragmentFavoritesBinding.inflate(inflater, container, false)

        favoritesAdapter = ProductsAdapter(onItemClicked = { onFavoriteItemClicked(it) })
        favoritesBinding.favoritesRecyclerView.apply {
            layoutManager = GridLayoutManager(this@FavoritesFragment.context, 2)
            adapter = favoritesAdapter
            setHasFixedSize(false)
        }

        auth = Firebase.auth
        val id = auth.currentUser?.uid

        loadFromDB(id)

        return favoritesBinding.root
    }

    private fun onFavoriteItemClicked(favorite: Product) {
        listFavorites.clear()
        findNavController().navigate(
            FavoritesFragmentDirections.actionNavigationFavoritesToProductFragment(
                favorite
            )
        )
    }

    private fun loadFromDB(id: String?) {
        db.collection("users").document(id!!).get().addOnSuccessListener { it1 ->

            val user: User? = it1.toObject()

            if (user != null) {
                if (user.favorites != null) {

                    fav = user.favorites!!

                    for (f in user.favorites!!) {
                        db.collection("products").document(f).get()
                            .addOnSuccessListener { it2 ->

                                val product: Product? = it2.toObject()

                                if (user.favorites?.contains(product?.id) == true) {
                                    if (product != null) {
                                        listFavorites.add(product)
                                        favoritesAdapter.appendItems(listFavorites)
                                    }
                                }
                                if (product == null) {
                                    fav.remove(f)
                                    db.collection("users").document(id).update("favorites", fav)
                                }
                            }
                    }
                }
            }
        }
    }
}