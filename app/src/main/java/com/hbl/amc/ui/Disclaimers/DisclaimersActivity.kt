package com.hbl.amc.ui.Disclaimers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.hbl.amc.R
import com.hbl.amc.databinding.ActivityDisclaimersBinding
import com.hbl.amc.ui.STEP_ID
import com.hbl.amc.utils.DialogUtils

class DisclaimersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisclaimersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisclaimersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DialogUtils.createPogressDialog(context = this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.disclaimers_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        intent?.also {
            if (it.hasExtra(STEP_ID)) {
                val st = it.getIntExtra(STEP_ID, 0)
                st?.let { step ->
                    when (step) {
                        13 -> {
                            navController.navigate(R.id.disclaimersFragment)
                        }
                    }
                }
            }
        }
    }
}