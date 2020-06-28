package dev.blackcat.minauta.ui.session

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.blackcat.minauta.R
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.service.UsedTimeService

class SessionBroadcastReceiver(val viewModel: SessionViewModel) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { intent ->

            if (intent.action.equals(UsedTimeService.TIMER_TICK_ACTION)) {
                val time = intent.getStringExtra(UsedTimeService.TIME) ?: ""
                viewModel.setTimerTick(time)
            }

        }
    }

}