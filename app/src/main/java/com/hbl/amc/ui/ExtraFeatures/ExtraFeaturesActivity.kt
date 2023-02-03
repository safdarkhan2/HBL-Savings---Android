package com.hbl.amc.ui.ExtraFeatures

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.hbl.amc.R
import com.hbl.amc.databinding.ActivityExtraFeaturesBinding
import com.hbl.amc.utils.DialogUtils

class ExtraFeaturesActivity : AppCompatActivity() {
    lateinit var binding: ActivityExtraFeaturesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExtraFeaturesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.extra_features_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        DialogUtils.createPogressDialog(context = this)

        if (intent.hasExtra("guides")) {
            navController.navigate(R.id.guidesFragment)
        }

        if (intent.hasExtra("contact")) {
            navController.navigate(R.id.contactFragment)
        }

        if (intent.hasExtra("contactUs")) {
            navController.navigate(R.id.contactUsFragment)
        }

        if (intent.hasExtra("webview")) {
            navController.navigate(R.id.testFragment)
        }
    }
}