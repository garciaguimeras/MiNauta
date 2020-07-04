package dev.blackcat.minauta.ui.main

import android.app.Application
import android.content.Intent
import android.os.Handler
import dev.blackcat.minauta.data.SessionLimit
import dev.blackcat.minauta.ui.MyViewModel
import dev.blackcat.minauta.ui.MyViewModelHandler
import dev.blackcat.minauta.ui.account.AccountActivity
import dev.blackcat.minauta.ui.portal.PortalActivity
import dev.blackcat.minauta.ui.session.SessionActivity

class MainViewModel(application: Application) : MyViewModel(application) {

    override fun getMessengerHandler(): Handler? {
        return MyViewModelHandler(this)
    }

    fun startSessionActivity(activity: MainActivity, shouldStartService: Boolean = false) {
        val intent = Intent(activity, SessionActivity::class.java)
        intent.putExtra(SessionActivity.SHOULD_START_SERVICE, shouldStartService)
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
        preferencesStore.setSessionLimit(sessionLimit)
        startSessionActivity(activity, shouldStartService = true)
    }

    fun saveSessionLimit(sessionLimit: SessionLimit) {
        preferencesStore.setSessionLimit(sessionLimit)
    }

}