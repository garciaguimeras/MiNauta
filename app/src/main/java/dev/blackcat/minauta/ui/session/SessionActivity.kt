package dev.blackcat.minauta.ui.session

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import dev.blackcat.minauta.R
import dev.blackcat.minauta.net.Connection
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
            viewModel.closeSession(this)
        }

        availableTimeTextView = findViewById(R.id.availableTimeTextView)
        usedTimeTextView = findViewById(R.id.usedTimeTextView)

        viewModel.availableTime.observe(this, Observer<String> {
            text -> availableTimeTextView.text = text
        })
        viewModel.usedTime.observe(this, Observer<String> {
            text -> usedTimeTextView.text = text
        })
        viewModel.sessionExpired.observe(this, Observer<Boolean> { expired ->
            viewModel.closeSession(this)
        })
        viewModel.logoutResult.observe(this, Observer<Connection.LogoutResult> { result ->
            closingDialog?.dismiss()
            if (result.state != Connection.State.OK) {
                this@SessionActivity.showOneButtonDialogWithText(R.string.logout_error)
            }
            viewModel.finishSession(this@SessionActivity)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.startSession(this)
    }


}
