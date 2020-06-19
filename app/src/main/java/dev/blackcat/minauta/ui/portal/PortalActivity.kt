package dev.blackcat.minauta.ui.portal

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dev.blackcat.minauta.R
import dev.blackcat.minauta.ui.MyAppCompatActivity

class PortalActivity : MyAppCompatActivity() {

    private lateinit var viewModel: PortalViewModel
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portal)
        supportActionBar!!.hide()

        webView = findViewById(R.id.webView)

        val viewModelFactory = PortalViewModelFactory(this, webView)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PortalViewModel::class.java)
        viewModel.captchaBitmap.observe(this, Observer<Bitmap> { bitmap ->
            showCaptchaDialog(bitmap)
        })

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

    fun showCaptchaDialog(bitmap: Bitmap) {
        val view = layoutInflater.inflate(R.layout.dialog_captcha, null, false)
        val captchaText: EditText = view.findViewById(R.id.captchaText)
        val captchaImage: ImageView = view.findViewById(R.id.captchaImage)
        captchaImage.setImageBitmap(bitmap)

        AlertDialog.Builder(this)
                .setTitle(R.string.captcha_title)
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.button_continue) { dialog, which ->
                    viewModel.loginPage.loadLoginScript(captchaText.text.toString())
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.button_cancel) { dialog, which ->
                    finish()
                }
                .create()
                .show()
    }
}
