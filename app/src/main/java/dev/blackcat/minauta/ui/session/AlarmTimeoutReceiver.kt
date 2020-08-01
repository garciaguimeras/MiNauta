package dev.blackcat.minauta.ui.session

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import dev.blackcat.minauta.util.PowerUtil
import dev.blackcat.minauta.util.PreferencesStore
import dev.blackcat.minauta.R
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.net.ConnectionManager
import dev.blackcat.minauta.util.SessionUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlarmTimeoutReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 1000
        const val CHANNEL_ID = "dev.blackcat.minauta.Channel"
        const val CHANNEL_NAME = "dev.blackcat.minauta.Channel"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("==>", "onReceive")

        context?.let { context ->
            CoroutineScope(Dispatchers.Default).launch {

                if (!PowerUtil.isScreenOn(context)) {
                    PowerUtil.acquireWakeLock(context)
                    sendNotification(context)

                    // TODO: How to fix the delay?
                    delay(SessionActivity.ALARM_DELAY)

                    val sessionUtil = SessionUtil(context)
                    val result = sessionUtil.logout()
                    result?.let { result ->
                        if (result.state == Connection.State.OK) {
                            Log.i("==>", "Cerrado!")
                            sessionUtil.cancelJob()
                        }
                    }

                    PowerUtil.releaseWakeLock()
                    return@launch
                }

                val activityIntent = Intent(context, SessionActivity::class.java)
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activityIntent)

                // TODO: How to fix the delay?
                delay(SessionActivity.ALARM_DELAY)

                val broadcastIntent = Intent(SessionActivity.CLOSE_SESSION_ACTION)
                context.sendBroadcast(broadcastIntent)
            }
        }
    }

    private fun createNotification(context: Context, text: String): Notification {
        val intent = Intent(context, SessionActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        builder.setContentTitle(context.getString(R.string.app_name))
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentIntent(pendingIntent)
        builder.setContentText(text)
        return builder.build()
    }

    private fun sendNotification(context: Context) {
        val text = context.getString(R.string.alarm_timeout_text)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager?.createNotificationChannel(channel)
        }
        val notification = createNotification(context, text)
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

}