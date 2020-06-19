package dev.blackcat.minauta.ui.main

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dev.blackcat.minauta.R
import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.AccountState
import dev.blackcat.minauta.ui.MyAppCompatActivity

class MainActivity : MyAppCompatActivity() {

    lateinit var viewModel: MainViewModel

    lateinit var portalButton: Button
    lateinit var configureButton: Button
    lateinit var startButton: Button
    lateinit var accountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider.AndroidViewModelFactory(application).create(MainViewModel::class.java)

        accountTextView = findViewById(R.id.accountTextView)

        configureButton = findViewById(R.id.configureButton)
        configureButton.setOnClickListener {
            viewModel.startAccountActivity(this)
        }

        portalButton = findViewById(R.id.portalButton)
        portalButton.setOnClickListener {
            viewModel.startPortalActivity(this)
        }

        startButton = findViewById(R.id.startButton)
        startButton.setOnClickListener { viewModel.startSession(this) }

        viewModel.account.observe(this, object : Observer<Account> {
            override fun onChanged(account: Account) {
                when (account.state) {
                    AccountState.ACCOUNT_NOT_SET -> {
                        accountTextView.setText(R.string.configure_account_text)
                        startButton.isEnabled = false
                    }
                    AccountState.SESSION_NOT_STARTED -> {
                        accountTextView.text = account.username
                        startButton.isEnabled = true
                    }
                    AccountState.SESSION_STARTED -> {
                        viewModel.startSessionActivity(this@MainActivity)
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkAccount(this)
    }

}
