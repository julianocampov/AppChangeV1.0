package com.jovel.appchangev10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.concurrent.timerTask


private lateinit var auth: FirebaseAuth

class SplashActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)

        auth = Firebase.auth
        val id = auth.currentUser?.uid


        val timer = Timer()
        timer.schedule(
            timerTask {
                if (id != null) {
                    sendDataToMain(id)
                } else {
                    goToLoginActivity()
                }
            }, 800
        )
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun sendDataToMain(uid: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("id", uid)
        startActivity(intent)
        finish()
    }
}