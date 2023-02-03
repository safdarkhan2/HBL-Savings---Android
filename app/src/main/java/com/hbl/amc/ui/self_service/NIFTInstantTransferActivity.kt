package com.hbl.amc.ui.self_service

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hbl.amc.BuildConfig
import com.hbl.amc.databinding.ActivityNiftinstantTransferBinding
import com.hbl.amc.databinding.ActivitySplashBinding
import android.webkit.WebViewClient

import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.Toast
import androidx.webkit.WebViewCompat
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.AMOUNT
import com.hbl.amc.utils.DialogUtils

import com.hbl.amc.ui.PAYMENT
import com.hbl.amc.ui.SCANNED_IMAGE
import com.hbl.amc.ui.STEP_ID


class NIFTInstantTransferActivity : AppCompatActivity() {
    lateinit var binding: ActivityNiftinstantTransferBinding

    var activity = this
    var amount : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNiftinstantTransferBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//        DialogUtils.createPogressDialog(context = this)

        initViews()
    }

    fun initViews() {

        val webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(this)
        Log.d("MY_APP_TAG", "WebView version: ${webViewPackageInfo?.versionName}")

        binding.webview.webChromeClient = MyWebChromeClient()
//        binding.webview.webViewClient = WebViewClient()
        binding.webview.settings.loadWithOverviewMode = true
        binding.webview.settings.setSupportZoom(true)
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.canGoForward()

        intent?.let {
            if (it.hasExtra(AMOUNT)) {
                amount = it.getStringExtra(AMOUNT)
                val cnic = HBLPreferenceManager.getCustomerCNIC(this).replace("-", "")
                binding.webview.loadUrl(
                    "${BuildConfig.BASE_URL_NIFT_PAYMENT}payment/instant/abfFGHIepqrstuvwSTUCDEXYZ/$cnic/$amount"
                )
            }
        }
    }

    inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (newProgress < 100) {
                DialogUtils.showLoading()
            } else {
                DialogUtils.hideLoading()

                view?.let { wbv ->
                    if (wbv.url!!.contains("success")) {
                       /* HBLPreferenceManager.savePaymentSuccess(this@NIFTInstantTransferActivity,
                            wbv.url!!
                        )*/
                        val resultIntent = Intent()
                        resultIntent.putExtra(PAYMENT, wbv.url)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                }

            }
        }

        /*override fun onHideCustomView() {
            super.onHideCustomView()
            DialogUtils.hideLoading()
        }*/
    }

    // Overriding WebViewClient functions
    /*inner class WebViewClient : android.webkit.WebViewClient() {

        // Load the URL
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return false
        }

        // ProgressBar will disappear once page is loaded
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            DialogUtils.hideLoading()
        }
    }*/

    override fun onBackPressed() {
        if (binding.webview.canGoBack()) {
            binding.webview.goBack()
            DialogUtils.hideLoading()
        } else {
            super.onBackPressed()
        }
    }
}