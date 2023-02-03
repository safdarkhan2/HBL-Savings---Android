package com.hbl.amc.ui.Preview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.hbl.amc.R
import com.hbl.amc.databinding.ActivityPreviewBinding
import com.hbl.amc.utils.DialogUtils

class PreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DialogUtils.createPogressDialog(context = this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.preview_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if(intent.hasExtra("FROM_DISCLAIMER"))
        {
            try {
                navController.navigate(R.id.action_previewFragment_to_thankYouFragment)
            } catch (ex : Exception) {
                ex.printStackTrace()
            }
        }
    }

}