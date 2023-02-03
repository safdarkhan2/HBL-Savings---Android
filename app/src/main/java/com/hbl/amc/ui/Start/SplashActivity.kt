package com.hbl.amc.ui.Start

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.hbl.amc.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private var mDelayHandler = Looper.myLooper()?.let { Handler(it) }

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /*findNavController().navigate(R.id.action_splashFragment_to_loginFragment)*/

        mDelayHandler?.postDelayed(
            {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            },
            2000
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        mDelayHandler?.removeCallbacksAndMessages(null)
        mDelayHandler = null
    }
}