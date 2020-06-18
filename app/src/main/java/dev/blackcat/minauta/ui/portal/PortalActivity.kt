package dev.blackcat.minauta.ui.portal

import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.lifecycle.ViewModelProvider
import dev.blackcat.minauta.R
import dev.blackcat.minauta.ui.MyAppCompatActivity

class PortalActivity : MyAppCompatActivity() {

    private lateinit var viewModel: PortalViewModel
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portal)

        webView = findViewById(R.id.webView)

        val viewModelFactory = PortalViewModelFactory(this, webView)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PortalViewModel::class.java)

        configureWebView()
        viewModel.loadPortalPage()
    }

    private fun configureWebView() {
        webView.getSettings().setJavaScriptEnabled(true)
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false)
        webView.getSettings().setDomStorageEnabled(true)
        // webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)
        // webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true)

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
        webView.setHorizontalScrollBarEnabled(false)
        // webView.setWebViewClient(CustomWebViewClient(this))
        webView.addJavascriptInterface(PortalJavaScriptInterface(viewModel), "portal")

        CookieManager.getInstance().setAcceptCookie(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        }

        webView.webViewClient = PortalWebViewClient(viewModel)
    }
}
