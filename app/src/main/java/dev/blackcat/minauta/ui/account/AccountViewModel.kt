package dev.blackcat.minauta.ui.account

import android.app.Application
import androidx.lifecycle.MutableLiveData
import dev.blackcat.minauta.data.StoredAccount
import dev.blackcat.minauta.ui.MyViewModel
import dev.blackcat.minauta.util.PreferencesStore
import java.util.*

class AccountViewModel(application: Application) : MyViewModel(application) {

    val accountsLiveData = MutableLiveData<List<StoredAccount>>()

    fun getStoredAccounts(): List<StoredAccount> {
        val preferenceStore = PreferencesStore(getApplication())
        return preferenceStore.getStoredAccounts()
    }

    fun getStoredAccount(position: Int): StoredAccount? {
        val preferenceStore = PreferencesStore(getApplication())
        val accounts = preferenceStore.getStoredAccounts()
        if (position < 0 || position >= accounts.size) return null
        return accounts[position]
    }

    fun setStoredAccounts(accounts: List<StoredAccount>) {
        val preferenceStore = PreferencesStore(getApplication())
        preferenceStore.setStoredAccounts(accounts)
    }

    fun addStoredAccount(account: StoredAccount) {
        val accounts = mutableListOf<StoredAccount>()
        accounts.addAll(getStoredAccounts())
        accounts.add(account)
        setStoredAccounts(accounts)

        accountsLiveData.postValue(accounts)
    }

    fun updateStoredAccount(position: Int, account: StoredAccount) {
        val accounts = mutableListOf<StoredAccount>()
        accounts.addAll(getStoredAccounts())
        accounts[position] = account
        setStoredAccounts(accounts)

        accountsLiveData.postValue(accounts)
    }

    fun removeStoredAccount(position: Int) {
        val accounts = mutableListOf<StoredAccount>()
        accounts.addAll(getStoredAccounts())
        accounts.removeAt(position)
        setStoredAccounts(accounts)

        accountsLiveData.postValue(accounts)
    }

    fun selectStoredAccount(position: Int) {
        val accounts = mutableListOf<StoredAccount>()
        accounts.addAll(getStoredAccounts())
        accounts.firstOrNull { a -> a.selected }?.let { account ->
            account.selected = false
        }
        accounts[position].selected = true
        setStoredAccounts(accounts)

        accountsLiveData.postValue(accounts)
    }

}