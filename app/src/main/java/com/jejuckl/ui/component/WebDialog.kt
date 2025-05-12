package com.jejuckl.ui.component

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import com.jejuckl.R

class WebDialog(context: Context, private val url: String) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_webview, null)
        setContentView(view)

        val webView = findViewById<WebView>(R.id.webView)
        val btnClose = findViewById<Button>(R.id.btnClose)

        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)

        btnClose.setOnClickListener { dismiss() }
    }
} 