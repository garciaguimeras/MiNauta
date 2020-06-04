package dev.blackcat.minauta.ui.session

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import dev.blackcat.minauta.R
import dev.blackcat.minauta.async.SessionAvailableTimeAsyncTask
import dev.blackcat.minauta.async.SessionLogoutAsyncTask
import dev.blackcat.minauta.async.SessionUsedTimeTask
import dev.blackcat.minauta.data.store.PreferencesStore
import dev.blackcat.minauta.net.Connection
import dev.blackcat.minauta.ui.MyViewModel

class SessionViewModel(application: Application) : MyViewModel(application) {

    var availableTime: MutableLiveData<String> = MutableLiveData()
    var usedTime: MutableLiveData<String> = MutableLiveData()
    var logoutResult: MutableLiveData<Connection.LogoutResult> = MutableLiveData()

    fun startSession(activity: Activity) {
        val availableTimeText = activity.getString(R.string.available_time_text)
        SessionAvailableTimeAsyncTask(activity, object: SessionAvailableTimeAsyncTask.OnTaskResult {
            override fun onResult(result: Connection.AvailableTimeResult) {
                if (result.state != Connection.State.OK)
                    availableTime.postValue("${availableTimeText} --:--:--")
                else
                    availableTime.postValue("${availableTimeText} ${result.availableTime}")
            }
        }).execute()

        val usedTimeText = activity.getString(R.string.used_time_text)
        SessionUsedTimeTask(activity, object: SessionUsedTimeTask.OnTaskResult {
            override fun onResult(time: String) {
                usedTime.postValue("${usedTimeText} ${time}")
            }
        }).execute()
    }

    fun closeSession(activity: Activity) {
        SessionLogoutAsyncTask(activity, object: SessionLogoutAsyncTask.OnTaskResult {
            override fun onResult(result: Connection.LogoutResult) {
                logoutResult.postValue(result)
            }
        }).execute()
    }

    fun finishSession(activity: Activity) {
        PreferencesStore(activity).setSession("", 0)
        activity.finish()
    }

}