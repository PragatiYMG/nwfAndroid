package com.app.nwf.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.*
import com.app.nwf.databinding.ActivityPdfWebViewBinding
import com.app.nwf.utils.GlobalProgressBarUtil

class PdfWebView : AppCompatActivity() {

    private lateinit var binding: ActivityPdfWebViewBinding
    private var isFirst: Boolean = true;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {

        GlobalProgressBarUtil.show(this@PdfWebView)

        binding.pdfTitle.text = intent.getStringExtra("title")
        binding.pdfBack.setOnClickListener {
            super.onBackPressedDispatcher.onBackPressed()
        }

        GlobalProgressBarUtil.show(this@PdfWebView)

        val mUrl = intent.getStringExtra("url")

        binding.pdfWebView?.settings?.javaScriptEnabled = true
        binding.pdfWebView.settings.setSupportMultipleWindows(true)
        binding.pdfWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        binding.pdfWebView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.pdfWebView.webViewClient = CustomWebViewClient()

        binding.pdfWebView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$mUrl")

        Handler(Looper.getMainLooper()).postDelayed({
            GlobalProgressBarUtil.hide()
        }, 3000)

    }

    inner class CustomWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

            return false;
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            if(view?.contentHeight == 0){
                view.reload()
            }
        }
    }

}
