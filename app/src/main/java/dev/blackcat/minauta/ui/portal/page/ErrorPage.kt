package dev.blackcat.minauta.ui.portal.page

import dev.blackcat.minauta.ui.portal.PortalViewModel

class ErrorPage(viewModel: PortalViewModel) : Page(viewModel) {

    fun loadErrorPage() {
        val errorJs = loadJsFile("error.js")
        val code = "javascript:(($errorJs)())"
        loadUrl(code)
    }

}