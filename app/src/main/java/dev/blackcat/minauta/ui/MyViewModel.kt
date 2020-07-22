package dev.blackcat.minauta.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dev.blackcat.minauta.PreferencesStore
import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.Session


open class MyViewModel(application: Application) : AndroidViewModel(application) {

    val preferencesStore = PreferencesStore(application.applicationContext)

    var accountObservable = MutableLiveData<Account>()
    var sessionObservable = MutableLiveData<Session?>()

    fun checkAccount() {
        accountObservable.postValue(preferencesStore.account)
    }

    fun checkSession() {
        sessionObservable.postValue(preferencesStore.session)
    }

}