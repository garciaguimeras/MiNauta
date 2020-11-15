package dev.blackcat.minauta.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import dev.blackcat.minauta.R
import dev.blackcat.minauta.data.*

class PreferencesStore(val context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val mapper = ObjectMapper();

    val account: Account
        get() {
            val storedAccounts = getStoredAccounts()
            val selectedAccount = storedAccounts.firstOrNull { a -> a.selected }
            if (selectedAccount == null) {
                val sessionLimit = SessionLimit(false, 0, SessionTimeUnit.MINUTES)
                return Account(null, null, sessionLimit, initialized = false)
            }
            val accountTypeValue = when (selectedAccount.accountType) {
                AccountType.NATIONAL -> context.getString(R.string.national_account_value)
                AccountType.INTERNATIONAL -> context.getString(R.string.international_account_value)
            }
            val fullUsername = "${selectedAccount.username}@${accountTypeValue}"
            val sessionLimit = sessionLimit
            return Account("${fullUsername}", selectedAccount.password!!, sessionLimit, initialized = true)
        }

    fun setStoredAccounts(accounts: List<StoredAccount>) {
        val storedAccountsStr = mapper.writeValueAsString(accounts)
        val editor = prefs.edit()
        editor.putString(ACCOUNTS, storedAccountsStr)
        editor.commit()
    }

    fun getStoredAccounts(): List<StoredAccount> {
        val storedAccountsStr = prefs.getString(ACCOUNTS, "[]")
        val storedAccounts = mapper.readValue(storedAccountsStr, object: TypeReference<List<StoredAccount>>() {})
        return storedAccounts
    }

    val sessionLimit: SessionLimit
        get() {
            val sessionLimitEnabled = prefs.getBoolean(SESSION_LIMIT_ENABLED, false)
            val sessionLimitTime = prefs.getInt(SESSION_LIMIT_TIME, 0)
            val sessionLimitTimeUnit = prefs.getInt(SESSION_LIMIT_TIME_UNIT, SessionTimeUnit.MINUTES.value)
            return SessionLimit(sessionLimitEnabled, sessionLimitTime, SessionTimeUnit.fromInt(sessionLimitTimeUnit))
        }

    val session: Session?
        get() {
            val loginParams = prefs.getString(LOGIN_PARAMS, "")!!
            val startTime = prefs.getLong(START_TIME, 0)
            return if (loginParams.isNullOrEmpty() && startTime == 0L) null
                   else Session(loginParams, startTime)
        }

    fun removeSession() {
        val editor = prefs.edit()
        editor.putString(LOGIN_PARAMS, "")
        editor.putLong(START_TIME, 0)
        editor.apply()
    }

    fun setSession(loginParams: String, startTime: Long) {
        val editor = prefs.edit()
        editor.putString(LOGIN_PARAMS, loginParams)
        editor.putLong(START_TIME, startTime)
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
        val ACCOUNTS = "dev.blackcat.minauta.Accounts"

        val LOGIN_PARAMS = "dev.blackcat.minauta.LoginParams"
        val START_TIME = "dev.blackcat.minauta.StartTime"

        val SESSION_LIMIT_ENABLED = "dev.blackcat.minauta.SessionLimitEnabled"
        val SESSION_LIMIT_TIME = "dev.blackcat.minauta.SessionLimitTime"
        val SESSION_LIMIT_TIME_UNIT = "dev.blackcat.minauta.SessionLimitTimeUnit"
    }

}
