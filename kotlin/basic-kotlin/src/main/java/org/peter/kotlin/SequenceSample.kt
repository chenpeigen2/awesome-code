package org.peter.kotlin

fun main() {
    val numbersSequence = sequenceOf("one", "two", "three", "four")
    println(numbersSequence)

    // 如果已经有一个 Iterable 对象（例如 List 或 Set），则可以通过调用 asSequence() 从而创建一个序列。
    val numbers = listOf("one", "tow", "three", "flow")
    print(numbers.asSequence())

    //    由函数
    val oddNumbersLessThan10 = generateSequence(1) { if (it + 2 < 10) it + 2 else null }
    println(oddNumbersLessThan10.count())

    //    由组块
    val oddNumbers = sequence {
        yield(1)
        yieldAll(listOf(3, 5))
        yieldAll(generateSequence(7) { it + 2 })
    }
    println(oddNumbers.take(1).toList())

    withoutSequence()

    withSequence()

}


fun withoutSequence() {
    val words = "The quick brown fox jumps over the lazy dog".split(" ") // return a list
    val lengthList = words.filter { println("filter $it"); it.length > 3 }.map {
        println(it);
        it.length
    }.take(4)
}

fun withSequence() {
    val words = "The quick brown fox jumps over the lazy dog".split(" ")
    val wordsSequence = words.asSequence()
    val lengthsSequence = wordsSequence.filter { println("filter: $it"); it.length > 3 }
        .map { println("length: ${it.length}"); it.length }
        .take(4).sum()

    println("Lengths of first 4 words longer than 3 chars")
// 末端操作：以列表形式获取结果。
    println(lengthsSequence)
}