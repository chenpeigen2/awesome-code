package org.peter.immutable.demo

import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentHashSetOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList

/**
 * 第二部分：修改一个不可变集合的四种姿势。
 *
 * - `+` / `-`：最常用，返回同类型的新集合；
 * - `mutate { }`：批量修改时用，内部只构造一次 builder；
 * - `builder()`：需要跨多次调用半成品时用；
 * - `put` / `add`：单点更新，同样返回新集合。
 *
 * 另外对比 ordered 与 hash 两套实现：前者保插入顺序，后者更快但不保序。
 */
object OperationsDemo {

    fun run() {
        val base = persistentListOf("apple", "banana")
        line("base", base)

        // + / -：导入 kotlinx.collections.immutable.plus 后才会返回 Persistent 类型
        val longer = base + "cherry"
        line("base + cherry", "$longer (${typeName(longer)})")
        line("base - apple", base - "apple")
        line("base 从未被修改", base)

        // mutate：内部一次 builder，多次操作用它最省
        val mutated = base.mutate {
            it.add("cherry")
            it.remove("apple")
            it.set(0, "APPLE")
        }
        line("mutate { add/remove/set }", mutated)
        line("mutate 无改动 === 原对象", base.mutate { } === base)

        // builder：适合“慢慢攒”的场景
        val builder = base.builder()
        val snapshot = builder.build()
        builder.add("cherry")
        line("builder 先 build 出快照", snapshot)
        line("之后继续改 builder", builder.build())
        line("快照不受影响", snapshot)

        // 单点更新：map 的 put 返回新版本
        val map = persistentMapOf("a" to 1)
        line("map.put(b, 2)", "${map.put("b", 2)}，原 map = $map")
        line("重复 put 保留原位置", persistentMapOf("a" to 1, "b" to 2).put("a", 9))

        // ordered 版保插入顺序，hash 版不保序但等价
        line("persistentMapOf 迭代顺序", persistentMapOf("z" to 1, "a" to 2, "m" to 3))
        line("persistentHashMapOf 迭代顺序", persistentHashMapOf("z" to 1, "a" to 2, "m" to 3))
        line("persistentSetOf 迭代顺序", persistentSetOf("b", "a", "c"))
        line("persistentHashSetOf 迭代顺序", persistentHashSetOf("b", "a", "c"))
        line("两套实现集合语义相等", persistentSetOf("b", "a", "c") == persistentHashSetOf("b", "a", "c"))
        line("ordered 删除后重加成最后一位", persistentSetOf("a", "b", "c").remove("a").add("a"))

        // 标准库变换会“掉回”普通 List，需要显式收回不可变类型
        val filtered = base.filter { it.length > 5 }
        line("base.filter { ... } 返回类型", typeName(filtered))
        line("toPersistentList() 收回", "${filtered.toPersistentList()} (${typeName(filtered.toPersistentList())})")

        // 常见的“整表过滤/映射”更适合 mutate：一次 builder 完成
        val evenSquares = persistentListOf(1, 2, 3, 4, 5, 6).mutate { builder ->
            builder.removeIf { it % 2 == 1 }
            builder.replaceAll { it * it }
        }
        line("mutate 里 removeIf + replaceAll", evenSquares)
    }
}
