package dev.blackcat.minauta.service

import android.app.*
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import dev.blackcat.minauta.BundledString
import dev.blackcat.minauta.R
import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.Session
import dev.blackcat.minauta.data.SessionTimeUnit
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.net.ConnectionManager
import dev.blackcat.minauta.ui.session.SessionActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SessionServiceHandler(val service: SessionService) : Handler() {
    override fun handleMessage(msg: Message) {
        when (msg.what) {
            SessionService.REC_ADD_MESSENGER_MESSAGE -> {
                service.outgoingMessenger = msg.replyTo
                service.sendRunning()
            }
            /*
            SessionService.REC_REMOVE_MESSENGER_MESSAGE -> {
                service.outgoingMessengers.remove(msg.replyTo)
            }
            SessionService.REC_START_MESSAGE -> {
                service.account = BundledString.toObject(msg.data, Account::class.java)
                service.start()
            }
             */
            SessionService.REC_STOP_MESSAGE -> { service.stop() }
            // SessionService.REC_STATUS_MESSAGE -> { service.sendRunning() }
            else -> { super.handleMessage(msg) }
        }
    }
}

class SessionService : Service() {

    companion object {
        const val NOTIFICATION_ID = 2000
        const val CHANNEL_ID = "dev.blackcat.minauta.service.ChannelId"

        const val ACCOUNT = "dev.blackcat.minauta.service.Account"

        const val REC_ADD_MESSENGER_MESSAGE = 1000
        // const val REC_REMOVE_MESSENGER_MESSAGE = 1001
        // const val REC_START_MESSAGE = 1002
        const val REC_STOP_MESSAGE = 1003
        // const val REC_STATUS_MESSAGE = 1004

        const val SND_LOGIN_RESULT_MESSAGE = 2000
        const val SND_AVAILABLE_TIME_MESSAGE = 2001
        const val SND_USED_TIME_MESSAGE = 2002
        const val SND_LOGOUT_RESULT_MESSAGE = 2003
        const val SND_RUNNING_MESSAGE = 2004
    }


    var running = false
    var account: Account? = null
    var session: Session? = null

    var notificationManager: NotificationManager? = null
    lateinit var incomingMessenger: Messenger
    var outgoingMessenger: Messenger? = null

    override fun onBind(intent: Intent?): IBinder? {
        incomingMessenger = Messenger(SessionServiceHandler(this))
        return incomingMessenger.binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("====>", "onStartCommand $intent")

        intent?.let {
            account = intent.getSerializableExtra(ACCOUNT) as Account
            startAsForegroundService()
            start()
        }
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "SessionServiceChannel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun startAsForegroundService() {
        Log.i("====>", "startAsForegroundService")

        createNotificationChannel()
        val notification = createNotification(null)
        startForeground(NOTIFICATION_ID, notification)
    }

    fun stop() {
        running = false
    }

    fun start() {
        if (running) return

        account?.let { account ->

            CoroutineScope(Dispatchers.Default).launch {
                Log.i("====>", "started")
                running = true
                val connectionManager = ConnectionManager(account)
                val loginResult = connectionManager.login()
                sendLoginResult(loginResult)
                session = loginResult.session
                if (loginResult.state != Connection.State.OK) {
                    running = false
                    return@launch
                }

                Log.i("====>", "time and loop")
                val availableTimeResult = connectionManager.getAvailableTime(session!!)
                sendAvailableTimeResult(availableTimeResult)
                val limitInSeconds = getSessionLimit()
                while (running) {
                    val time = Calendar.getInstance().timeInMillis
                    val diff = (time - session!!.startTime) / 1000
                    if (account.sessionLimit.enabled && diff >= limitInSeconds) {
                        running = false
                        continue
                    }

                    val timeStr = millisToString(diff)
                    Log.i("====>", "time ${timeStr}")
                    sendAvailableTimeResult(availableTimeResult)
                    sendUsedTime(timeStr)
                    sendNotification(timeStr)
                    try { Thread.sleep(1000) } catch (e: Exception) {}
                }

                Log.i("====>", "logout")
                stopForeground(true)
                val logoutResult = connectionManager.logout(session!!)
                sendLogoutResult(logoutResult)
                Log.i("====>", "ended")
            }

        }
    }

    private fun createNotification(text: String?): Notification {
        val intent = Intent(this, SessionActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentText("${getString(R.string.used_time_text)} $text" ?: "")
                .build()
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE or Notification.FLAG_ONLY_ALERT_ONCE
        return notification
    }

    private fun sendNotification(text: String) {
        val notification = createNotification(text)
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private fun sendLoginResult(result: Connection.LoginResult) {
        outgoingMessenger?.let { messenger ->
            val msg = Message.obtain()
            msg.what = SND_LOGIN_RESULT_MESSAGE
            msg.data = BundledString.fromObject(result)
            messenger.send(msg)
        }
    }

    fun sendRunning() {
        outgoingMessenger?.let { messenger ->
            val msg = Message.obtain()
            msg.what = SND_RUNNING_MESSAGE
            msg.data = BundledString.fromObject(running.toString())
            messenger.send(msg)
        }
    }

    private fun sendAvailableTimeResult(result: Connection.AvailableTimeResult) {
        outgoingMessenger?.let { messenger ->
            val msg = Message.obtain()
            msg.what = SND_AVAILABLE_TIME_MESSAGE
            msg.data = BundledString.fromObject(result)
            messenger.send(msg)
        }
    }

    private fun sendUsedTime(time: String) {
        outgoingMessenger?.let { messenger ->
            val msg = Message.obtain()
            msg.what = SND_USED_TIME_MESSAGE
            msg.data = BundledString.fromObject(time)
            messenger.send(msg)
        }
    }

    private fun sendLogoutResult(result: Connection.LogoutResult) {
        outgoingMessenger?.let { messenger ->
            val msg = Message.obtain()
            msg.what = SND_LOGOUT_RESULT_MESSAGE
            msg.data = BundledString.fromObject(result)
            messenger.send(msg)
        }
    }

    private fun getSessionLimit(): Int {
        val sessionLimit = account!!.sessionLimit
        return if (sessionLimit.enabled) {
            when (sessionLimit.timeUnit) {
                SessionTimeUnit.SECONDS -> {
                    account!!.sessionLimit.time
                }
                SessionTimeUnit.MINUTES -> {
                    account!!.sessionLimit.time * 60
                }
                SessionTimeUnit.HOURS -> {
                    account!!.sessionLimit.time * 3600
                }
            }
        } else 0
    }

    private fun millisToString(millis: Long): String {
        var minutes = millis / 60
        val seconds = millis % 60
        val hours = minutes / 60
        minutes %= 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}