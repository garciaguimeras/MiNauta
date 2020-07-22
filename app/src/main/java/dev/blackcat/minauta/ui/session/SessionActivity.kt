package dev.blackcat.minauta.ui.session

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import dev.blackcat.minauta.R
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.ui.MyAppCompatActivity

class SessionActivity : MyAppCompatActivity() {

    companion object {
        const val CLOSE_SESSION_ACTION = "dev.blackcat.minauta.CLOSE_SESSION_ACTION"
        const val SHOULD_START_SERVICE = "dev.blackcat.minauta.ui.session.ShouldStartService"

        const val ALARM_DELAY = 2500L;
    }

    lateinit var viewModel: SessionViewModel

    lateinit var parentLayout: ConstraintLayout
    lateinit var closeButton: Button
    lateinit var availableTimeTextView: TextView
    lateinit var usedTimeTextView: TextView

    var closingDialog: AlertDialog? = null
    var shouldStartService = false

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.closeSession()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)
        viewModel = SessionViewModelFactory(this).create(SessionViewModel::class.java)
        shouldStartService = intent.getBooleanExtra(SHOULD_START_SERVICE, false)

        parentLayout = findViewById(R.id.parentLayout)

        closeButton = findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            closingDialog = showDialogWithText(R.string.closing_text)
            viewModel.closeSession()
        }

        availableTimeTextView = findViewById(R.id.availableTimeTextView)
        usedTimeTextView = findViewById(R.id.usedTimeTextView)

        viewModel.sessionObservable.observe(this, Observer { session ->
            if (session == null) {
                if (shouldStartService) {
                    viewModel.startSession()
                    shouldStartService = false
                }
                else {
                    finish()
                }
            }
            else {
                viewModel.continueSession()
            }
        })

        viewModel.loginResult.observe(this, Observer { result ->
            onLoginResult(result)
        })
        viewModel.availableTime.observe(this, Observer { result ->
            val availableTimeText = getString(R.string.available_time_text)
            var text = if (result.state == Connection.State.OK) result.availableTime!! else "--:--:--"
            availableTimeTextView.text = "${availableTimeText} ${text}"
        })
        viewModel.usedTime.observe(this, Observer { text ->
            val usedTimeText = getString(R.string.used_time_text)
            usedTimeTextView.text = "${usedTimeText} ${text}"
        })
        viewModel.logoutResult.observe(this, Observer { result ->
            onLogoutResult(result)
        })
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, IntentFilter(CLOSE_SESSION_ACTION))
        viewModel.checkSession()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onBackPressed() {
        Toast.makeText(this, R.string.close_session_warning, Toast.LENGTH_LONG)
                .show()
    }

    private fun onLoginResult(result: Connection.LoginResult) {
        val state = result.state
        if (state != Connection.State.OK) {
            var resId = R.string.login_error
            when (state) {
                Connection.State.ALREADY_CONNECTED -> resId = R.string.already_connected_error
                Connection.State.NO_MONEY -> resId = R.string.no_money_error
                Connection.State.INCORRECT_PASSWORD -> resId = R.string.incorrect_password_error
                Connection.State.UNKNOWN_USERNAME -> resId = R.string.unknown_username_error
            }
            val dialog = createDialogWithText(resId)
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.button_accept)) { dialogInterface, i ->
                dialogInterface.dismiss()
                finish()
            }
            dialog.show()
        }
    }

    private fun onLogoutResult(result: Connection.LogoutResult) {
        closingDialog?.dismiss()

        val textResId = if (result.state == Connection.State.OK) R.string.logout_ok else R.string.logout_error
        val dialog = createDialogWithText(textResId)

        if (result.state != Connection.State.OK) {
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.button_retry)) { dialogInterface, i ->
                dialogInterface.dismiss()
                viewModel.closeSession()
            }
        }
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.button_accept)) { dialogInterface, i ->
            dialogInterface.dismiss()
            viewModel.forceSessionClosing()
            finish()
        }

        dialog.show()
    }

}
