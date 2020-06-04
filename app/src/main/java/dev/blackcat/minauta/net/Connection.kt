package dev.blackcat.minauta.net

import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.Session

interface Connection {

    enum class State {
        OK,
        ERROR,
        UNKNOWN_USERNAME,
        INCORRECT_PASSWORD,
        ALREADY_CONNECTED,
        NO_MONEY,
        IS_GOOGLE
    }

    class LoginResult {
        var session: Session? = null
        var state: State? = null
    }

    class LogoutResult {
        var state: State? = null
    }

    class AvailableTimeResult {
        var state: State? = null
        var availableTime: String? = null
    }

    fun login(account: Account): LoginResult
    fun logout(account: Account): LogoutResult
    fun getAvailableTime(account: Account): AvailableTimeResult

}
