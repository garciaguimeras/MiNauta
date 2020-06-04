package dev.blackcat.minauta.async

import android.content.Context
import android.os.AsyncTask

import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.store.PreferencesStore
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.net.ConnectionFactory
import dev.blackcat.minauta.net.JNautaConnection

class SessionLogoutAsyncTask(internal var context: Context, internal var onTaskResult: OnTaskResult?) : AsyncTask<Void, Void, Connection.LogoutResult>() {

    interface OnTaskResult {
        fun onResult(result: Connection.LogoutResult)
    }

    override fun doInBackground(vararg params: Void): Connection.LogoutResult {
        val store = PreferencesStore(this.context)
        val account = store.account

        val connection = ConnectionFactory.createSessionProducer(JNautaConnection::class.java)

        return connection!!.logout(account!!)
    }

    override fun onPostExecute(result: Connection.LogoutResult) {
        if (onTaskResult != null)
            onTaskResult!!.onResult(result)
    }
}
