package com.jovel.appchangev10.fragments_main.profile

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.LoginActivity
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.FragmentProfileBinding
import com.jovel.appchangev10.fragments_main.MyProductsAdapter
import com.jovel.appchangev10.model.Product
import com.jovel.appchangev10.model.User
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var profileBinding: FragmentProfileBinding
    private lateinit var productsAdapter: MyProductsAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var user : User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        profileBinding = FragmentProfileBinding.inflate(inflater, container, false)

        productsAdapter = MyProductsAdapter(onProductClicked = {onProductItemClicked2(it)})
        profileBinding.myProductsRecyclerView.apply {
            layoutManager = GridLayoutManager(this@ProfileFragment.context, 2)
            adapter = productsAdapter
            setHasFixedSize(false)
        }

        auth = Firebase.auth
        val id = auth.currentUser?.uid
        val db = Firebase.firestore

        loadDataFromFB(db,id)

        profileBinding.toolbar3.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }

        return profileBinding.root
    }

    private fun loadDataFromFB(db: FirebaseFirestore, id: String?) {
        if (id != null) {
            db.collection("users").get().addOnSuccessListener {
                for(document in it){
                    if(document.id == id){
                        user = document.toObject()
                        profileBinding.nameTextView.text = user.name
                        profileBinding.changesTextView.text = user.changes.toString()
                        profileBinding.qualifTextView.text = user.qualification.toString()
                        if (user.urlProfileImage != null) {
                            Picasso.get().load(user.urlProfileImage)
                                .into(profileBinding.profileImageView)
                        }else{
                            Picasso.get().load(R.drawable.not_picture)
                                .into(profileBinding.profileImageView)
                        }
                    }
                }
            }

            db.collection("products").get().addOnSuccessListener { result ->
                val listProducts: MutableList<Product> = arrayListOf()
                for (document in result){
                    var product: Product = document.toObject()
                    if(product.idOwner==id)
                        listProducts.add(document.toObject())
                }
                productsAdapter.appendItems(listProducts)
            }
        }

    }

    private fun onProductItemClicked2(product: Product) {
        findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToProductFragment(product))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.salir -> {
                Firebase.auth.signOut()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                onDetach()
            }
            R.id.ch_personal_info -> {
                findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToUpdateInfoFragment())
            }
            R.id.ch_password -> {
                findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToChangePasswordFragment(user))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}