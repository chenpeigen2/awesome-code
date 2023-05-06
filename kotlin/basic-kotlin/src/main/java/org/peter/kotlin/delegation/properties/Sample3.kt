package org.peter.kotlin.delegation.properties

import kotlin.reflect.KProperty

/**
 * kotlin reflect
 */

// Property delegates don't have to implement an interface, but they have to provide a getValue() function (and setValue() for vars).
class Delegate3 {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }
}

class Example {
    var p: String by Delegate3()
}

fun main() {
//    当我们从委托到一个 Delegate 实例的 p 读取时，将调用 Delegate 中的 getValue() 函数， 所以它第一个参数是读出 p 的对象、第二个参数保存了对 p 自身的描述 （例如你可以取它的名字)。 例如:
    val e = Example()
    e.p = "NEW"
    println(e.p)
}