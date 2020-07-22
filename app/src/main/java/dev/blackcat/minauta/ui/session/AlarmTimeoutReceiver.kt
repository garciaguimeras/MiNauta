package dev.blackcat.minauta.ui.session

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlarmTimeoutReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { context ->
            CoroutineScope(Dispatchers.Default).launch {
                val activityIntent = Intent(context, SessionActivity::class.java)
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activityIntent)

                delay(2000)

                val broadcastIntent = Intent(SessionActivity.CLOSE_SESSION_ACTION)
                context.sendBroadcast(broadcastIntent)
            }
        }
    }

}