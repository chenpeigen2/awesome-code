package org.peter.kotlin.delegation

interface Base1 {
    fun printMessage()
    fun printMessageLine()
}

class BaseImpl1(val x: Int) : Base1 {
    override fun printMessage() {
        print(x)
    }

    override fun printMessageLine() {
        println(x)
    }
}

class Derived1(b: Base1) : Base1 by b {
    override fun printMessage() {
        println("abc")
    }
}

fun main() {
    val b = BaseImpl1(12)
    val b1 = Derived1(b)

    b1.printMessage()
    b1.printMessageLine()
}