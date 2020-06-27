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

        var session: Session?,
        var sessionLimit: SessionLimit,
        var state: AccountState) : Serializable
