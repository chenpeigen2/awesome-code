package org.peter.types

import com.google.gson.internal.bind.TypeAdapters.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
fun main() {
    val a = 123

    val b = "123"

    println(a.javaClass)
    println(a::class.javaPrimitiveType)
    println(a::class.javaObjectType)
    println(a::class.java)

    println(Uuid.generateV4().toString())
    println(Uuid.generateV7().toString())
    println(Uuid.random().toString())
}