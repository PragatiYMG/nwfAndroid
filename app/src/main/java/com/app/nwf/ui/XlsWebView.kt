package com.app.nwf.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.nwf.databinding.ActivityXlsWebViewBinding
import com.app.nwf.utils.GlobalProgressBarUtil

class XlsWebView : AppCompatActivity() {

    private lateinit var binding: ActivityXlsWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityXlsWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    private fun initView() {
        GlobalProgressBarUtil.show(this@XlsWebView)

        binding.xslTitle.text = intent.getStringExtra("title")
        binding.xslBack.setOnClickListener {
            super.onBackPressedDispatcher.onBackPressed()
        }

        binding.xlsWebView?.settings?.javaScriptEnabled = true
        binding.xlsWebView.settings.domStorageEnabled = true
        binding.xlsWebView.settings.databaseEnabled = true
        //  webView.getSettings().setDatabasePath(dbpath); //check the documentation for info about dbpath
        binding.xlsWebView.settings.minimumFontSize = 1
        binding.xlsWebView.settings.minimumLogicalFontSize = 1
        binding.xlsWebView.webViewClient = CustomWebViewClient()
        binding.xlsWebView.settings.setSupportMultipleWindows(true)
        binding.xlsWebView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.xlsWebView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(
                view: WebView,
                url: String,
                message: String,
                result: JsResult
            ): Boolean {
                if (url.startsWith("mailto:")) {
                    startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(url)))
                    return true
                } else if (url.startsWith("tel:")) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                    return true
                }
                return super.onJsAlert(view, url, message, result)
            }

        }

        binding.xlsWebView.loadUrl(intent.getStringExtra("url")!!)

        Handler(Looper.getMainLooper()).postDelayed({
            GlobalProgressBarUtil.hide()
        }, 3000)

    }

    inner class CustomWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (url!!.startsWith("http:") || url.startsWith("https:")) {
                view?.loadUrl(url)
                return true
            }
            return false;
        }

    }

}
