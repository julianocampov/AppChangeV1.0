package com.jovel.appchangev10.fragments_main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.databinding.ActivityRegisterBinding
import com.jovel.appchangev10.databinding.FragmentFavoritesBinding
import com.jovel.appchangev10.model.User


class FavoritesFragment : Fragment() {

    private lateinit var favoritesBinding : FragmentFavoritesBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        favoritesBinding = FragmentFavoritesBinding.inflate(inflater, container, false)

        auth = Firebase.auth

        val id = auth.currentUser?.uid

        val db = Firebase.firestore
        /*
        if (id != null) {
            db.collection("users").get().addOnSuccessListener {
                for(document in it){
                    if(document.id == id){
                        val user : User? = document.toObject()
                        if (user != null) {
                            favoritesBinding.textView.text = user.name
                        }
                    }
                }
            }
        }*/

        return favoritesBinding.root
    }
}