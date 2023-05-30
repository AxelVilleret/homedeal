package com.example.homedeal

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.homedeal.databinding.ActivityMainBinding
import com.example.homedeal.utils.ThemeUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        //ThemeUtils.applyTheme(this); // Appliquer le thème avant setContentView()
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_newdeal, R.id.navigation_notifications, R.id.navigation_profile
            )
        )
        navView.setupWithNavController(navController)

        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.popBackStack()
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_newdeal -> {
                    navController.popBackStack()
                    navController.navigate(R.id.navigation_newdeal)
                    true
                }
                R.id.navigation_notifications -> {
                    navController.popBackStack()
                    navController.navigate(R.id.navigation_notifications)
                    true
                }
                R.id.navigation_profile -> {
                    navController.popBackStack()
                    navController.navigate(R.id.navigation_profile)
                    true
                }
                R.id.navigation_saveddeals -> {
                    navController.popBackStack()
                    navController.navigate(R.id.navigation_saveddeals)
                    true
                }
                R.id.navigation_details -> {
                    navController.popBackStack()
                    navController.navigate(R.id.navigation_details)
                    true
                }
                R.id.navigation_update_profile -> {
                    navController.popBackStack()
                    navController.navigate(R.id.navigation_update_profile)
                    true
                }
                else -> false
            }
        }
    }
}