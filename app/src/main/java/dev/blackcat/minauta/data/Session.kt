package dev.blackcat.minauta.data

import java.io.Serializable

enum class SessionTimeUnit(val value: Int) {
    SECONDS(0),
    MINUTES(1),
    HOURS(2);

    companion object {
        fun fromInt(value: Int) = values().first { opt -> opt.value == value }
    }
}

data class SessionLimit(
        var enabled: Boolean,
        var time: Int,
        var timeUnit: SessionTimeUnit) : Serializable

data class Session(
        var loginParams: String = "",
        var startTime: Long = 0) : Serializable