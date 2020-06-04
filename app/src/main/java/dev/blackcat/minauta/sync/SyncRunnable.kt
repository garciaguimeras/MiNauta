package dev.blackcat.minauta.sync

abstract class SyncRunnable<T> : Runnable {
    var result: T? = null

    fun execute(): T? {
        return SyncThread.execute(this)
    }
}
