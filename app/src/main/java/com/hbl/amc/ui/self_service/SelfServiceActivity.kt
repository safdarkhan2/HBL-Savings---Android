package com.hbl.amc.ui.self_service

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.hbl.amc.R
import com.hbl.amc.databinding.ActivitySelfServiceBinding
import com.hbl.amc.utils.DialogUtils

class SelfServiceActivity : AppCompatActivity() {
    lateinit var binding: ActivitySelfServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelfServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.self_service_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        DialogUtils.createPogressDialog(context = this)


    }
}