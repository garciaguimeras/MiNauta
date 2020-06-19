package dev.blackcat.minauta.ui.portal.page

import dev.blackcat.minauta.ui.portal.PortalViewModel

open class Page(val viewModel: PortalViewModel) {

    fun loadJsFile(filename: String): String {
        val reader = viewModel.activity.assets.open("js/$filename")
                .bufferedReader()
                .lineSequence()
                .iterator()
        var code = ""
        while (reader.hasNext()) code = "$code${reader.next()}"
        return code
    }

    fun loadUrl(url: String) {
        viewModel.webView.loadUrl(url)
    }

}