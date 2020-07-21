package dev.blackcat.minauta.net

import dev.blackcat.jnauta.net.Authentication
import dev.blackcat.jnauta.net.ConnectionBuilder
import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class JNautaConnection : Connection {

    override suspend fun login(account: Account): Connection.LoginResult {
        val authentication = Authentication(ConnectionBuilder.Method.OK_HTTP, account.username, account.password, null)
        val loginResult = authentication.login()
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
            session.startTime = Calendar.getInstance().timeInMillis
            result.session = session
            result.state = Connection.State.OK
        }
        return result
    }

    override suspend fun logout(account: Account, session: Session): Connection.LogoutResult {
        val authentication = Authentication(ConnectionBuilder.Method.OK_HTTP, account.username, account.password, null)
        val logoutResult = authentication.logout(session.loginParams)

        val result = Connection.LogoutResult()
        result.session = session
        result.state = if (logoutResult) Connection.State.OK else Connection.State.ERROR
        return result
    }

    override suspend fun getAvailableTime(account: Account, session: Session): Connection.AvailableTimeResult {
        val authentication = Authentication(ConnectionBuilder.Method.OK_HTTP, account.username, account.password, null)
        val time = authentication.getAvailableTime(session!!.loginParams)

        val result = Connection.AvailableTimeResult()
        result.state = if (time != null) Connection.State.OK else Connection.State.ERROR
        result.availableTime = time
        return result
    }

}
