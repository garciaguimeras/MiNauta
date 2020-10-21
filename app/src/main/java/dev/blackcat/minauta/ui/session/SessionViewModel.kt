package dev.blackcat.minauta.ui.session

import android.os.SystemClock
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.ui.MyViewModel
import dev.blackcat.minauta.util.SessionUtil
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

    var timeLoopJob: Job? = null
    val sessionUtil = SessionUtil(activity)

    // Public methods

    fun startSession() {
        val result = sessionUtil.login()
        val baseTime = SystemClock.elapsedRealtime()
        loginResult.postValue(result)
        if (result.state!! != Connection.State.OK) return

        val timeResult = sessionUtil.getAvailableTime()
        availableTime.postValue(timeResult)
        val availableTimeInMillis = sessionUtil.getAvailableTimeInMillis(timeResult.availableTime)

        val account = preferencesStore.account
        val sessionLimitInMillis = sessionUtil.getSessionLimitInMillis(account.sessionLimit)

        val alarmTimeInMillis = Math.min(availableTimeInMillis, sessionLimitInMillis);
        sessionUtil.setAlarm(baseTime, alarmTimeInMillis)

        startTimeLoop()
    }

    fun continueSession() {
        if (timeLoopJob == null) {
            val timeResult = sessionUtil.getAvailableTime()
            availableTime.postValue(timeResult)
            startTimeLoop()
        }
    }

    fun closeSession() {
        val result = sessionUtil.logout()
        result?.let { result ->
            if (result.state!! == Connection.State.OK) {
                forceSessionClosing()
            }
            logoutResult.postValue(result)
        }
    }

    fun forceSessionClosing() {
        timeLoopJob?.cancel()
        sessionUtil.cancelJob()
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

    private fun millisToString(millis: Long): String {
        var minutes = millis / 60
        val seconds = millis % 60
        val hours = minutes / 60
        minutes %= 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

}