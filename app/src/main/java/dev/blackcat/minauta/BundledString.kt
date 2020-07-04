package dev.blackcat.minauta

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
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