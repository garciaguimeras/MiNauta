package dev.blackcat.minauta.net

object ConnectionFactory {

    enum class Type {
        JNAUTA,
        FAKE,
        FAKE_NO_LOGIN,
        FAKE_NO_LOGOUT
    }

    fun create(type: Type): Connection {
        return when (type) {
            Type.JNAUTA -> { JNautaConnection::class.java.newInstance() }
            Type.FAKE -> { FakeConnection::class.java.newInstance() }
            Type.FAKE_NO_LOGIN -> { FakeNoLoginConnection::class.java.newInstance() }
            Type.FAKE_NO_LOGOUT -> { FakeNoLogoutConnection::class.java.newInstance() }
        }
    }

    fun create(): Connection {
        return create(Type.FAKE)
    }
}
