package dev.blackcat.minauta.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import dev.blackcat.minauta.R

open class MyAppCompatActivity : AppCompatActivity() {

    fun createDialogWithText(resId: Int): AlertDialog {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setMessage(this.getString(resId))
        return dialog
    }

    fun showDialogWithText(resId: Int): AlertDialog {
        val dialog = createDialogWithText(resId)
        dialog.show()
        return dialog
    }

    fun showOneButtonDialogWithText(resId: Int): AlertDialog {
        val acceptText = getString(R.string.button_accept)

        val dialog = createDialogWithText(resId)
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, acceptText) { dialogInterface, i ->
            dialogInterface.dismiss()
        }
        dialog.show()

        return dialog
    }

    fun showAboutDialog() {
        val appName = getString(R.string.app_name)

        val info = packageManager.getPackageInfo(packageName, 0)
        val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) info.longVersionCode
                   else info.versionCode
        val appVersion = "${info.versionName}.${code}"

        val view = layoutInflater.inflate(R.layout.dialog_about, null, false)

        val appNameTextView: TextView = view.findViewById(R.id.appNameTextView)
        appNameTextView.text = "${appName} v${appVersion}"

        val jNautaLink: TextView = view.findViewById(R.id.jNautaLink)
        jNautaLink.setOnClickListener { v ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/garciaguimeras/jnauta"))
            startActivity(intent)
        }

        val miNautaLink: TextView = view.findViewById(R.id.miNautaLink)
        miNautaLink.setOnClickListener { v ->
            Toast.makeText(this, getString(R.string.soon_on_github_text), Toast.LENGTH_LONG).show()
        }

        AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .create()
                .show()
    }

}
