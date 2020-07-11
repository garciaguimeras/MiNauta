package dev.blackcat.minauta

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dev.blackcat.minauta.data.*

class PreferencesStore(context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    val account: Account
        get() {
            val username = prefs.getString(USERNAME, "")
            val password = prefs.getString(PASSWORD, "")
            if (username == "") {
                val sessionLimit = SessionLimit(false, 0, SessionTimeUnit.MINUTES)
                return Account(null, null, sessionLimit, initialized = false)
            }

            val sessionLimit = getSessionLimit()
            return Account(username!!, password!!, sessionLimit, initialized = true)
        }

    fun getSessionLimit(): SessionLimit {
        val sessionLimitEnabled = prefs.getBoolean(SESSION_LIMIT_ENABLED, false)
        val sessionLimitTime = prefs.getInt(SESSION_LIMIT_TIME, 0)
        val sessionLimitTimeUnit = prefs.getInt(SESSION_LIMIT_TIME_UNIT, SessionTimeUnit.MINUTES.value)
        return SessionLimit(sessionLimitEnabled, sessionLimitTime, SessionTimeUnit.fromInt(sessionLimitTimeUnit))
    }

    fun setAccount(username: String, password: String) {
        val editor = prefs.edit()
        editor.putString(USERNAME, username)
        editor.putString(PASSWORD, password)
        editor.apply()
    }

    fun setSessionLimit(sessionLimit: SessionLimit) {
        val editor = prefs.edit()
        editor.putBoolean(SESSION_LIMIT_ENABLED, sessionLimit.enabled)
        editor.putInt(SESSION_LIMIT_TIME, sessionLimit.time)
        editor.putInt(SESSION_LIMIT_TIME_UNIT, sessionLimit.timeUnit.ordinal)
        editor.apply()
    }

    companion object {
        val USERNAME = "dev.blackcat.minauta.Username"
        val PASSWORD = "dev.blackcat.minauta.Password"
        val SESSION_LIMIT_ENABLED = "dev.blackcat.minauta.SessionLimitEnabled"
        val SESSION_LIMIT_TIME = "dev.blackcat.minauta.SessionLimitTime"
        val SESSION_LIMIT_TIME_UNIT = "dev.blackcat.minauta.SessionLimitTimeUnit"
    }

}
