package dev.blackcat.minauta.net

import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class ConnectionManager(val account: Account) {

    private val connection = ConnectionFactory.create()

    fun login(): Connection.LoginResult =
            runBlocking {
                val result = CoroutineScope(Dispatchers.IO).async {
                    connection.login(account)
                }
                return@runBlocking result.await()
            }

    fun getAvailableTime(session: Session): Connection.AvailableTimeResult =
            runBlocking {
                val result = CoroutineScope(Dispatchers.IO).async {
                    connection.getAvailableTime(account, session)
                }
                return@runBlocking result.await()
            }

    fun logout(session: Session): Connection.LogoutResult =
            runBlocking {
                val result = CoroutineScope(Dispatchers.IO).async {
                    connection.logout(account, session)
                }
                return@runBlocking result.await()
            }

}