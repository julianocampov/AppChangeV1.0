package com.jovel.appchangev10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.databinding.ActivityLoginBinding
import com.jovel.appchangev10.utils.notEmptyFields

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(loginBinding.root)

        auth = Firebase.auth
        val id = auth.currentUser?.uid

        if (id != null) {
            sendDataToMain(id)
        }

        saveData()
        buttonsListeners()
    }

    private fun saveData() {
        val data = intent.extras
        if (data?.getString("correo").toString() != "null"){
            loginBinding.emailEditText.setText(data?.getString("correo").toString())
            loginBinding.passwordEditText.setText(data?.getString("contraseña").toString())
        }
    }

    private fun buttonsListeners() {
        loginBinding.loginButton.setOnClickListener{

            signIn()
        }

        loginBinding.registerTextView.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun sendDataToMain(uid: String) {
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("id", uid)
        startActivity(intent)
        finish()
    }

    private fun signIn() {

        val email = loginBinding.emailEditText.text.toString()
        val password = loginBinding.passwordEditText.text.toString()

        if(notEmptyFields(email, password," "," ", this)) {

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Inicio de sesion correcto", Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        if (user != null) {
                            sendDataToMain(user.uid)
                        }
                    } else
                        Toast.makeText(
                            this, getString(R.string.warning_login_incorrect_field),
                            Toast.LENGTH_SHORT
                        ).show()

                }
        }
    }
}