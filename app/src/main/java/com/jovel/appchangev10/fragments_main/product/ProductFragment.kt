package com.jovel.appchangev10.fragments_main.product

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
    private lateinit var product: Product


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productBinding = FragmentProductBinding.inflate(inflater, container, false)

        val idProduct = args.product.id
        auth = Firebase.auth
        val id = auth.currentUser?.uid
        val db = Firebase.firestore


        db.collection("products").document(idProduct!!).get().addOnSuccessListener {

            product = it.toObject()!!

            showProductData(product)
            loadUser(id, db)
            loadOwnerFromFB(db, product)

            if (id == product.idOwner) {
                adminProduct(id, db, product)
            } else {
                loadFavorites(id, db, product)

                productBinding.likeTextView.setOnClickListener {
                    addToFavorites(id, db, product)
                }
                productBinding.changeButton.setOnClickListener {
                    findNavController().navigate(
                        ProductFragmentDirections.actionProductFragmentToMyProductsFragment(
                            product
                        )
                    )
                }
            }
        }

        productBinding.toolbar3.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        return productBinding.root
    }

    private fun addToFavorites(id: String?, db: FirebaseFirestore, product: Product) {
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

    private fun adminProduct(id: String?, db: FirebaseFirestore, product: Product) {

        with(productBinding) {

            changeButton.visibility = View.GONE
            likeTextView.setBackgroundResource(R.drawable.ic_delete)
            editTextView.setBackgroundResource(R.drawable.ic_edit)

            editTextView.setOnClickListener {
                findNavController().navigate(
                    ProductFragmentDirections.actionProductFragmentToNavigationChange(
                        product
                    )
                )
            }

            likeTextView.setOnClickListener {
                val alertDialog: AlertDialog? = activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setTitle(R.string.title_delete)
                        setMessage(getString(R.string.alert_delete_product))
                        setPositiveButton(R.string.accept) { dialog, it ->
                            deleteProduct(id, db)
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

    private fun deleteProduct(id: String?, db: FirebaseFirestore) {
        db.collection("products").document(product.id!!).delete()
        db.collection("users").document(id!!)
            .collection("products").document(product.id!!).delete()

        val storageRef = FirebaseStorage.getInstance()
        val pictureRef =
            storageRef.reference.child("products").child(product.id!!)
        pictureRef.delete()
        activity?.onBackPressed()
    }

    private fun loadUser(id: String?, db: FirebaseFirestore) {
        db.collection("users").document(id!!).get()
            .addOnSuccessListener {
                user = it.toObject()
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
        db.collection("users").document(product.idOwner!!).get().addOnSuccessListener {
            val user: User = it.toObject()!!
            productBinding.nameOwnerTextView.text = user.name
            Picasso.get().load(user.urlProfileImage)
                .into(productBinding.profileOwnerImageView)
        }
    }

    private fun showProductData(product: Product) {
        with(productBinding) {
            val arraySpinner = resources.getStringArray(R.array.state_list)
            titleTextView.text = product.title
            descriptionTextView.text =
                getString(R.string.descriptionConc, product.description ?: "")
            locationTextView.text = product.ubication
            conditionTextView.text =
                getString(R.string.conditionConc, arraySpinner[product.state?.toInt()!!])
            nameOwnerTextView.text = product.idOwner
            preferencesTextView.text =
                getString(R.string.preferencesConc, product.preferences ?: "")
            var aux = ""
            for (i in product.categories!!) {
                aux += i + "\n                       "
            }
            loadCategoriesTextView.text = getString(R.string.categoriesConc, aux)
        }
        if (product.urlImage != null)
            Picasso.get().load(product.urlImage).into(productBinding.productImgImageView)
    }
}