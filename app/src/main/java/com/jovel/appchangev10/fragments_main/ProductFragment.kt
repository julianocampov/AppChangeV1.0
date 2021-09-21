package com.jovel.appchangev10.fragments_main

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.FragmentProductBinding
import com.jovel.appchangev10.model.Product
import com.jovel.appchangev10.model.User
import com.squareup.picasso.Picasso

class ProductFragment : Fragment() {

    private lateinit var productBinding: FragmentProductBinding
    private lateinit var auth: FirebaseAuth
    private val args: ProductFragmentArgs by navArgs()
    private var user: User? = null
    private lateinit var fav: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productBinding = FragmentProductBinding.inflate(inflater, container, false)

        val product = args.product
        auth = Firebase.auth
        val id = auth.currentUser?.uid
        val db = Firebase.firestore

        loadUser(id, db)
        loadOwnerFromFB(db, product)
        loadProductData(product)

        if (id == product.idOwner) {
            adminProduct(id, db, product)

        } else {
            loadFavorites(id, db, product)

            productBinding.likeTextView.setOnClickListener {
                if (user != null) {
                    fav = user!!.favorites!!

                    if (!user!!.favorites!!.contains(product.id)) {
                        fav.add(product.id.toString())
                        if (id != null)
                            db.collection("users").document(id).update("favorites", fav)
                        productBinding.likeTextView.setBackgroundResource(R.drawable.ic_favorite)

                    } else {
                        fav.remove(product.id.toString())
                        if (id != null)
                            db.collection("users").document(id).update("favorites", fav)
                        productBinding.likeTextView.setBackgroundResource(R.drawable.ic_favorite_border)
                    }
                }
            }
        }

        productBinding.toolbar3.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        return productBinding.root
    }

    private fun adminProduct(id: String?, db: FirebaseFirestore, product: Product) {

        with(productBinding) {

            likeTextView.setBackgroundResource(R.drawable.ic_delete)
            editTextView.setBackgroundResource(R.drawable.ic_edit)

            editTextView.setOnClickListener {
                findNavController().navigate(ProductFragmentDirections.actionProductFragmentToNavigationChange(product))
            }

            likeTextView.setOnClickListener {
                val alertDialog: AlertDialog? = activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setTitle(R.string.title_delete)
                        setMessage(getString(R.string.alert_delete_product))
                        setPositiveButton(R.string.accept) { dialog, it ->
                            db.collection("products").document(product.id!!).delete()
                            db.collection("users").document(id!!)
                                .collection("products").document(product.id!!).delete()

                            val storageRef = FirebaseStorage.getInstance()
                            val pictureRef = storageRef.reference.child("products").child(product.id!!)
                            pictureRef.delete()
                            activity?.onBackPressed()
                        }
                        setNegativeButton(R.string.cancel) { dialog, id ->
                        }
                    }
                    builder.create()
                }
                alertDialog?.show()
            }
        }
    }

    private fun loadUser(id: String?, db: FirebaseFirestore) {
        db.collection("users").get()
            .addOnSuccessListener {
                for (document in it) {
                    if (document.id == id) {
                        user = document.toObject()
                    }
                }
            }
    }


    private fun loadFavorites(id: String?, db: FirebaseFirestore, product: Product) {
        db.collection("users").document(id!!).get().addOnSuccessListener { it1 ->
            val user: User = it1.toObject()!!
            if (user.favorites!!.contains(product.id)) {
                productBinding.likeTextView.setBackgroundResource(R.drawable.ic_favorite)
            } else {
                productBinding.likeTextView.setBackgroundResource(R.drawable.ic_favorite_border)
            }
        }
    }

    private fun loadOwnerFromFB(db: FirebaseFirestore, product: Product) {
        db.collection("users").get().addOnSuccessListener {
            for (document in it) {
                if (document.id == product.idOwner) {
                    val user: User = document.toObject()
                    productBinding.nameOwnerTextView.text = user.name
                    Picasso.get().load(user.urlProfileImage)
                        .into(productBinding.profileOwnerImageView)
                }
            }
        }
    }

    private fun loadProductData(product: Product) {
        with(productBinding) {
            titleTextView.text = product.title
            descriptionTextView.text = "Descripción : " + product.description
            locationTextView.text = product.ubication
            conditionTextView.text = "Condición : " + product.state
            nameOwnerTextView.text = product.idOwner
            loadCategoriesTextView.text = "Categorias : " + product.categories
        }
        if (product.urlImage != null)
            Picasso.get().load(product.urlImage).into(productBinding.productImgImageView)
    }
}