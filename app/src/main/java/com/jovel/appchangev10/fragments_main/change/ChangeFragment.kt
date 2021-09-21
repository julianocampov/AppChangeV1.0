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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.FragmentChangeBinding
import com.jovel.appchangev10.fragments_main.home.CategoriesTitleAdapter
import com.jovel.appchangev10.model.Category
import com.jovel.appchangev10.model.Product
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class ChangeFragment : Fragment() {

    private lateinit var changeBinding: FragmentChangeBinding
    private lateinit var categoriesAdapter: CategoriesTitleAdapter
    private var listCategoriesSelected: MutableList<String> = arrayListOf()
    private val args: ChangeFragmentArgs by navArgs()
    private lateinit var auth: FirebaseAuth
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var ubication: String
    private lateinit var state: String
    private var numberPictures = 0
    private var imageCreated = false

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                changeBinding.addProductImageView.setImageBitmap(imageBitmap)
                imageCreated = true
            }

        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        changeBinding = FragmentChangeBinding.inflate(inflater, container, false)

        val product = args.product

        if (product != null){
            with(changeBinding){
                titleProductEditText.setText(product.title)
                descriptionProductEditText.setText(product.description)
                ubicationProductEditText.setText(product.ubication)

                val arraySpinner = resources.getStringArray(R.array.state_list)
                stateSpinner.setSelection(arraySpinner.binarySearch(product.state))

                listCategoriesSelected = product.categories!!

                Picasso.get().load(product.urlImage).into(addProductImageView)
                addProductButton.text = getString(R.string.update)

                imageCreated = true
            }
        } else {

            changeBinding.addProductButton.setOnClickListener {
                with(changeBinding) {
                    title = titleProductEditText.text.toString()
                    description = descriptionProductEditText.text.toString()
                    ubication = ubicationProductEditText.text.toString()
                    state = stateSpinner.selectedItem.toString()
                }
                if (notEmptyFieldsChange(title, description, ubication, state) && listCategoriesSelected.isNotEmpty() && imageCreated) {
                    saveProduct()
                }
                else if((!notEmptyFieldsChange(title, description, ubication, state) || listCategoriesSelected.isEmpty())){
                    Toast.makeText(requireContext(), R.string.missing_data, Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(requireContext(), R.string.missing_picture, Toast.LENGTH_SHORT).show()
                }
            }
        }

        categoriesAdapter =
            CategoriesTitleAdapter(onItemClicked = { onCategoryItemClicked(it) }) //TODO guardar categorias del producto
        changeBinding.categoriesTitleRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@ChangeFragment.context, RecyclerView.HORIZONTAL, false)
            adapter = categoriesAdapter
            setHasFixedSize(false)
        }

        loadFromFB()

        with(changeBinding) {
            addPicturesButton.setOnClickListener {
                dispatchTakePictureIntent()
            }

            toolbar3.setNavigationOnClickListener {
                cleanViews()
                imageCreated = false
                listCategoriesSelected.clear()
                Toast.makeText(requireContext(), "Campos limpiados", Toast.LENGTH_SHORT).show()
            }
        }

        return changeBinding.root
    }

    private fun loadFromFB() {
        val db = Firebase.firestore
        db.collection("categories").get().addOnSuccessListener { result ->
            val listCategories: MutableList<Category> = arrayListOf()
            for (document in result) {
                val c: Category = document.toObject()
                c.let { listCategories.add(it) }
            }
            categoriesAdapter.appendItems(listCategories)
        }
    }

    private fun onCategoryItemClicked(category: Category) {
        if (!listCategoriesSelected.contains(category.name))
            category.name?.let { listCategoriesSelected.add(it) }
    }

    private fun saveProduct() {

        val db = Firebase.firestore
        auth = Firebase.auth
        val id_user = auth.currentUser?.uid
        val document_product_in_user = id_user?.let { db.collection("users").document(it).collection("products").document() }
        val id_product = document_product_in_user?.id

        val storageRef = FirebaseStorage.getInstance()
        val pictureRef = id_product?.let { storageRef.reference.child("products").child(it) }
        changeBinding.addProductButton.isDrawingCacheEnabled = true
        changeBinding.addProductButton.buildDrawingCache()
        val bitmap = (changeBinding.addProductImageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = pictureRef?.putBytes(data)    //Sube la información

        uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            pictureRef.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val urlPicture = task.result.toString()
                val product = Product(id = id_product, idOwner = id_user, ubication = ubication, urlImage = urlPicture, title = title, description = description, state = state, categories = listCategoriesSelected)
                id_product.let { db.collection("products").document(it).set(product) }
                id_user.let {
                    db.collection("users").document(it).collection("products").document(id_product)
                        .set(product)
                }
                Toast.makeText(requireContext(), "Producto creado", Toast.LENGTH_SHORT).show()
                cleanViews()
            }
        }
    }

    private fun notEmptyFieldsChange(title: String, description: String, ubication: String, state: String): Boolean {
        if (!(title.isNotEmpty() && description.isNotEmpty() && ubication.isNotEmpty() && state.isNotEmpty()))
            Toast.makeText(activity, "Algún campo está vacío", Toast.LENGTH_SHORT).show()
        return title.isNotEmpty() && description.isNotEmpty() && ubication.isNotEmpty() && state.isNotEmpty()
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