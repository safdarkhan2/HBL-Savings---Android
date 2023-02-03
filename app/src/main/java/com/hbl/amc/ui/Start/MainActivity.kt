package com.hbl.amc.ui.Start

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.hbl.amc.R
import com.hbl.amc.databinding.ActivityMainBinding
import com.hbl.amc.utils.DialogUtils.Companion.createPogressDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        val view = binding.root
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.start_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        createPogressDialog(context = this)
    }
}