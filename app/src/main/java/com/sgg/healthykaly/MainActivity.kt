package com.sgg.healthykaly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.sgg.healthykaly.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navHost.navController)

        val appBarConfig = AppBarConfiguration(navHost.navController.graph)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.primaryTextColor))
        binding.toolbar.setupWithNavController(navHost.navController, appBarConfig)
    }
}