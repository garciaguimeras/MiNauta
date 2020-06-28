package dev.blackcat.minauta.ui.account

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dev.blackcat.minauta.R
import dev.blackcat.minauta.data.Account

import dev.blackcat.minauta.ui.MyAppCompatActivity

class AccountActivity : MyAppCompatActivity() {

    lateinit var viewModel: AccountViewModel

    lateinit var usernameEdit: EditText
    lateinit var passwordEdit: EditText
    lateinit var acceptButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application).create(AccountViewModel::class.java)

        usernameEdit = findViewById(R.id.usernameEdit)

        passwordEdit = findViewById(R.id.passwordEdit)

        acceptButton = findViewById(R.id.acceptButton)
        acceptButton.setOnClickListener {
            var username = usernameEdit.text.toString()
            val password = passwordEdit.text.toString()
            viewModel.setAccountInfo(this, username, password)
        }

        viewModel.account.observe(this, object : Observer<Account> {
            override fun onChanged(account: Account) {
                usernameEdit.setText(account.username)
                passwordEdit.setText(account.password)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkAccount()
    }

}
