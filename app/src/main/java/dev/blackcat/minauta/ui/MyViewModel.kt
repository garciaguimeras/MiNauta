package dev.blackcat.minauta.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dev.blackcat.minauta.MiNautaApp
import dev.blackcat.minauta.PreferencesStore
import dev.blackcat.minauta.data.Account

open class MyViewModel(application: Application) : AndroidViewModel(application) {

    val preferencesStore = PreferencesStore(application.applicationContext)
    var account: MutableLiveData<Account> = MutableLiveData()

    fun checkAccount() {
        account.postValue(preferencesStore.account)
    }

}