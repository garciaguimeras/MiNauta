package dev.blackcat.minauta.data

import java.io.Serializable

import dev.blackcat.jnauta.net.AuthenticationResponseParser

data class Session(
        var loginParams: String = "",
        var startTime: Long = 0) : Serializable