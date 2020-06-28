package dev.blackcat.minauta

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dev.blackcat.minauta.data.*

class PreferencesStore(context: Context) {

    private val prefs = context.getSharedPreferences(MiNautaApp::class.java.name, Context.MODE_PRIVATE)

    val account: Account
        get() {
            val username = prefs.getString(USERNAME, "")
            val password = prefs.getString(PASSWORD, "")
            if (username == "") {
                val sessionLimit = SessionLimit(false, 0, SessionTimeUnit.MINUTES)
                return Account(null, null, null, sessionLimit, AccountState.ACCOUNT_NOT_SET)
            }

            var state = AccountState.SESSION_NOT_STARTED
            var session: Session? = null
            val loginParams = prefs.getString(LOGIN_PARAMS, "")
            val startTime = prefs.getLong(START_TIME, 0)
            if (loginParams != "") {
                session = Session()
                session.loginParams = loginParams!!
                session.startTime = startTime
                state = AccountState.SESSION_STARTED
            }

            val sessionLimit = getSessionLimit()
            return Account(username!!, password!!, session, sessionLimit, state)
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

    fun setSession(loginParams: String, startTime: Long) {
        val editor = prefs.edit()
        editor.putString(LOGIN_PARAMS, loginParams)
        editor.putLong(START_TIME, startTime)
        editor.apply()
    }

    companion object {

        val USERNAME = "dev.blackcat.minauta.Username"
        val PASSWORD = "dev.blackcat.minauta.Password"
        val LOGIN_PARAMS = "dev.blackcat.minauta.LoginParams"
        val START_TIME = "dev.blackcat.minauta.StartTime"
        val SESSION_LIMIT_ENABLED = "dev.blackcat.minauta.SessionLimitEnabled"
        val SESSION_LIMIT_TIME = "dev.blackcat.minauta.SessionLimitTime"
        val SESSION_LIMIT_TIME_UNIT = "dev.blackcat.minauta.SessionLimitTimeUnit"
    }

}
