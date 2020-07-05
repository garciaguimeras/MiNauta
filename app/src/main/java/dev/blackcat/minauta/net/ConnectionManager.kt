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

    fun login(): Connection.LoginResult {
        val scope = CoroutineScope(Dispatchers.IO).async {
            val result = connection.login(account)
            return@async result
        }

        var result: Connection.LoginResult? = null
        runBlocking {
            result = scope.await()
        }
        return result!!
    }

    fun getAvailableTime(session: Session): Connection.AvailableTimeResult {
        val scope = CoroutineScope(Dispatchers.IO).async {
            val result = connection.getAvailableTime(account, session)
            return@async result
        }

        var result: Connection.AvailableTimeResult? = null
        runBlocking {
            result = scope.await()
        }
        return result!!
    }

    fun logout(session: Session): Connection.LogoutResult {
        val scope = CoroutineScope(Dispatchers.IO).async {
            val result = connection.logout(account, session)
            return@async result
        }

        var result: Connection.LogoutResult? = null
        runBlocking {
            result = scope.await()
        }
        return result!!
    }

}