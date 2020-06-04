package dev.blackcat.minauta.net

object ConnectionFactory {

    enum class Type {
        FAKE,
        JNAUTA
    }

    fun <T : Connection> createSessionProducer(clazz: Class<T>): T? {
        try {
            return clazz.newInstance()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}
