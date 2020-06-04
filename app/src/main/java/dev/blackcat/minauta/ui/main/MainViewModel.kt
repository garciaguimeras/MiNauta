package dev.blackcat.minauta.ui.main

import android.app.Application
import android.content.Intent
import dev.blackcat.minauta.R
import dev.blackcat.minauta.async.SessionLoginAsyncTask
import dev.blackcat.minauta.data.store.PreferencesStore
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.ui.MyViewModel
import dev.blackcat.minauta.ui.account.AccountActivity
import dev.blackcat.minauta.ui.session.SessionActivity
import java.util.*

class MainViewModel(application: Application) : MyViewModel(application) {

    fun startSessionActivity(activity: MainActivity) {
        val intent = Intent(activity, SessionActivity::class.java)
        activity.startActivity(intent)
    }

    fun startAccountActivity(activity: MainActivity) {
        val intent = Intent(activity, AccountActivity::class.java)
        activity.startActivity(intent)
    }

    fun startSession(activity: MainActivity) {
        val store = PreferencesStore(activity)
        val connectingDialog = activity.showDialogWithText(R.string.connecting_text)

        SessionLoginAsyncTask(activity, object: SessionLoginAsyncTask.OnTaskResult {
            override fun onResult(result: Connection.LoginResult) {
                connectingDialog.dismiss()

                if (result.state != Connection.State.OK) {
                    var resId = R.string.login_error
                    when (result.state) {
                        Connection.State.ALREADY_CONNECTED -> resId = R.string.already_connected_error
                        Connection.State.NO_MONEY -> resId = R.string.no_money_error
                        Connection.State.INCORRECT_PASSWORD -> resId = R.string.incorrect_password_error
                        Connection.State.UNKNOWN_USERNAME -> resId = R.string.unknown_username_error
                    }
                    activity.showOneButtonDialogWithText(resId)
                    return
                }

                val now = Calendar.getInstance().timeInMillis
                store.setSession(result.session!!.loginParams, now)
                startSessionActivity(activity)
            }
        }).execute()

    }

}