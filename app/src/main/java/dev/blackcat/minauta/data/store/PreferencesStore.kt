package dev.blackcat.minauta.data.store

import android.content.Context
import androidx.preference.PreferenceManager

import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.AccountState
import dev.blackcat.minauta.data.Session

class PreferencesStore(internal var context: Context) {

    val account: Account
        get() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this.context)
            val username = prefs.getString(USERNAME, "")
            val password = prefs.getString(PASSWORD, "")
            if (username == "")
                return Account(null, null, null, AccountState.ACCOUNT_NOT_SET)

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

            return Account(username!!, password!!, session, state)
        }

    fun setAccount(username: String, password: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this.context)
        val editor = prefs.edit()
        editor.putString(USERNAME, username)
        editor.putString(PASSWORD, password)
        editor.apply()
    }

    fun setSession(loginParams: String, startTime: Long) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this.context)
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
    }

}