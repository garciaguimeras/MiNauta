package dev.blackcat.minauta.ui.portal

import android.app.Application
import android.content.DialogInterface
import android.graphics.Bitmap
import android.text.Editable
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.blackcat.minauta.R
import dev.blackcat.minauta.data.store.PreferencesStore
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PortalViewModelFactory(
        private val activity: PortalActivity,
        private val webView: WebView) : ViewModelProvider.AndroidViewModelFactory(activity.application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PortalViewModel(activity, webView) as T
    }
}

class PortalViewModel(
        val activity: PortalActivity,
        private val webView: WebView) : AndroidViewModel(activity.application) {

    val portalPage = "https://www.portal.nauta.cu/user/login/es-es"

    val loginJs = """
        function(username, password, captcha) {
            usernameElem = document.getElementById('username');
            passwordElem = document.getElementById('password');
            captchaElem = document.getElementById('captcha');
            button = document.getElementsByName('btn_submit')[0];
            
            usernameElem.value = username;
            passwordElem.value = password;
            captchaElem.value = captcha;
            button.click();
        }
    """.trimIndent()

    val captchaJs = """
        function() {
            img = document.getElementsByClassName('captcha')[0];
            canvas = document.createElement('canvas');
            canvas.width = img.width;
            canvas.height = img.height;
            ctx = canvas.getContext('2d');
            ctx.drawImage(img, 0, 0);
            bmp = ctx.getImageData(0, 0, canvas.width, canvas.height).data;
            
            obj = { 
                bitmap: bmp,
                width: canvas.width,
                height: canvas.height
            };
            
            portal.captcha(JSON.stringify(obj));
        }
    """.trimIndent()

    private fun getCaptchaScript() {
        val code = "javascript:(($captchaJs)())"
        webView.loadUrl(code)
    }

    private fun applyLoginScript(captcha: String) {
        val account = PreferencesStore(getApplication()).account
        val code = "javascript:(($loginJs)('${account.username}', '${account.password}', '$captcha'))"
        webView.loadUrl(code)
    }

    fun loadPortalPage() {
        webView.loadUrl(portalPage)
    }

    fun onPageLoaded(url: String) {
        if (url.equals(portalPage)) {
            getCaptchaScript()
        }
    }

    fun shopCaptchaDialog(bitmap: Bitmap) {
        MainScope().launch {
            val view = activity.layoutInflater.inflate(R.layout.dialog_captcha, null, false)
            val captchaText: EditText = view.findViewById(R.id.captchaText)
            val captchaImage: ImageView = view.findViewById(R.id.captchaImage)
            captchaImage.setImageBitmap(bitmap)

            AlertDialog.Builder(activity)
                    .setTitle(R.string.captcha_title)
                    .setView(view)
                    .setCancelable(false)
                    .setPositiveButton(R.string.button_continue) { dialog, which ->
                        applyLoginScript(captchaText.text.toString())
                        dialog.dismiss()
                    }
                    .create()
                    .show()
        }
    }

}