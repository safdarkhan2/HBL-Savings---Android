package com.hbl.amc.ui.CustomerInformation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.hbl.amc.R
import com.hbl.amc.databinding.ActivityCustomerInformationBinding
import com.hbl.amc.databinding.ActivityMainBinding
import com.hbl.amc.utils.AppUtils
import com.hbl.amc.utils.DialogUtils

class CustomerInformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerInformationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        DialogUtils.createPogressDialog(context = this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.customer_info_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }

    /*override fun onResume() {
        super.onResume()
        if (!AppUtils.allPermissionsGranted(this)) {
            AppUtils.requestRuntimePermissions(this)
        }
    }*/
}