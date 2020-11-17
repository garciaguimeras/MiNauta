package dev.blackcat.minauta

import android.content.Context
import dev.blackcat.minauta.util.PreferencesStore

class AppSetup(val context: Context) {

    fun migrateAccountsFrom_v1_6() {
        val preferencesStore = PreferencesStore(context)
        val oldAccount = preferencesStore.getOldAccountKeysAsStoredAccount()
        if (oldAccount != null) {
            preferencesStore.setStoredAccounts(listOf(oldAccount))
            preferencesStore.removeOldAccountKeys()
        }
    }

}