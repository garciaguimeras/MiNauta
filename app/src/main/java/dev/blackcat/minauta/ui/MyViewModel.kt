package dev.blackcat.minauta.ui

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dev.blackcat.minauta.BundledString
import dev.blackcat.minauta.PreferencesStore
import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.service.SessionService



open class MyViewModelHandler(open val viewModel: MyViewModel) : Handler() {
    override fun handleMessage(msg: Message) {
        when (msg.what) {
            SessionService.SND_RUNNING_MESSAGE -> {
                val running = BundledString.toObject(msg.data, String::class.java).toBoolean()
                viewModel.isServiceRunning.postValue(running)
            }
            else -> { super.handleMessage(msg) }
        }
    }
}

open class MyViewModel(application: Application) : AndroidViewModel(application) {

    val preferencesStore = PreferencesStore(application.applicationContext)


    var account: MutableLiveData<Account> = MutableLiveData()
    val isServiceRunning = MutableLiveData<Boolean>()


    private lateinit var incomingMessenger: Messenger
    protected var outgoingMessenger: Messenger? = null
    private lateinit var serviceConnection: ServiceConnection

    open fun getMessengerHandler(): Handler? {
        return null
    }

    fun checkAccount() {
        account.postValue(preferencesStore.account)
    }

    fun bindService(activity: Activity) {
        serviceConnection = createServiceConnection()
        val intent = Intent(activity, SessionService::class.java)
        activity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(activity: Activity) {
        activity.unbindService(serviceConnection)
    }

    fun sendAddMessenger() {
        val handler = getMessengerHandler()
        outgoingMessenger?.let { messenger ->
            incomingMessenger = Messenger(handler)
            val msg = Message.obtain()
            msg.what = SessionService.REC_ADD_MESSENGER_MESSAGE
            msg.replyTo = incomingMessenger
            messenger.send(msg)
        }
    }

    private fun createServiceConnection() : ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                outgoingMessenger = Messenger(binder)
                sendAddMessenger()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                outgoingMessenger = null
            }
        }
    }

}