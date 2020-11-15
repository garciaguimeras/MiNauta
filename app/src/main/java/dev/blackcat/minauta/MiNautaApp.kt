package dev.blackcat.minauta

import android.app.Application

class MiNautaApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val setup = AppSetup(applicationContext)
        setup.convertAccountsFrom_v1_6()
    }

}