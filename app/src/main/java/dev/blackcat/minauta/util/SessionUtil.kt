package dev.blackcat.minauta.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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

    fun getAvailableTimeInMillis(availableTime: String?): Long {
        val parts = availableTime?.split(":") ?: arrayListOf()
        if (parts.size < 3) {
            // Error getting available time... asumming infinite session
            return Long.MAX_VALUE;
        }

        val hours = parts[0].toLong()
        val minutes = parts[1].toLong()
        val seconds = parts[2].toLong()
        return (hours * 3600 + minutes * 60 + seconds) * 1000L
    }

    fun getSessionLimitInMillis(sessionLimit: SessionLimit): Long {
        if (!sessionLimit.enabled) {
            // No session limit defined
            return Long.MAX_VALUE
        }

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

    fun setAlarm(baseTime: Long, millis: Long) {
        var limit = millis - SessionActivity.ALARM_DELAY
        limit = if (limit > 0) limit else 0
        val timeInMillis = baseTime + limit

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val intent = Intent(context.applicationContext, AlarmTimeoutReceiver::class.java)
        val alarmPendingIntent = PendingIntent.getBroadcast(context.applicationContext, 0, intent, 0)
        alarmManager?.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeInMillis, alarmPendingIntent)
    }

    fun cancelAlarm() {
        val intent = Intent(context.applicationContext, AlarmTimeoutReceiver::class.java)
        val alarmPendingIntent = PendingIntent.getBroadcast(context.applicationContext, 0, intent, 0)
        alarmManager?.cancel(alarmPendingIntent)
    }

}