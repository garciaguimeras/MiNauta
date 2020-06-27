package dev.blackcat.minauta.ui.portal.page

import dev.blackcat.minauta.data.PreferencesStore
import dev.blackcat.minauta.ui.portal.PortalViewModel

class LoginPage(viewModel: PortalViewModel) : Page(viewModel) {

    fun loadCaptchaScript() {
        val captchaJs = loadJsFile("captcha.js")
        val code = "javascript:(($captchaJs)())"
        loadUrl(code)
    }

    fun loadLoginScript(captcha: String) {
        val account = PreferencesStore(viewModel.getApplication()).account
        val loginJs = loadJsFile("login.js")
        val code = "javascript:(($loginJs)('${account.username}', '${account.password}', '$captcha'))"
        loadUrl(code)
    }

}