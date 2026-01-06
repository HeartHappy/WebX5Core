package com.hearthappy.webx5core

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hearthappy.webx5core.databinding.ActivityMainBinding
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebSettings

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()

    }

    private fun initView() {
        viewBinding.apply {
            btnLoadWebView.setOnClickListener {
                Toast.makeText(this@MainActivity, if (webView.getIsX5Core()) "当前是：X5内核: " + QbSdk.getTbsVersion(this@MainActivity) else "当前是：SDK系统内核", Toast.LENGTH_SHORT).show()
                viewBinding.initWebView()
            }
            btnLoadWebView.requestFocus()

        }
    }

    private fun ActivityMainBinding.initWebView() {
        val webSettings: WebSettings = webView.settings
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true // 如果需要加载混合内容（HTTP 和 HTTPS 混合），需要允许
        //        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // 设置字体支持
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true

        // Load HTML file from assets
        webView.loadUrl("https://debugtbs.qq.com")

    }




    override fun onDestroy() {
        viewBinding.webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}