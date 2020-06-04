package dev.blackcat.minauta.async

import android.content.Context
import android.os.AsyncTask

import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.store.PreferencesStore
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.net.ConnectionFactory
import dev.blackcat.minauta.net.JNautaConnection

class SessionAvailableTimeAsyncTask(internal var context: Context, internal var onTaskResult: OnTaskResult?) : AsyncTask<Void, Void, Connection.AvailableTimeResult>() {

    interface OnTaskResult {
        fun onResult(result: Connection.AvailableTimeResult)
    }

    override fun doInBackground(vararg params: Void): Connection.AvailableTimeResult {
        val store = PreferencesStore(this.context)
        val account = store.account

        val connection = ConnectionFactory.createSessionProducer(JNautaConnection::class.java)

        return connection!!.getAvailableTime(account!!)
    }

    override fun onPostExecute(result: Connection.AvailableTimeResult) {
        if (onTaskResult != null)
            onTaskResult!!.onResult(result)
    }
}
