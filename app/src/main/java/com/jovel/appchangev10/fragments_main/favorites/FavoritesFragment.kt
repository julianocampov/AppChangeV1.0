package com.jovel.appchangev10.fragments_main.favorites

import android.os.Bundle
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
        findNavController().navigate(FavoritesFragmentDirections.actionNavigationFavoritesToProductFragment(favorite))
    }

    private fun loadFromDB() {
        auth = Firebase.auth
        val id = auth.currentUser?.uid
        val db = Firebase.firestore
        val listFavorites: MutableList<Product> = arrayListOf()
        db.collection("users").get().addOnSuccessListener {
            for(document in it){
                if(document.id == id){
                    val user : User = document.toObject()
                    user.favorites?.let { it1 -> favoritesAdapter.appendItems(it1) }
                }
            }
        }
    }
}