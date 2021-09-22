package com.jovel.appchangev10.fragments_main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.FragmentUpdateInfoBinding

class UpdateInfoFragment : Fragment() {

    private lateinit var updateInfoFragmentBinding: FragmentUpdateInfoBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth
        val id = auth.currentUser?.uid
        val db = Firebase.firestore
        val documen_user = db.collection("users").document(id!!).get()


        updateInfoFragmentBinding = FragmentUpdateInfoBinding.inflate(inflater, container, false)

        updateInfoFragmentBinding.toolbar3.setNavigationOnClickListener{
            activity?.onBackPressed()
        }

        updateInfoFragmentBinding.addPictureButton.setOnClickListener {

        }




        return updateInfoFragmentBinding.root
    }
}