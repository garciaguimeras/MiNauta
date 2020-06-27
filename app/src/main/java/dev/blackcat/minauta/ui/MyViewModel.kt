package dev.blackcat.minauta.ui

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.PreferencesStore

open class MyViewModel(application: Application) : AndroidViewModel(application) {

    var account: MutableLiveData<Account> = MutableLiveData()

    fun checkAccount(activity: Activity) {
        account.postValue(PreferencesStore(activity).account)
    }

}