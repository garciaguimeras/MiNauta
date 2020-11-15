package dev.blackcat.minauta.ui.main

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dev.blackcat.minauta.R
import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.SessionLimit
import dev.blackcat.minauta.data.SessionTimeUnit
import dev.blackcat.minauta.ui.MyAppCompatActivity

class MainActivity : MyAppCompatActivity() {

    lateinit var viewModel: MainViewModel

    lateinit var portalButton: Button
    lateinit var configureButton: Button
    lateinit var startButton: Button
    lateinit var accountTextView: TextView

    lateinit var sessionLimitCheckBox: CheckBox
    lateinit var sessionLimitEditText: EditText
    lateinit var sessionLimitSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider.AndroidViewModelFactory(application).create(MainViewModel::class.java)

        findViewById<ImageView>(R.id.logoImageView).setOnClickListener { showAboutDialog() }
        findViewById<TextView>(R.id.appNameTextView).setOnClickListener { showAboutDialog() }

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
        sessionLimitEditText.visibility = View.GONE

        sessionLimitSpinner = findViewById(R.id.sessionLimitSpinner)
        sessionLimitSpinner.visibility = View.GONE

        sessionLimitCheckBox = findViewById(R.id.sessionLimitCheckBox)
        sessionLimitCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            val visibility = if (isChecked) View.VISIBLE else View.GONE
            sessionLimitEditText.visibility = visibility
            sessionLimitSpinner.visibility = visibility
        }

        startButton = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            val sessionLimit = createSessionLimit()
            viewModel.startSession(this, sessionLimit)
        }

        viewModel.accountObservable.observe(this, Observer { account ->
            checkAccountState(account)
            setSessionLimit(account.sessionLimit)
        })

        viewModel.sessionObservable.observe(this, Observer { session ->
            session?.let {
                viewModel.startSessionActivity(this)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkAccount()
        viewModel.checkSession()
    }

    override fun onPause() {
        super.onPause()
        val sessionLimit = createSessionLimit()
        viewModel.saveSessionLimit(sessionLimit)
    }

    private fun checkAccountState(account: Account) {
        when (account.initialized) {
            false -> {
                accountTextView.setText(R.string.configure_account_text)
                startButton.isEnabled = false
                portalButton.isEnabled = false
            }
            true -> {
                accountTextView.text = account.username
                startButton.isEnabled = true
                portalButton.isEnabled = true
            }
        }
    }

    private fun setSessionLimit(sessionLimit: SessionLimit) {
        sessionLimitCheckBox.isChecked = sessionLimit.enabled
        sessionLimitEditText.text = Editable.Factory.getInstance().newEditable("${sessionLimit.time}")
        sessionLimitSpinner.setSelection(sessionLimit.timeUnit.value)
    }

    private fun createSessionLimit(): SessionLimit {
        val unit = SessionTimeUnit.fromInt(sessionLimitSpinner.selectedItemPosition)
        val time = sessionLimitEditText.text.toString().toInt()
        return SessionLimit(sessionLimitCheckBox.isChecked, time, unit)
    }

}
