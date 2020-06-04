package dev.blackcat.minauta.net

import dev.blackcat.jnauta.net.AuthenticationResponseParser
import dev.blackcat.minauta.sync.AvailableTimeThread
import dev.blackcat.minauta.sync.LoginThread
import dev.blackcat.minauta.sync.LogoutThread
import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.Session

class JNautaConnection : Connection {

    override fun login(account: Account): Connection.LoginResult {
        val thread = LoginThread(account)
        val loginResult = thread.execute()

        val result = Connection.LoginResult()
        if (loginResult == null)
            result.state = Connection.State.ERROR
        else if (loginResult.alreadyConnected)
            result.state = Connection.State.ALREADY_CONNECTED
        else if (loginResult.noMoney)
            result.state = Connection.State.NO_MONEY
        else if (loginResult.badPassword)
            result.state = Connection.State.INCORRECT_PASSWORD
        else if (loginResult.badUsername)
            result.state = Connection.State.UNKNOWN_USERNAME
        else if (loginResult.isGoogle)
            result.state = Connection.State.IS_GOOGLE
        else if (loginResult.paramString == "")
            result.state = Connection.State.ERROR
        else {
            val session = Session()
            session.loginParams = loginResult.paramString
            result.session = session
            result.state = Connection.State.OK
        }
        return result
    }

    override fun logout(account: Account): Connection.LogoutResult {
        val thread = LogoutThread(account)
        val logoutResult = thread.execute()

        val result = Connection.LogoutResult()
        result.state = Connection.State.OK
        if (logoutResult == null || !logoutResult)
            result.state = Connection.State.ERROR
        return result
    }

    override fun getAvailableTime(account: Account): Connection.AvailableTimeResult {
        val thread = AvailableTimeThread(account)
        val time = thread.execute()

        val result = Connection.AvailableTimeResult()
        if (time == null)
            result.state = Connection.State.ERROR
        else {
            result.state = Connection.State.OK
            result.availableTime = time
        }
        return result
    }
}
