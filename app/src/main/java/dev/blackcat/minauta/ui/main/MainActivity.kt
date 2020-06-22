package dev.blackcat.minauta.ui.main

import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
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

    lateinit var sessionLimitCheckBox: CheckBox
    lateinit var sessionLimitEditText: EditText
    lateinit var sessionLimitTextView: TextView

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

        sessionLimitEditText = findViewById(R.id.sessionLimitEditText)
        sessionLimitEditText.text = Editable.Factory.getInstance().newEditable("0")
        sessionLimitEditText.isEnabled = false

        sessionLimitTextView = findViewById(R.id.sessionLimitTextView)
        sessionLimitTextView.isEnabled = false

        sessionLimitCheckBox = findViewById(R.id.sessionLimitCheckBox)
        sessionLimitCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            sessionLimitEditText.isEnabled = isChecked
            sessionLimitTextView.isEnabled = isChecked
        }

        startButton = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            val time = sessionLimitEditText.text.toString().toInt()
            viewModel.startSession(this, sessionLimitCheckBox.isChecked, time)
        }

        viewModel.account.observe(this, Observer<Account> { account ->
            sessionLimitEditText.text = Editable.Factory.getInstance().newEditable("${account.sessionLimitTime}")
            sessionLimitCheckBox.isChecked = account.sessionLimitEnabled

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
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkAccount(this)
    }

}
