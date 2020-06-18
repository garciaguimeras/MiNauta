package dev.blackcat.minauta.ui.portal

import android.graphics.Bitmap
import android.webkit.JavascriptInterface
import com.fasterxml.jackson.databind.ObjectMapper
import java.nio.ByteBuffer

data class CaptchaBitmap (
        var width: Int,
        var height: Int,
        var bitmap: Map<Int, Byte>
) {
    constructor() : this(0, 0, mapOf<Int, Byte>())
}

class PortalJavaScriptInterface(val viewModel: PortalViewModel) {

    @JavascriptInterface
    fun captcha(jsonObj: String) {
        val captchaBitmap = ObjectMapper().readValue(jsonObj, CaptchaBitmap::class.java)

        val bitmap = Bitmap.createBitmap(captchaBitmap.width, captchaBitmap.height, Bitmap.Config.ARGB_8888)
        val bytes = captchaBitmap.bitmap.values.toByteArray()
        val buffer = ByteBuffer.wrap(bytes)
        bitmap.copyPixelsFromBuffer(buffer)
        viewModel.shopCaptchaDialog(bitmap)
    }

}
