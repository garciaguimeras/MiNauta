package dev.blackcat.minauta

import android.app.Application

class MiNautaApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val setup = AppSetup(applicationContext)
        setup.migrateAccountsFrom_v1_6()
    }

}