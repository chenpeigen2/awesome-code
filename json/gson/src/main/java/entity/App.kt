package entity

fun main() {
    val test = Test<Int>()
    val cls = Test::class.java
    try {
        val method = cls.getDeclaredMethod("setData", Any::class.java)
        method.invoke(test, "aaa")
    } catch (e: Exception) {
        e.printStackTrace()
    }
    val result = test.data
    println("mmp" + "测试反射泛型的结果: $result")
}