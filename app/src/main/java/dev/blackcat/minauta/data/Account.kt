package dev.blackcat.minauta.data

import java.io.Serializable

enum class AccountState {
    ACCOUNT_NOT_SET,
    SESSION_NOT_STARTED,
    SESSION_STARTED
}

data class Account(
        var username: String?,
        var password: String?,
        var sessionLimitEnabled: Boolean,
        var sessionLimitTime: Int,
        var session: Session?,
        var state: AccountState) : Serializable
