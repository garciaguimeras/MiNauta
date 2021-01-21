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
import dev.blackcat.minauta.R
import dev.blackcat.minauta.net.Connection
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

                val isScreenOn = PowerUtil.isScreenOn(context)
                Log.i("==>", "Screen is ${if (isScreenOn) "on" else "off"}")

                if (!isScreenOn) {
                    PowerUtil.acquireWakeLock(context)
                }
                // sendNotification(context, R.string.alarm_timeout_text)

                // TODO: How to fix the delay?
                delay(SessionActivity.ALARM_DELAY)

                val sessionUtil = SessionUtil(context)
                val result = sessionUtil.logout()
                result?.let { result ->
                    if (result.state == Connection.State.OK) {
                        sessionUtil.cancelJob()
                        Log.i("==>", "Cerrado!")
                        sendNotification(context, R.string.logout_ok)
                    }
                    else {
                        Log.i("==>", "No se pudo cerrar la sesion")
                        sendNotification(context, R.string.logout_error)
                    }
                }

                result?.let { result ->
                    val broadcastIntent = Intent(SessionActivity.SESSION_CLOSED_ACTION)
                    broadcastIntent.putExtra(SessionActivity.LOGOUT_RESULT, result)
                    context.sendBroadcast(broadcastIntent)
                }

                if (!isScreenOn) {
                    PowerUtil.releaseWakeLock()
                }
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

    private fun sendNotification(context: Context, textResId: Int) {
        val text = context.getString(textResId)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager?.createNotificationChannel(channel)
        }
        val notification = createNotification(context, text)
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

}