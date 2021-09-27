package com.jovel.appchangev10.fragments_main.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.FragmentUpdateInfoBinding
import com.jovel.appchangev10.model.Product
import com.jovel.appchangev10.model.User
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class UpdateInfoFragment : Fragment() {

    private lateinit var updateInfoFragmentBinding: FragmentUpdateInfoBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: User
    private var imageCreated = false
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                updateInfoFragmentBinding.editProfileImageView.setImageBitmap(imageBitmap)
                imageCreated = true
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth
        val id = auth.currentUser?.uid
        val db = Firebase.firestore
        db.collection("users").get()
            .addOnSuccessListener {
                for (document in it) {
                    if (document.id == id) {
                        user = document.toObject()
                        loadData(user)
                    }
                }
            }



        updateInfoFragmentBinding = FragmentUpdateInfoBinding.inflate(inflater, container, false)

        updateInfoFragmentBinding.toolbar3.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        updateInfoFragmentBinding.addPictureButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        updateInfoFragmentBinding.saveButton.setOnClickListener {
            saveUser()
        }

        return updateInfoFragmentBinding.root
    }

    private fun saveUser() {
        val db = Firebase.firestore
        auth = Firebase.auth
        val id_user = auth.currentUser?.uid

        val storageRef = FirebaseStorage.getInstance()
        val pictureRef = id_user?.let { storageRef.reference.child("users").child(id_user) }
        updateInfoFragmentBinding.saveButton.isDrawingCacheEnabled = true
        updateInfoFragmentBinding.saveButton.buildDrawingCache()
        val bitmap =
            (updateInfoFragmentBinding.editProfileImageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = pictureRef?.putBytes(data)

        uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            pictureRef.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val urlPirofileImage = task.result.toString()
                val documentUpdate = HashMap<String, Any>()
                documentUpdate["name"] = updateInfoFragmentBinding.nameEditText.text.toString()
                documentUpdate["email"] = updateInfoFragmentBinding.emailEditText.text.toString()
                documentUpdate["phone"] = updateInfoFragmentBinding.phoneEditText.text.toString()
                documentUpdate["address"] = updateInfoFragmentBinding.addressEditText.text.toString()
                documentUpdate["city"] = updateInfoFragmentBinding.cityEditText.text.toString()
                documentUpdate["urlProfileImage"] = urlPirofileImage
                db.collection("users").document(id_user).update(documentUpdate)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Usuario actualizado", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }
    }


    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(intent)
    }


    private fun loadData(user: com.jovel.appchangev10.model.User) {
        with(updateInfoFragmentBinding) {
            nameEditText.setText(user.name)
            emailEditText.setText(user.email)
            phoneEditText.setText(user.phone)
            addressEditText.setText(user.address)
            cityEditText.setText(user.city)
            if (user.urlProfileImage != null) {
                Picasso.get().load(user.urlProfileImage)
                    .into(updateInfoFragmentBinding.editProfileImageView)
            }
            else{
                Picasso.get().load(R.drawable.not_picture)
                    .into(updateInfoFragmentBinding.editProfileImageView)
            }

        }
    }
}