package dev.blackcat.minauta.ui.session

import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Message
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.blackcat.minauta.BundledString
import dev.blackcat.minauta.data.Session
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.net.ConnectionManager
import dev.blackcat.minauta.service.SessionService
import dev.blackcat.minauta.ui.MyViewModel
import dev.blackcat.minauta.ui.MyViewModelHandler


class SessionViewModelFactory(val activity: SessionActivity) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SessionViewModel(activity) as T
    }

}

class SessionViewModelHandler(override val viewModel: SessionViewModel) : MyViewModelHandler(viewModel) {
    override fun handleMessage(msg: Message) {
        when (msg.what) {
            SessionService.SND_LOGIN_RESULT_MESSAGE -> {
                val result = BundledString.toObject(msg.data, Connection.LoginResult::class.java)
                viewModel.loginResult.postValue(result)
            }
            SessionService.SND_AVAILABLE_TIME_MESSAGE -> {
                val result = BundledString.toObject(msg.data, Connection.AvailableTimeResult::class.java)
                viewModel.availableTime.postValue(result)
            }
            SessionService.SND_USED_TIME_MESSAGE -> {
                val time = BundledString.toObject(msg.data, String::class.java)
                viewModel.usedTime.postValue(time)
            }
            SessionService.SND_LOGOUT_RESULT_MESSAGE -> {
                val result = BundledString.toObject(msg.data, Connection.LogoutResult::class.java)
                viewModel.logoutResult.postValue(result)
            }
            else -> { super.handleMessage(msg) }
        }
    }
}

class SessionViewModel(val activity: SessionActivity) : MyViewModel(activity.application) {


    val loginResult = MutableLiveData<Connection.LoginResult>()
    val availableTime = MutableLiveData<Connection.AvailableTimeResult>()
    val usedTime = MutableLiveData<String>()
    val logoutResult = MutableLiveData<Connection.LogoutResult>()

    override fun getMessengerHandler(): Handler? {
        return SessionViewModelHandler(this)
    }

    fun startService(activity: SessionActivity) {
        val intent = Intent(activity, SessionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.startForegroundService(intent)
        }
        else {
            activity.startService(intent)
        }
    }

    fun sendCloseSession() {
        outgoingMessenger?.let { messenger ->
            val msg = Message.obtain()
            msg.what = SessionService.REC_STOP_MESSAGE
            messenger.send(msg)
        }
    }

    fun retryCloseSession(session: Session) {
        val connectionManager = ConnectionManager(preferencesStore.account)
        val result = connectionManager.logout(session)
        logoutResult.postValue(result)
    }

}