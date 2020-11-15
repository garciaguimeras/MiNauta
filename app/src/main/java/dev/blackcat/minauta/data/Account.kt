package dev.blackcat.minauta.data

import java.io.Serializable

enum class AccountType {
    NATIONAL,
    INTERNATIONAL
}

data class StoredAccount(
    var username: String,
    var password: String,
    var accountType: AccountType,
    var selected: Boolean
) {
    constructor() : this("", "", AccountType.INTERNATIONAL, false)
}

data class Account(
        var username: String?,
        var password: String?,

        var sessionLimit: SessionLimit,
        var initialized: Boolean) : Serializable {

    constructor() : this("", "", SessionLimit(), false)

}
