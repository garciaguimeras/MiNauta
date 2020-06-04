package dev.blackcat.minauta.sync

import android.util.Log

object SyncThread {

    fun <T> execute(runnable: SyncRunnable<T>?): T? {
        if (runnable == null)
            return null

        try {
            val thread = Thread(runnable)
            Log.i(SyncThread::class.java.toString(), "Calling thread.start")
            thread.start()
            Log.i(SyncThread::class.java.toString(), "Calling thread.join")
            thread.join()
            Log.i(SyncThread::class.java.toString(), "Thread returned, getting results")
            return runnable.result
        } catch (e: Exception) {
            Log.i(SyncThread::class.java.toString(), "Error on thread.join")
            e.printStackTrace()
        }

        return null
    }

}
