package dev.blackcat.minauta.net

import dev.blackcat.minauta.data.Account
import dev.blackcat.minauta.data.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.*

class ConnectionManager(val account: Account) {

    val connection = ConnectionFactory.createSessionProducer(JNautaConnection::class.java)!!
    //val connection = ConnectionFactory.createSessionProducer(FakeConnection::class.java)!!

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