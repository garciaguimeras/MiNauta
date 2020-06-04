package dev.blackcat.minauta.async

import android.content.Context

import java.util.Calendar
import java.util.Timer
import java.util.TimerTask

import dev.blackcat.minauta.data.Session
import dev.blackcat.minauta.data.store.PreferencesStore

class SessionUsedTimeTask(internal var context: Context, internal var result: OnTaskResult?) {

    interface OnTaskResult {
        fun onResult(time: String)
    }

    fun execute() {
        val store = PreferencesStore(context)
        val session = store.account!!.session
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val time = Calendar.getInstance().timeInMillis
                val diff = (time - session!!.startTime) / 1000

                var minutes = diff / 60
                val seconds = diff % 60

                val hours = minutes / 60
                minutes = minutes % 60

                val timeStr = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                if (result != null)
                    result!!.onResult(timeStr)
            }
        }, Calendar.getInstance().time, 1000)
    }

}
