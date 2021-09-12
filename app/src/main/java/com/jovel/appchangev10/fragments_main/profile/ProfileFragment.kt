package com.jovel.appchangev10.fragments_main.profile

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.LoginActivity
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.FragmentProfileBinding
import com.jovel.appchangev10.model.User
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var profileBinding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        profileBinding = FragmentProfileBinding.inflate(inflater, container, false)

        auth = Firebase.auth

        val id = auth.currentUser?.uid

        val db = Firebase.firestore

        if (id != null) {
            db.collection("users").get().addOnSuccessListener {
                for(document in it){
                    if(document.id == id){
                        val user : User = document.toObject()
                        profileBinding.nameTextView.text = user.name
                        profileBinding.changesTextView.text = user.changes.toString()
                        profileBinding.qualifTextView.text = user.qualification.toString()
                        Picasso.get().load(user.urlProfileImage).into(profileBinding.profileImageView)
                    }
                }
            }
        }

        return profileBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menuprofile,menu)
        super.onCreateOptionsMenu(menu, inflater)
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
        }
        return super.onOptionsItemSelected(item)
    }
}