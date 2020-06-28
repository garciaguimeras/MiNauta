package dev.blackcat.minauta.ui.main

import android.app.Application
import android.content.Intent
import dev.blackcat.minauta.R
import dev.blackcat.minauta.data.SessionLimit
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.net.ConnectionManager
import dev.blackcat.minauta.ui.MyViewModel
import dev.blackcat.minauta.ui.account.AccountActivity
import dev.blackcat.minauta.ui.portal.PortalActivity
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

    fun startPortalActivity(activity: MainActivity) {
        val intent = Intent(activity, PortalActivity::class.java)
        activity.startActivity(intent)
    }

    fun startSession(activity: MainActivity, sessionLimit: SessionLimit) {
        val connectionManager = ConnectionManager(preferencesStore.account)
        val connectingDialog = activity.showDialogWithText(R.string.connecting_text)
        val result = connectionManager.login()
        val params = if (result.session != null) result.session!!.loginParams else ""
        val time = if (result.session != null) Calendar.getInstance().timeInMillis else 0
        preferencesStore.setSession(params, time)
        connectingDialog.dismiss()

        if (result?.state != Connection.State.OK) {
            var resId = R.string.login_error
            when (result?.state) {
                Connection.State.ALREADY_CONNECTED -> resId = R.string.already_connected_error
                Connection.State.NO_MONEY -> resId = R.string.no_money_error
                Connection.State.INCORRECT_PASSWORD -> resId = R.string.incorrect_password_error
                Connection.State.UNKNOWN_USERNAME -> resId = R.string.unknown_username_error
            }
            activity.showOneButtonDialogWithText(resId)
            return
        }

        preferencesStore.setSessionLimit(sessionLimit)
        startSessionActivity(activity)
    }

    fun saveSessionLimit(activity: MainActivity, sessionLimit: SessionLimit) {
        preferencesStore.setSessionLimit(sessionLimit)
    }

}