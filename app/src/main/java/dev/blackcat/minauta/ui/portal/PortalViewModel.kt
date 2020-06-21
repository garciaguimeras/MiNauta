package dev.blackcat.minauta.ui.portal

import android.graphics.Bitmap
import android.webkit.WebView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.blackcat.minauta.ui.portal.page.ErrorPage
import dev.blackcat.minauta.ui.portal.page.LoginPage

class PortalViewModelFactory(val activity: PortalActivity) : ViewModelProvider.AndroidViewModelFactory(activity.application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PortalViewModel(activity) as T
    }
}

class PortalViewModel(
        val activity: PortalActivity) : AndroidViewModel(activity.application) {

    companion object {
        val PORTAL_URL = "https://www.portal.nauta.cu"
        val LOGIN_URL = "https://www.portal.nauta.cu/user/login/es-es"
    }

    val captchaBitmap: MutableLiveData<Bitmap> = MutableLiveData()
    val urlToLoad: MutableLiveData<String> = MutableLiveData()

    val loginPage = LoginPage(this)
    val errorPage = ErrorPage(this)

    fun onPageLoaded(url: String) {
        if (url.equals(LOGIN_URL)) {
            loginPage.loadCaptchaScript()
        }
    }

}