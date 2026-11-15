package org.peter.immutable.demo

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList

/**
 * 第一部分：类型体系与三个“日常坑”。
 *
 * 1. `ImmutableList` / `ImmutableMap` / `ImmutableSet` 是**只读契约**；
 * 2. `Persistent*` 在其上增加了 `add` / `put` / `mutate` 等“返回新版本”的操作；
 * 3. Kotlin 标准库的 `listOf()` 只是**只读视图**，不是不可变，`is ImmutableList` 为 false。
 */
object BasicsDemo {

    fun run() {
        val list: PersistentList<String> = persistentListOf("Java", "Kotlin", "Go")
        val set: PersistentSet<String> = persistentSetOf("b", "a", "c")
        val map: PersistentMap<String, Int> = persistentMapOf("a" to 1, "b" to 2)

        line("persistentListOf", list)
        line("persistentSetOf", set)
        line("persistentMapOf", map)

        line("list is List", list is List<*>)
        line("list is ImmutableList", list is ImmutableList<*>)
        line("list is PersistentList", list is PersistentList<*>)
        line("set is ImmutableSet / map is ImmutableMap", "${set is ImmutableSet<*>} / ${map is ImmutableMap<*, *>}")

        line("类型名", typeName(list))

        // 坑 1：标准库的只读集合并不是 Immutable*
        val readOnly: List<Int> = listOf(1, 2, 3)
        line("listOf(1,2,3) is ImmutableList", readOnly is ImmutableList<*>)
        line("toImmutableList() 返回新实例", readOnly.toImmutableList() !== readOnly)

        // 坑 2：toImmutableList 会冻结，可变源后续修改不影响结果
        val mutable = mutableListOf("a", "b")
        val frozen = mutable.toImmutableList()
        mutable.add("c")
        line("冻结后源再改：frozen / source", "$frozen / $mutable")

        // 坑 3：本身就是不可变时，转换零拷贝，原样返回
        line("persistentList.toImmutableList() === 自己", list.toImmutableList() === list)

        // 值得放心的两点：相等性仍按集合语义、空集合是共享单例
        line("persistentListOf(1,2,3) == listOf(1,2,3)", persistentListOf(1, 2, 3) == listOf(1, 2, 3))
        line("空 list/set 是单例", "${persistentListOf<Int>() === persistentListOf<Int>()} / ${persistentSetOf<Int>() === persistentSetOf<Int>()}")

        // 不可变是真的改不动
        val hacked = runCatching {
            @Suppress("UNCHECKED_CAST")
            (list as MutableList<String>).add("Rust")
        }
        line("强转成 MutableList 再 add", hacked.exceptionOrNull()?.let { "被拒绝：${it::class.simpleName}" } ?: "居然成功了?!")
        line("list 依然", list)
    }
}
