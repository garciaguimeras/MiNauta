package dev.blackcat.minauta.util

import android.os.Bundle
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.Serializable

class BundledString {

    companion object {
        fun fromObject(obj: Serializable): Bundle {
            val objectMapper = ObjectMapper()
            val str = objectMapper.writeValueAsString(obj)

            val bundle = Bundle()
            bundle.putString(obj.javaClass.name, str)
            return bundle
        }

        fun <T> toObject(bundle: Bundle, clazz: Class<T>): T {
            val value = bundle.getString(clazz.name) ?: ""

            val objectMapper = ObjectMapper()
            return objectMapper.readValue(value, clazz)
        }
    }

}