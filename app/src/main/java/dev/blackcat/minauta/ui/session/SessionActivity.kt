package dev.blackcat.minauta.ui.session

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import dev.blackcat.minauta.R
import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.AccountState
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.service.UsedTimeService
import dev.blackcat.minauta.ui.MyAppCompatActivity

class SessionActivity : MyAppCompatActivity() {

    lateinit var viewModel: SessionViewModel

    lateinit var closeButton: Button
    lateinit var availableTimeTextView: TextView
    lateinit var usedTimeTextView: TextView

    var closingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)
        viewModel = ViewModelProvider.AndroidViewModelFactory(application).create(SessionViewModel::class.java)

        closeButton = findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            closingDialog = showDialogWithText(R.string.closing_text)
            viewModel.closeSession()
        }

        availableTimeTextView = findViewById(R.id.availableTimeTextView)
        usedTimeTextView = findViewById(R.id.usedTimeTextView)

        viewModel.account.observe(this, Observer<Account> { account ->
            if (account.state != AccountState.SESSION_STARTED) {
                finish()
            }
        })
        viewModel.availableTime.observe(this, Observer<String> { text ->
            val availableTimeText = getString(R.string.available_time_text)
            availableTimeTextView.text = "${availableTimeText} ${text}"
        })
        viewModel.usedTime.observe(this, Observer<String> { text ->
            val usedTimeText = getString(R.string.used_time_text)
            usedTimeTextView.text = "${usedTimeText} ${text}"
        })
        viewModel.logoutResult.observe(this, Observer<Connection.State> { state ->
            closingDialog?.dismiss()
            if (state != Connection.State.OK) {
                this@SessionActivity.showOneButtonDialogWithText(R.string.logout_error)
            }
            finish()
        })

        if (intent.action == UsedTimeService.SESSION_EXPIRED_ACTION) {
            val stateStr = intent.getStringExtra(UsedTimeService.LOGOUT_STATE) ?: ""
            val state = Connection.State.valueOf(stateStr)
            viewModel.setSessionExpired(state)
        }
        else {
            viewModel.startUsedTimeService()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkAccount()
        viewModel.registerBroadcastReceiver(this)
        viewModel.startSession()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterBroadcastReceiver(this)
    }

}
