package dev.blackcat.minauta.ui.session

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.blackcat.minauta.data.SessionLimit
import dev.blackcat.minauta.data.SessionTimeUnit
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.net.ConnectionManager
import dev.blackcat.minauta.ui.MyViewModel
import kotlinx.coroutines.*
import java.util.*


class SessionViewModelFactory(val activity: SessionActivity) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SessionViewModel(activity) as T
    }
}

class SessionViewModel(val activity: SessionActivity) : MyViewModel(activity.application) {

    val loginResult = MutableLiveData<Connection.LoginResult>()
    val availableTime = MutableLiveData<Connection.AvailableTimeResult>()
    val usedTime = MutableLiveData<String>()
    val logoutResult = MutableLiveData<Connection.LogoutResult>()

    val connectionManager = ConnectionManager(preferencesStore.account)
    var timeLoopJob: Job? = null
    var alarmManager: AlarmManager? = null
    var alarmPendingIntent: PendingIntent? = null

    // Public methods

    fun startSession() {
        val result = login()
        if (!result) return
        val account = preferencesStore.account
        if (account.sessionLimit.enabled) {
            setAlarm(account.sessionLimit)
        }
        getAvailableTime()
        startTimeLoop()
    }

    fun continueSession() {
        if (timeLoopJob == null) {
            getAvailableTime()
            startTimeLoop()
        }
    }

    fun closeSession() {
        logout()
    }

    fun forceSessionClosing() {
        cancelJob()
    }

    // Session

    private fun login(): Boolean {
        val result = connectionManager.login()
        if (result.state!! == Connection.State.OK) {
            val session = result.session!!
            preferencesStore.setSession(session.loginParams, session.startTime)
        }
        loginResult.postValue(result)
        return result.state!! == Connection.State.OK
    }

    private fun getAvailableTime() {
        val result = connectionManager.getAvailableTime(preferencesStore.session!!)
        availableTime.postValue(result)
    }

    private fun startTimeLoop() {
        timeLoopJob = CoroutineScope(Dispatchers.Default).launch {
            val session = preferencesStore.session
            while (isActive) {
                val time = Calendar.getInstance().timeInMillis
                val diff = (time - session!!.startTime) / 1000
                val timeStr = millisToString(diff)
                usedTime.postValue(timeStr)
                delay(1000)
            }
        }
    }

    private fun logout() {
        preferencesStore.session?.let { session ->
            val connectionManager = ConnectionManager(preferencesStore.account)
            val result = connectionManager.logout(session)
            if (result.state!! == Connection.State.OK) {
                cancelJob()
            }
            logoutResult.postValue(result)
        }
    }

    private fun cancelJob() {
        timeLoopJob?.cancel()
        cancelAlarm()
        preferencesStore.removeSession()
    }

    private fun millisToString(millis: Long): String {
        var minutes = millis / 60
        val seconds = millis % 60
        val hours = minutes / 60
        minutes %= 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    // Alarm

    private fun getSessionLimit(sessionLimit: SessionLimit): Long {
        return when (sessionLimit.timeUnit) {
            SessionTimeUnit.SECONDS -> {
                sessionLimit.time * 1000L
            }
            SessionTimeUnit.MINUTES -> {
                sessionLimit.time * 60 * 1000L
            }
            SessionTimeUnit.HOURS -> {
                sessionLimit.time * 3600 * 1000L
            }
        }
    }

    private fun setAlarm(sessionLimit: SessionLimit) {
        val timeInMillis = Calendar.getInstance().timeInMillis + getSessionLimit(sessionLimit)

        alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        // val intent = Intent(SessionActivity.SESSION_EXPIRED_ACTION)
        val intent = Intent(activity, AlarmTimeoutReceiver::class.java)
        alarmPendingIntent = PendingIntent.getBroadcast(activity, 0, intent, 0)
        alarmManager?.set(AlarmManager.RTC_WAKEUP, timeInMillis, alarmPendingIntent)
    }

    private fun cancelAlarm() {
        alarmPendingIntent?.let { intent ->
            alarmManager?.cancel(intent)
        }
    }
}