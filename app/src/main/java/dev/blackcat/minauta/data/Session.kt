package dev.blackcat.minauta.data

import java.io.Serializable

data class Session(
        var loginParams: String = "",
        var startTime: Long = 0) : Serializable