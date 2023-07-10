package org.peter.lambda

fun higherFunc(sum: (x: Int, y: Int) -> Int) {
    println(sum(5, 2))
}

fun main() {
    val sum = { x: Int, y: Int -> x + y }
    sum.invoke(1, 2)
    higherFunc { x, y -> x + y }
    higherFunc(fun(x: Int, y: Int): Int {
        return x * y
    })
}