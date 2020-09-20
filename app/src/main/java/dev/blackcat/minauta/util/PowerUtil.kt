package dev.blackcat.minauta.util

import android.content.Context
import android.os.Build
import android.os.PowerManager

class PowerUtil {

    companion object {

        const val TAG = "minauta:wakeLockTag"

        var wakeLock: PowerManager.WakeLock? = null

        fun isScreenOn(context: Context): Boolean {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) powerManager.isInteractive
                   else powerManager.isScreenOn
        }

        fun acquireWakeLock(context: Context) {
            wakeLock?.release()

            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val flags = PowerManager.PARTIAL_WAKE_LOCK // or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE
            wakeLock = powerManager.newWakeLock(flags, TAG)
            wakeLock?.acquire()
        }

        fun releaseWakeLock() {
            wakeLock?.let { wakeLock ->
                wakeLock.release()
            }
            wakeLock = null
        }

    }

}