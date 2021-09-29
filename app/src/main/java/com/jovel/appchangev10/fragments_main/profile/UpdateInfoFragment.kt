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
import androidx.core.widget.doAfterTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.FragmentUpdateInfoBinding
import com.jovel.appchangev10.model.User
import com.jovel.appchangev10.utils.USER_NAME_LENGTH
import com.jovel.appchangev10.utils.lengthString
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class UpdateInfoFragment : Fragment() {

    private lateinit var updateInfoFragmentBinding: FragmentUpdateInfoBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: User
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var reppassword: String
    private lateinit var phone: String
    private lateinit var address: String
    private lateinit var city: String
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
            readTextInputs()
            if (name != null && validateName() && imageCreated) {
                updateInfoFragmentBinding.nameEditText.doAfterTextChanged {
                    updateInfoFragmentBinding.nameTextInputLayout.error = null
                }
                saveUser()
            }else{
                updateInfo("",id.toString(),db)
            }
        }
        updateInfoFragmentBinding.deletePictureButton.setOnClickListener {
            imageCreated = false
            Picasso.get().load(R.drawable.not_picture)
                .into(updateInfoFragmentBinding.editProfileImageView)

        }

        return updateInfoFragmentBinding.root
    }

    private fun validateName(): Boolean {
        if (lengthString(name, USER_NAME_LENGTH)) return true
        updateInfoFragmentBinding.nameTextInputLayout.error = getString(R.string.enter_valid_name)
        return lengthString(name, USER_NAME_LENGTH)
    }

    private fun readTextInputs() {
        with(updateInfoFragmentBinding) {
            name = nameEditText.text.toString()
            email = emailEditText.text.toString()
            phone = phoneEditText.text.toString()
            address = addressEditText.text.toString()
            city = cityEditText.text.toString()
        }
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
                updateInfo(task.result.toString(), id_user, db)
            }
        }
    }


    private fun updateInfo(urlProfileImage: String, id_user: String, db: FirebaseFirestore) {
        val documentUpdate = HashMap<String, Any>()
        documentUpdate["name"] = updateInfoFragmentBinding.nameEditText.text.toString()
        documentUpdate["email"] = updateInfoFragmentBinding.emailEditText.text.toString()
        documentUpdate["phone"] = updateInfoFragmentBinding.phoneEditText.text.toString()
        documentUpdate["address"] = updateInfoFragmentBinding.addressEditText.text.toString()
        documentUpdate["city"] = updateInfoFragmentBinding.cityEditText.text.toString()
        if(urlProfileImage.isNotEmpty()){
            documentUpdate["urlProfileImage"] = urlProfileImage
        }else{
            db.collection("users").document(id_user).update("urlProfileImage", null)
        }

        db.collection("users").document(id_user).update(documentUpdate)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Usuario actualizado", Toast.LENGTH_SHORT)
                    .show()
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
            emailEditText.isEnabled  = false
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