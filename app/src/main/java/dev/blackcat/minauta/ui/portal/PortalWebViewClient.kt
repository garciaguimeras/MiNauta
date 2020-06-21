package dev.blackcat.minauta.ui.portal

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.webkit.*

class PortalWebViewClient(val viewModel: PortalViewModel) : WebViewClient() {

    val pageResult = HashMap<String, Boolean>()

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        Log.i(PortalWebViewClient::class.java.name, url!!)
        pageResult.put(url, true)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        val result = pageResult.get(url!!) ?: true
        if (!result) return
        viewModel.onPageLoaded(url)
    }

    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
        pageResult.put(failingUrl!!, false)
        viewModel.errorPage.loadErrorPage()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        pageResult.put(request!!.url!!.toString(), false)
        viewModel.errorPage.loadErrorPage()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
        pageResult.put(request!!.url!!.toString(), false)
        viewModel.errorPage.loadErrorPage()
    }

}