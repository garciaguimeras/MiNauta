package dev.blackcat.minauta.ui.session

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import dev.blackcat.minauta.R
import dev.blackcat.minauta.net.UsedTimeTask
import dev.blackcat.minauta.data.store.PreferencesStore
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.net.ConnectionFactory
import dev.blackcat.minauta.net.JNautaConnection
import dev.blackcat.minauta.ui.MyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SessionViewModel(application: Application) : MyViewModel(application) {

    private val mainScope = CoroutineScope(Dispatchers.Main)

    var availableTime: MutableLiveData<String> = MutableLiveData()
    var usedTime: MutableLiveData<String> = MutableLiveData()
    var logoutResult: MutableLiveData<Connection.LogoutResult> = MutableLiveData()

    fun startSession(activity: Activity) {
        val store = PreferencesStore(activity)

        mainScope.launch {
            val connection = ConnectionFactory.createSessionProducer(JNautaConnection::class.java)
            val result = connection?.getAvailableTime(store.account)

            val availableTimeText = activity.getString(R.string.available_time_text)
            if (result?.state == Connection.State.OK)
                availableTime.postValue("${availableTimeText} ${result.availableTime}")
            else
                availableTime.postValue("${availableTimeText} --:--:--")
        }

        mainScope.launch {
            val usedTimeText = activity.getString(R.string.used_time_text)
            UsedTimeTask().execute(store.account, object : UsedTimeTask.OnTaskResult {
                override fun onResult(time: String) {
                    usedTime.postValue("${usedTimeText} ${time}")
                }
            })
        }
    }

    fun closeSession(activity: Activity) {
        mainScope.launch {
            val store = PreferencesStore(activity)
            val connection = ConnectionFactory.createSessionProducer(JNautaConnection::class.java)
            val result = connection?.logout(store.account)
            logoutResult.postValue(result)
        }
    }

    fun finishSession(activity: Activity) {
        PreferencesStore(activity).setSession("", 0)
        activity.finish()
    }

}