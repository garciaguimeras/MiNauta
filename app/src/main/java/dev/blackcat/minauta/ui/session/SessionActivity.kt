package dev.blackcat.minauta.ui.session

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
        const val SHOULD_START_SERVICE = "dev.blackcat.minauta.ui.session.ShouldStartService"
    }

    lateinit var viewModel: SessionViewModel

    lateinit var parentLayout: ConstraintLayout
    lateinit var closeButton: Button
    lateinit var availableTimeTextView: TextView
    lateinit var usedTimeTextView: TextView

    var closingDialog: AlertDialog? = null
    var shouldStartService = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)
        viewModel = SessionViewModelFactory(this).create(SessionViewModel::class.java)
        shouldStartService = intent.getBooleanExtra(SHOULD_START_SERVICE, false)

        parentLayout = findViewById(R.id.parentLayout)

        closeButton = findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            closingDialog = showDialogWithText(R.string.closing_text)
            viewModel.sendCloseSession()
        }

        availableTimeTextView = findViewById(R.id.availableTimeTextView)
        usedTimeTextView = findViewById(R.id.usedTimeTextView)

        viewModel.isServiceRunning.observe(this, Observer { running ->
            if (shouldStartService && !running) {
                viewModel.startService(this)
                shouldStartService = false
            }
        })

        viewModel.loginResult.observe(this, Observer { state ->
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
                    viewModel.sendCloseSession()
                    finish()
                }
                dialog.show()
            }
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
        viewModel.logoutResult.observe(this, Observer { state ->
            closingDialog?.dismiss()
            if (state != Connection.State.OK) {
                this@SessionActivity.showOneButtonDialogWithText(R.string.logout_error)
            }
            this@SessionActivity.finish()
        })

    }

    override fun onResume() {
        super.onResume()
        viewModel.bindService(this)
    }

    override fun onPause() {
        super.onPause()
        viewModel.unbindService(this)
    }

    override fun onBackPressed() {
        Toast.makeText(this, R.string.close_session_warning, Toast.LENGTH_LONG)
                .show()
    }

}
