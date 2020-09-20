package dev.blackcat.minauta.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import dev.blackcat.minauta.data.SessionLimit
import dev.blackcat.minauta.data.SessionTimeUnit
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.net.ConnectionManager
import dev.blackcat.minauta.ui.session.AlarmTimeoutReceiver
import dev.blackcat.minauta.ui.session.SessionActivity

class SessionUtil(val context: Context) {

    val preferencesStore = PreferencesStore(context)
    val connectionManager = ConnectionManager(preferencesStore.account)

    var alarmManager: AlarmManager? = null
    var alarmPendingIntent: PendingIntent? = null

    // Session

    fun login(): Connection.LoginResult {
        val result = connectionManager.login()
        if (result.state!! == Connection.State.OK) {
            val session = result.session!!
            preferencesStore.setSession(session.loginParams, session.startTime)
        }
        return result
    }

    fun getAvailableTime(): Connection.AvailableTimeResult {
        return connectionManager.getAvailableTime(preferencesStore.session!!)
    }

    fun logout(): Connection.LogoutResult? {
        if (preferencesStore.session == null) return null;

        val connectionManager = ConnectionManager(preferencesStore.account)
        val result = connectionManager.logout(preferencesStore.session!!)
        return result
    }

    fun cancelJob() {
        cancelAlarm()
        preferencesStore.removeSession()
    }

    // Alarm

    fun getSessionLimit(sessionLimit: SessionLimit): Long {
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

    fun setAlarm(sessionLimit: SessionLimit) {
        var sessionLimit = getSessionLimit(sessionLimit) - SessionActivity.ALARM_DELAY
        sessionLimit = if (sessionLimit > 0) sessionLimit else 0
        val timeInMillis = SystemClock.elapsedRealtime() + sessionLimit

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val intent = Intent(context, AlarmTimeoutReceiver::class.java)
        alarmPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        alarmManager?.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeInMillis, alarmPendingIntent)
    }

    fun cancelAlarm() {
        alarmPendingIntent?.let { intent ->
            alarmManager?.cancel(intent)
        }
    }

}