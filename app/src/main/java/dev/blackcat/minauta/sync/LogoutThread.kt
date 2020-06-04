package dev.blackcat.minauta.sync

import dev.blackcat.jnauta.net.Authentication
import dev.blackcat.jnauta.net.ConnectionBuilder
import dev.blackcat.minauta.data.Account

class LogoutThread(internal var account: Account) : SyncRunnable<Boolean>() {

    override fun run() {
        val authentication = Authentication(ConnectionBuilder.Method.OK_HTTP, account.username, account.password, null)
        val r = authentication.logout(account.session!!.loginParams)
        this.result = r
    }
}