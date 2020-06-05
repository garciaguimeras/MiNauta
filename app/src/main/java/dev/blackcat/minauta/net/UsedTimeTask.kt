package dev.blackcat.minauta.net

import dev.blackcat.minauta.data.Account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class UsedTimeTask {

    interface OnTaskResult {
        fun onResult(time: String)
    }

    suspend fun execute(account: Account, result: OnTaskResult?) =
        withContext(Dispatchers.Default) {
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    val time = Calendar.getInstance().timeInMillis
                    val diff = (time - account.session!!.startTime) / 1000

                    var minutes = diff / 60
                    val seconds = diff % 60

                    val hours = minutes / 60
                    minutes = minutes % 60

                    val timeStr = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    result?.onResult(timeStr)
                }
            }, Calendar.getInstance().time, 1000)
        }

}
