package org.peter.lambda

fun higherFunc(sum: (x: Int, y: Int) -> Int) {
    println(sum(1, 2))
}

fun main() {
    val sum = { x: Int, y: Int -> x + y }
    higherFunc { x, y -> x + y }
}