package dev.blackcat.minauta.ui.portal.page

import dev.blackcat.minauta.ui.portal.PortalViewModel

class ErrorPage(viewModel: PortalViewModel) : Page(viewModel) {

    fun loadErrorPage() {
        loadUrl("file:///android_asset/html/error.html")
    }

}