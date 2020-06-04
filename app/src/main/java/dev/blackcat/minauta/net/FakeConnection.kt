package dev.blackcat.minauta.net

import java.util.Calendar

import dev.blackcat.jnauta.net.AuthenticationResponseParser
import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.Session

class FakeConnection : Connection {
    override fun login(account: Account): Connection.LoginResult {
        val session = Session()
        session.loginParams = "x=x&y=y"
        session.startTime = Calendar.getInstance().timeInMillis

        val result = Connection.LoginResult()
        result.session = session
        result.state = Connection.State.OK

        return result
    }

    override fun logout(account: Account): Connection.LogoutResult {
        val result = Connection.LogoutResult()
        result.state = Connection.State.OK

        return result
    }

    override fun getAvailableTime(account: Account): Connection.AvailableTimeResult {
        val result = Connection.AvailableTimeResult()
        result.availableTime = "02:30:15"
        result.state = Connection.State.OK

        return result
    }
}
