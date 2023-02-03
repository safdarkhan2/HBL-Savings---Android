package com.hbl.amc.ui.VideoCall

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.NavHostFragment
import com.hbl.amc.R
import com.hbl.amc.databinding.ActivitySelfServiceBinding
import com.hbl.amc.databinding.ActivityVideoCallBinding
import com.hbl.amc.utils.DialogUtils

class VideoCallActivity : AppCompatActivity() {
    lateinit var binding: ActivityVideoCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.video_call_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        DialogUtils.createPogressDialog(context = this)
    }
}