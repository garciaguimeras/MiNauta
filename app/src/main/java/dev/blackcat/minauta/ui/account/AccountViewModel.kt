package dev.blackcat.minauta.ui.account

import android.app.Activity
import android.app.Application
import dev.blackcat.minauta.data.store.PreferencesStore
import dev.blackcat.minauta.ui.MyViewModel

class AccountViewModel(application: Application) : MyViewModel(application) {

    fun setAccountInfo(activity: Activity, username: String, password: String) {
        val user = if (username == "") "" else (if (username.contains("@")) username else "${username}@nauta.com.cu")
        PreferencesStore(activity).setAccount(user, password)
        activity.finish()
    }

}