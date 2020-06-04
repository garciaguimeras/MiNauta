package dev.blackcat.minauta.sync

import android.util.Log

import dev.blackcat.jnauta.net.Authentication
import dev.blackcat.jnauta.net.AuthenticationResponseParser
import dev.blackcat.jnauta.net.ConnectionBuilder
import dev.blackcat.minauta.data.Account

class LoginThread(internal var account: Account) : SyncRunnable<AuthenticationResponseParser.LoginResult>() {

    override fun run() {
        Log.i(LoginThread::class.java.toString(), "Starting thread")
        val authentication = Authentication(ConnectionBuilder.Method.OK_HTTP, account.username, account.password, null)
        Log.i(LoginThread::class.java.toString(), "jnauta-net.authentication.login")
        val r = authentication.login()
        Log.i(LoginThread::class.java.toString(), "Returned from jnauta-net.authentication.login: $r")
        this.result = r
        Log.i(LoginThread::class.java.toString(), "Result set, finishing thread")
    }
}