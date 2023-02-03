package com.hbl.amc.ui.productInformation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.hbl.amc.R
import com.hbl.amc.databinding.ActivityProductSelectionBinding
import com.hbl.amc.ui.STEP_ID
import com.hbl.amc.utils.DialogUtils

class ProductSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DialogUtils.createPogressDialog(context = this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.product_info_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        intent?.also {
            if (it.hasExtra(STEP_ID)) {
                val st = it.getIntExtra(STEP_ID, 0)
                st?.let { step ->
                    when (step) {
                        9 -> {
                            navController.navigate(R.id.mutualFundsSelectionFragment)
                        }
                        10 -> {
                            navController.navigate(R.id.vpsFundsSelectionFragment)
                        }
                        11 -> {
                            navController.navigate(R.id.productOtherInfoFragment)
                        }
                    }
                }
            }
        }
    }
}