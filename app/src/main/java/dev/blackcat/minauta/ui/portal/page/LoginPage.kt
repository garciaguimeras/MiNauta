package dev.blackcat.minauta.ui.portal.page

import dev.blackcat.minauta.util.PreferencesStore
import dev.blackcat.minauta.ui.portal.PortalViewModel

class LoginPage(viewModel: PortalViewModel) : Page(viewModel) {

    fun loadCaptchaScript() {
        val captchaJs = loadJsFile("captcha.js")
        val code = "javascript:(($captchaJs)())"
        loadUrl(code)
    }

    fun loadLoginScript(captcha: String) {
        val preferencesStore = PreferencesStore(viewModel.getApplication())
        val account = preferencesStore.account
        val loginJs = loadJsFile("login.js")
        val code = "javascript:(($loginJs)('${account.username}', '${account.password}', '$captcha'))"
        loadUrl(code)
    }

}