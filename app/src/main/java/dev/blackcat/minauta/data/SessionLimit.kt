package dev.blackcat.minauta.data

import android.os.Parcel
import android.os.Parcelable
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
        var timeUnit: SessionTimeUnit) : Serializable {

    constructor() : this(false, 0, SessionTimeUnit.MINUTES)

}
