package dev.blackcat.minauta.ui

import android.view.View

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import dev.blackcat.minauta.R

open class MyAppCompatActivity : AppCompatActivity() {

    fun showDialogWithText(resId: Int): AlertDialog {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setMessage(this.getString(resId))
        dialog.show()

        return dialog
    }

    fun showOneButtonDialogWithText(resId: Int): AlertDialog {
        val acceptText = getString(R.string.button_accept)

        val dialog = AlertDialog.Builder(this).create()
        dialog.setMessage(this.getString(resId))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, acceptText) { dialogInterface, i ->
            dialogInterface.dismiss()
        }
        dialog.show()

        return dialog
    }

}
