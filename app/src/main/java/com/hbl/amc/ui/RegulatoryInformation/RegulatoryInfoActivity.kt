package com.hbl.amc.ui.RegulatoryInformation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.ActivityMainBinding
import com.hbl.amc.databinding.ActivityRegulatoryInfoBinding
import com.hbl.amc.ui.STEP_ID
import com.hbl.amc.ui.TO_RPQ
import com.hbl.amc.utils.DialogUtils

class RegulatoryInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegulatoryInfoBinding
    var toRPQ = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegulatoryInfoBinding.inflate(layoutInflater)
//        val view = binding.root
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.regulatory_info_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        DialogUtils.createPogressDialog(context = this)

        intent?.also {
            if (it.hasExtra(STEP_ID)) {
                val st = it.getIntExtra(STEP_ID, 0)
                st?.let { step ->
                    when (step) {
                        5 -> {
                            navController.navigate(R.id.riskProfileQuestionnaireFragment)
                        }
                        6 -> {
                            navController.navigate(R.id.fatcaChecklistFragment)
                        }
                        7 -> {
                            navController.navigate(R.id.crsFragment)
                        }
                    }
                }
            } else if (it.hasExtra(TO_RPQ)) {
                toRPQ = it.getBooleanExtra(TO_RPQ, false)
                navController.navigate(R.id.riskProfileQuestionnaireFragment)
            }
        }
    }
}