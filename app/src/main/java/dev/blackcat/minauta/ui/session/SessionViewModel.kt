package dev.blackcat.minauta.ui.session

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.MutableLiveData
import dev.blackcat.minauta.MiNautaApp
import dev.blackcat.minauta.service.UsedTimeService
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.net.ConnectionManager
import dev.blackcat.minauta.ui.MyViewModel

class SessionViewModel(application: Application) : MyViewModel(application) {

    val app = application as MiNautaApp
    val broadcastReceiver = SessionBroadcastReceiver(this)

    var availableTime: MutableLiveData<String> = MutableLiveData()
    var usedTime: MutableLiveData<String> = MutableLiveData()
    var logoutResult: MutableLiveData<Connection.State> = MutableLiveData()

    fun startSession() {
        val connectionManager = ConnectionManager(preferencesStore.account)
        val result = connectionManager.getAvailableTime()
        if (result.state == Connection.State.OK)
            availableTime.postValue(result.availableTime)
        else
            availableTime.postValue("--:--:--")
    }

    fun setTimerTick(time: String) {
        usedTime.postValue(time)
    }

    fun setSessionExpired(state: Connection.State) {
        preferencesStore.setSession("", 0)
        logoutResult.postValue(state)
    }

    fun closeSession() {
        val connectionManager = ConnectionManager(preferencesStore.account)
        val result = connectionManager.logout()
        preferencesStore.setSession("", 0)
        stopUsedTimeService()
        logoutResult.postValue(result.state)
    }

    fun startUsedTimeService() {
        val intent = Intent(app.applicationContext, UsedTimeService::class.java)
        intent.putExtra(UsedTimeService.ACCOUNT, preferencesStore.account)
        app.startService(intent)
    }

    fun stopUsedTimeService() {
        val broadcastIntent = Intent()
        broadcastIntent.action = UsedTimeService.END_SESSION_ACTION
        app.sendBroadcast(broadcastIntent)
    }

    fun registerBroadcastReceiver(activity: Activity) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(UsedTimeService.TIMER_TICK_ACTION)
        intentFilter.addAction(UsedTimeService.SESSION_EXPIRED_ACTION)
        activity.registerReceiver(broadcastReceiver, intentFilter)
    }

    fun unregisterBroadcastReceiver(activity: Activity) {
        activity.unregisterReceiver(broadcastReceiver)
    }

}