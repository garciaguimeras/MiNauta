package dev.blackcat.minauta.data

import java.io.Serializable

data class Account(
        var username: String?,
        var password: String?,

        var sessionLimit: SessionLimit,
        var initialized: Boolean) : Serializable {

    constructor() : this("", "", SessionLimit(), false)

}
