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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.FragmentChangeBinding
import com.jovel.appchangev10.fragments_main.home.CategoriesTitleAdapter
import com.jovel.appchangev10.model.Category
import com.jovel.appchangev10.model.Product
import java.io.ByteArrayOutputStream

class ChangeFragment : Fragment() {

    private lateinit var changeBinding: FragmentChangeBinding
    private lateinit var categoriesAdapter: CategoriesTitleAdapter
    private var listCategoriesSelected: MutableList<Category> = arrayListOf()
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

        categoriesAdapter = CategoriesTitleAdapter( onItemClicked = {onCategoryItemClicked(it)})
        changeBinding.categoriesTitleRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChangeFragment.context, RecyclerView.HORIZONTAL, false)
            adapter = categoriesAdapter
            setHasFixedSize(false)
        }

        loadFromFB()

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

    private fun loadFromFB() {
        val db = Firebase.firestore
        db.collection("categories").get().addOnSuccessListener { result ->
            val listCategories : MutableList<Category> = arrayListOf()
            for (document in result){
                val c : Category = document.toObject()
                c.let { listCategories.add(it) }
            }
            categoriesAdapter.appendItems(listCategories)
        }
    }

    private fun onCategoryItemClicked(category: Category) {
        listCategoriesSelected.add(category)
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
                    db.collection("products").document(id_product).set(product)
                    db.collection("users").document(id_user_prueba).collection("products").document(id_product).set(product)
                    Toast.makeText(requireContext(), "Producto creado", Toast.LENGTH_SHORT).show()
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
            addProductImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_camera))
        }
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(intent)
    }
}