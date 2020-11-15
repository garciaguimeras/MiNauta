package dev.blackcat.minauta.ui.account

import android.text.Editable
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import dev.blackcat.minauta.R
import dev.blackcat.minauta.data.AccountType
import dev.blackcat.minauta.data.StoredAccount
import dev.blackcat.minauta.ui.MyAppCompatActivity

class AccountDialog(val activity: MyAppCompatActivity) {

    val usernameTextEdit: TextInputEditText
    val passwordTextEdit: TextInputEditText
    val accountTypeSpinner: Spinner
    val dialog: AlertDialog
    val acceptText = activity.getString(R.string.button_accept)

    init {
        val view = activity.layoutInflater.inflate(R.layout.dialog_account, null, false)

        usernameTextEdit = view.findViewById(R.id.usernameTextEdit)
        passwordTextEdit = view.findViewById(R.id.passwordTextEdit)
        accountTypeSpinner = view.findViewById(R.id.accountTypeSpinner)

        dialog = AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(true)
                .create()
    }

    private fun fillInitialValue(initialValue: StoredAccount) {
        val selected = when (initialValue.accountType) {
            AccountType.INTERNATIONAL -> activity.getString(R.string.international_account_text)
            AccountType.NATIONAL -> activity.getString(R.string.national_account_text)
        }
        val idx = activity.resources.getStringArray(R.array.account_type_texts).indexOfFirst { str -> str == selected }

        usernameTextEdit.text = Editable.Factory.getInstance().newEditable(initialValue.username)
        passwordTextEdit.text = Editable.Factory.getInstance().newEditable(initialValue.password)
        accountTypeSpinner.setSelection(idx)
    }

    private fun show(initialValue: StoredAccount?, onChangeMethod: (account: StoredAccount) -> Unit) {
        if (initialValue != null) fillInitialValue(initialValue)
        else fillInitialValue(StoredAccount())

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, acceptText) { dialogInterface, i ->
            val username = usernameTextEdit.text.toString()
            val password = passwordTextEdit.text.toString()
            val spinnerValue = accountTypeSpinner.selectedItem as String
            val value = activity.getString(R.string.international_account_text)
            val type = if (spinnerValue == value) AccountType.INTERNATIONAL else AccountType.NATIONAL

            onChangeMethod(StoredAccount(username, password, type, false))
        }
        dialog.show()
    }

    fun createAccount(onChangeMethod: (account: StoredAccount) -> Unit) {
        show(null, onChangeMethod)
    }

    fun editAccount(initialValue: StoredAccount, onChangeMethod: (account: StoredAccount) -> Unit) {
        show(initialValue, onChangeMethod)
    }

    fun removeAccount(onRemoveMethod: () -> Unit) {
        val dialog = AlertDialog.Builder(activity).create()
        dialog.setMessage(activity.getString(R.string.remove_account_warning))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, acceptText) { dialogInterface, i ->
            onRemoveMethod()
        }
        dialog.show()
    }

}