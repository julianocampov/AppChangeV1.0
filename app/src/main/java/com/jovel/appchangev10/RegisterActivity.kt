package com.jovel.appchangev10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.databinding.ActivityRegisterBinding
import com.jovel.appchangev10.model.User
import com.jovel.appchangev10.utils.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerBinding: ActivityRegisterBinding
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var reppassword: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(registerBinding.root)

        auth = Firebase.auth

        buttonListener()
        onChangeListener()
    }

    private fun buttonListener() {
        registerBinding.checkinButton.setOnClickListener {

            readTextInputs()

            if (notEmptyFields(name, email, password, reppassword, this)) {
                if(validateName()){
                    if (password != reppassword){
                        registerBinding.repPasswordTextInputLayout.error = getString(R.string.password_not_match)
                    } else{
                        createAuthUser(email, password)
                    }
                }
            }
        }
    }

    private fun createAuthUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    createStorageUser(email, name)
                    sendDataToLogin()
                    //TODO ir al login
                } else {
                    when (task.exception?.localizedMessage) {
                        "The email address is badly formatted." ->
                            registerBinding.emailTextInputLayout.error = getString(R.string.enter_valid_email)
                        "The given password is invalid. [ Password should be at least 6 characters ]" ->
                            registerBinding.passwordTextInputLayout.error = getString(R.string.password_length)
                        "The email address is already in use by another account." ->
                            registerBinding.emailTextInputLayout.error = getString(R.string.email_used)
                    }
                }
            }
    }

    private fun createStorageUser(email: String, name: String) {
        val id = auth.currentUser?.uid
        id?.let { id ->
            val user = User(
                id = id,
                name = name,
                email = email,
                qualification = 0.0,
                changes = 0,
                phone = null,
                urlProfileImage = null,
                address = null,
                city = null,
                ProviderType.BASIC)
            val db = Firebase.firestore

            db.collection("users").document(id)
                    .set(user)
                    .addOnSuccessListener { documentReference ->   //Creacion exitosa
                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->   //Creacion fallida
                        Toast.makeText(this, "No se pudo registrar", Toast.LENGTH_SHORT).show()
                    }
        }
    }

    private fun onChangeListener() {
        with(registerBinding){

            nameEditText.doAfterTextChanged {
                nameTextInputLayout.error = null
            }

            emailEditText.doAfterTextChanged {
                emailTextInputLayout.error = null
            }

            passwordEditText.doAfterTextChanged {
                passwordTextInputLayout.error = null
            }

            repPasswordEditText.doAfterTextChanged {
                repPasswordTextInputLayout.error = null
            }
        }
    }

    private fun readTextInputs() {
        with(registerBinding) {
            name = nameEditText.text.toString()
            email = emailEditText.text.toString()
            password = passwordEditText.text.toString()
            reppassword = repPasswordEditText.text.toString()
            passwordTextInputLayout.error = null
            repPasswordTextInputLayout.error = null
        }
    }

    private fun validateName(): Boolean {
        if(lengthString(name, USER_NAME_LENGTH)) return true
        registerBinding.nameTextInputLayout.error = getString(R.string.enter_valid_name)
        return lengthString(name, USER_NAME_LENGTH)
    }

    private fun sendDataToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("correo", email)
        intent.putExtra("contraseña", password)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}