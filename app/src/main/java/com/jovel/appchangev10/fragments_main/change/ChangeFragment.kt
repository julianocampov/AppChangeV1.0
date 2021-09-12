package com.jovel.appchangev10.fragments_main.change

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
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.jovel.appchangev10.databinding.FragmentChangeBinding
import com.jovel.appchangev10.model.Product
import java.io.ByteArrayOutputStream

class ChangeFragment : Fragment() {

    private lateinit var changeBinding: FragmentChangeBinding
    var numberPictures = 0

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                changeBinding.addProductImageView.setImageBitmap(imageBitmap)
            }

        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        changeBinding = FragmentChangeBinding.inflate(inflater, container, false)


        with(changeBinding) {
            addPicturesButton.setOnClickListener {

                if (numberPictures == 3) {
                    addProductButton.isClickable = false
                } else {
                    numberPictures += 1
                    dispatchTakePictureIntent()
                }
            }
        }

        changeBinding.addProductButton.setOnClickListener {
            saveProduct()
        }

        return changeBinding.root
    }

    private fun saveProduct() {
        val db = Firebase.firestore
        val id_user_prueba = "Jbj2es7yWpOBmSXJMgCu3U4XInF3"
        val document_product_in_user = db.collection("users").document(id_user_prueba).collection("products").document()
        val id_product = document_product_in_user.id

        var storageRef = FirebaseStorage.getInstance()
        val pictureRef = storageRef.reference.child("products").child(id_product)
        changeBinding.addProductButton.isDrawingCacheEnabled = true
        changeBinding.addProductButton.buildDrawingCache()

        val bitmap = (changeBinding.addProductImageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = pictureRef.putBytes(data)    //Sube la información
        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            pictureRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val urlPicture = task.result.toString()           //URL de la imagen
                with(changeBinding) {
                    val title = titleProductEditText.text.toString()
                    val description = descriptionProductEditText.text.toString()
                    val ubication = ubicationProductEditText.text.toString()
                    val state = stateSpinner.selectedItem.toString()
                    val product = Product(id = id_product, idOwner = id_user_prueba, urlImage = urlPicture, title=title,description= description,null,null, state = state)
                    //db.collection("pruducts").document(id_product).set(product)
                    //db.collection("users").document(id_user_prueba).collection("products").document(id_product).set(product)
                    //intento de change


                    cleanViews()
                }
            } else {
                // Handle failures
                // ...
            }
        }
    }

    private fun cleanViews() {
        with(changeBinding) {
            numberPictures = 0
            titleProductEditText.setText("")
            descriptionProductEditText.setText("")
            ubicationProductEditText.setText("")

        }
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(intent)
    }
}