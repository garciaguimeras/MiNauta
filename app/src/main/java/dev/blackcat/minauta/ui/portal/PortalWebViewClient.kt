package dev.blackcat.minauta.ui.portal

import android.webkit.WebView
import android.webkit.WebViewClient

class PortalWebViewClient(val viewModel: PortalViewModel) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        if (url == null || view == null) return
        viewModel.onPageLoaded(url)
    }

}