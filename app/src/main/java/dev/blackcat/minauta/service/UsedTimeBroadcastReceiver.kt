package dev.blackcat.minauta.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.blackcat.minauta.R
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.service.UsedTimeService

class UsedTimeBroadcastReceiver(val service: UsedTimeService) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { intent ->

            if (intent.action.equals(UsedTimeService.END_SESSION_ACTION)) {
                service.stopRunning()
            }

        }
    }

}