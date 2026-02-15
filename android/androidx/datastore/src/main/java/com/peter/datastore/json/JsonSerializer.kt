package com.peter.datastore.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object JsonSerializer {
    val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }
    
    inline fun <reified T> encode(value: T): String {
        return json.encodeToString(value)
    }
    
    inline fun <reified T> decode(string: String): T {
        return json.decodeFromString(string)
    }
    
    fun <T> encode(serializer: KSerializer<T>, value: T): String {
        return json.encodeToString(serializer, value)
    }
    
    fun <T> decode(serializer: KSerializer<T>, string: String): T {
        return json.decodeFromString(serializer, string)
    }
}
