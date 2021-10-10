package com.jovel.appchangev10

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController

enum class ProviderType {
    BASIC
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val lis: List<Int> = listOf(
            R.id.chatFragment,
            R.id.productFragment,
            R.id.changePasswordFragment,
            R.id.updateInfoFragment,
            R.id.myProductsFragment,
            R.id.navigation_change
        )

        val navController = findNavController(R.id.nav_host_fragment)
        /*val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_favorites,
                R.id.navigation_change,
                R.id.navigation_messages,
                R.id.navigation_profile
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)*/
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (lis.contains(destination.id)) {
                navView.visibility = View.GONE
            } else
                navView.visibility = View.VISIBLE
        }
    }
}