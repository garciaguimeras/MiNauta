package dev.blackcat.minauta.net

import java.util.Calendar

import dev.blackcat.jnauta.net.AuthenticationResponseParser
import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.Session

open class FakeConnection : Connection {

    override suspend fun login(account: Account): Connection.LoginResult {
        val session = Session()
        session.loginParams = "x=x&y=y"
        session.startTime = Calendar.getInstance().timeInMillis

        val result = Connection.LoginResult()
        result.session = session
        result.state = Connection.State.OK
        return result
    }

    override suspend fun logout(account: Account, session: Session): Connection.LogoutResult {
        val result = Connection.LogoutResult()
        result.session = session
        result.state = Connection.State.OK
        return result
    }

    override suspend fun getAvailableTime(account: Account, session: Session): Connection.AvailableTimeResult {
        val result = Connection.AvailableTimeResult()
        result.availableTime = "00:00:20:time@fake.connection"
        result.state = Connection.State.OK
        return result
    }
}

class FakeNoLoginConnection: FakeConnection() {

    override suspend fun login(account: Account): Connection.LoginResult {
        val result = super.login(account)
        result.state = Connection.State.ERROR
        return result
    }

}

class FakeNoLogoutConnection: FakeConnection() {

    override suspend fun logout(account: Account, session: Session): Connection.LogoutResult {
        val result = super.logout(account, session)
        result.state = Connection.State.ERROR
        return result
    }

}
