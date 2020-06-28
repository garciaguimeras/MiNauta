package dev.blackcat.minauta.service

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.SessionTimeUnit
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.net.ConnectionManager
import java.util.*


class UsedTimeService : IntentService(UsedTimeService::class.java.name) {

    companion object {
        const val ACCOUNT = "dev.blackcat.minauta.service.UsedTimeService.Account"

        const val TIME = "dev.blackcat.minauta.service.UsedTimeService.Time"
        const val LOGOUT_STATE = "dev.blackcat.minauta.service.UsedTimeService.LogoutState"

        const val END_SESSION_ACTION = "dev.blackcat.minauta.END_SESSION_ACTION"
        const val TIMER_TICK_ACTION = "dev.blackcat.minauta.TIMER_TICK_ACTION"
        const val SESSION_EXPIRED_ACTION = "dev.blackcat.minauta.SESSION_EXPIRED_ACTION"
    }

    val broadcastReceiver = UsedTimeBroadcastReceiver(this)
    var running: Boolean = false

    /*
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }
     */

    override fun onHandleIntent(intent: Intent?) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(END_SESSION_ACTION)
        registerReceiver(broadcastReceiver, intentFilter)

        intent?.let { intent ->
            val account = intent.getSerializableExtra(ACCOUNT) as Account
            startRunning(account)
        }
    }

    fun stopRunning() {
        running = false
    }

    private fun startRunning(account: Account) {
        if (running) return

        val sessionLimit = account.sessionLimit
        var limitInSeconds =
                if (sessionLimit.enabled) {
                    when (sessionLimit.timeUnit) {
                        SessionTimeUnit.SECONDS -> {
                            sessionLimit.time
                        }
                        SessionTimeUnit.MINUTES -> {
                            sessionLimit.time * 60
                        }
                        SessionTimeUnit.HOURS -> {
                            sessionLimit.time * 3600
                        }
                    }
                } else 0

        Log.i("====>", "Iniciando timer")

        running = true
        while (running) {

            val time = Calendar.getInstance().timeInMillis
            val diff = (time - account.session!!.startTime) / 1000

            if (sessionLimit.enabled && diff >= limitInSeconds) {

                Log.i("====>", "Timer expiro")

                val connectionManager = ConnectionManager(account)
                val result = connectionManager.logout()
                broadcastSessionExpired(result)
                running = false
                continue
            }

            var minutes = diff / 60
            val seconds = diff % 60
            val hours = minutes / 60
            minutes %= 60
            val timeStr = String.format("%02d:%02d:%02d", hours, minutes, seconds)

            Log.i("====>", "Enviando tiempo! ${timeStr}")

            broadcastTimerTick(timeStr)

            try { Thread.sleep(1000) } catch (e: Exception) {}
        }
        running = false

        Log.i("====>", "Saliendo del timer")
    }

    fun broadcastTimerTick(time: String) {
        val broadcastIntent = Intent()
        broadcastIntent.action = TIMER_TICK_ACTION
        broadcastIntent.putExtra(TIME, time)
        sendBroadcast(broadcastIntent)
    }

    fun broadcastSessionExpired(result: Connection.LogoutResult) {
        val intent = Intent()
        intent.action = SESSION_EXPIRED_ACTION
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(LOGOUT_STATE, result.state!!.name)
        startActivity(intent)
    }

}
