package dev.blackcat.minauta.service

import android.app.*
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import dev.blackcat.minauta.BundledString
import dev.blackcat.minauta.PreferencesStore
import dev.blackcat.minauta.R
import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.Session
import dev.blackcat.minauta.data.SessionTimeUnit
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.net.ConnectionManager
import dev.blackcat.minauta.ui.main.MainActivity
import dev.blackcat.minauta.ui.session.SessionActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class SessionServiceHandler(val service: SessionService) : Handler() {
    override fun handleMessage(msg: Message) {
        when (msg.what) {
            SessionService.REC_ADD_MESSENGER_MESSAGE -> {
                service.outgoingMessenger = msg.replyTo
                service.sendRunning()
            }
            SessionService.REC_STOP_MESSAGE -> { service.stop() }
            else -> { super.handleMessage(msg) }
        }
    }
}

class SessionService : Service() {

    companion object {
        const val NOTIFICATION_ID = 2000
        const val CHANNEL_ID = "dev.blackcat.minauta.service.ChannelId"

        const val REC_ADD_MESSENGER_MESSAGE = 1000
        const val REC_STOP_MESSAGE = 1003

        const val SND_LOGIN_RESULT_MESSAGE = 2000
        const val SND_AVAILABLE_TIME_MESSAGE = 2001
        const val SND_USED_TIME_MESSAGE = 2002
        const val SND_LOGOUT_RESULT_MESSAGE = 2003
        const val SND_RUNNING_MESSAGE = 2004
    }


    var running = false

    var notificationManager: NotificationManager? = null
    lateinit var incomingMessenger: Messenger
    var outgoingMessenger: Messenger? = null

    override fun onBind(intent: Intent?): IBinder? {
        incomingMessenger = Messenger(SessionServiceHandler(this))
        return incomingMessenger.binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(SessionService::class.java.name, "====> onStartCommand $intent")

        if (intent != null) {
            createSession()
        }
        val preferencesStore = PreferencesStore(this)
        if (preferencesStore.session != null) {
            startAsForegroundService()
            start()
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "SessionServiceChannel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun startAsForegroundService() {
        Log.d(SessionService::class.java.name, "====> startAsForegroundService")
        createNotificationChannel()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    fun stop() {
        if (!running) stopForeground(true)
        running = false
    }

    fun createSession() {
        val preferencesStore = PreferencesStore(this)
        val account = preferencesStore.account
        val connectionManager = ConnectionManager(account)

        Log.d(SessionService::class.java.name, "====> login")
        val loginResult = connectionManager.login()
        sendLoginResult(loginResult)
        if (loginResult.state == Connection.State.OK) {
            preferencesStore.setSession(loginResult.session!!.loginParams, loginResult.session!!.startTime)
        }
        else {
            preferencesStore.removeSession()
        }
    }

    fun start() {
        val preferencesStore = PreferencesStore(this)
        val account = preferencesStore.account
        val connectionManager = ConnectionManager(account)

        preferencesStore.session?.let { session ->

            CoroutineScope(Dispatchers.Default).launch {
                Log.d(SessionService::class.java.name, "====> start")
                running = true

                Log.d(SessionService::class.java.name, "====> time and loop")
                val availableTimeResult = connectionManager.getAvailableTime(session!!)
                sendAvailableTimeResult(availableTimeResult)
                val limitInSeconds = getSessionLimit(account)
                while (running) {
                    val time = Calendar.getInstance().timeInMillis
                    val diff = (time - session!!.startTime) / 1000
                    if (account.sessionLimit.enabled && diff >= limitInSeconds) {
                        running = false
                        continue
                    }

                    val timeStr = millisToString(diff)
                    Log.d(SessionService::class.java.name, "====> time ${timeStr}")
                    sendAvailableTimeResult(availableTimeResult)
                    sendUsedTime(timeStr)
                    sendTimeNotification(availableTimeResult.availableTime ?: "", timeStr, account)

                    delay(1000)
                }

                Log.d(SessionService::class.java.name, "====> logout")
                stopForeground(true)
                val logoutResult = connectionManager.logout(session!!)
                preferencesStore.removeSession()
                sendLogoutNotification(logoutResult)
                sendLogoutResult(logoutResult)
                Log.d(SessionService::class.java.name, "====> ended")
            }

        }
    }

    private fun createNotification(text: String = "", lines: List<String> = listOf()): Notification {
        val style = NotificationCompat.InboxStyle()
        lines.forEach { line -> style.addLine(line) }

        val intent = Intent(this, SessionActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setContentTitle(getString(R.string.app_name))
        builder.setSmallIcon(R.drawable.ic_stat_minauta)
        builder.setContentText(text)
        builder.setContentIntent(pendingIntent)
        if (lines.size > 0) {
            builder.setStyle(style)
        }
        val notification = builder.build()

        notification.flags = Notification.FLAG_FOREGROUND_SERVICE or Notification.FLAG_ONLY_ALERT_ONCE
        return notification
    }

    private fun sendTimeNotification(availableTime: String, usedTime: String, account: Account) {
        val sessionLimit = account.sessionLimit

        val text = "${getString(R.string.session_started_as_text)} ${account.username}"
        val lines = mutableListOf(
                "${getString(R.string.available_time_text)} $availableTime",
                "${getString(R.string.used_time_text)} $usedTime"
        )
        if (sessionLimit.enabled) {
            val timeUnitText = when (sessionLimit.timeUnit) {
                SessionTimeUnit.SECONDS -> { getString(R.string.seconds_text) }
                SessionTimeUnit.MINUTES -> { getString(R.string.minutes_text) }
                SessionTimeUnit.HOURS -> { getString(R.string.hours_text) }
            }
            val line = "${getString(R.string.session_limit_text)} ${sessionLimit.time} ${timeUnitText}"
            lines.add(line)
        }

        val notification = createNotification(text, lines)
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private fun sendLogoutNotification(result: Connection.LogoutResult) {
        val textResId = if (result.state == Connection.State.OK) R.string.logout_ok else R.string.logout_error
        val text = getString(textResId)

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setContentTitle(getString(R.string.app_name))
        builder.setSmallIcon(R.drawable.ic_stat_minauta)
        builder.setContentText(text)
        builder.setContentIntent(pendingIntent)
        val notification = builder.build()
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

    private fun getSessionLimit(account: Account): Int {
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