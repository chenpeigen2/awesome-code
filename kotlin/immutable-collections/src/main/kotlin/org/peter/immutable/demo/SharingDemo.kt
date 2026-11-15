package org.peter.immutable.demo

import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList

/**
 * 第三部分：结构共享（structural sharing）到底省在哪。
 *
 * 持久化集合不是“每次改都复制一整份”，而是只重建从根到被改叶子的那条路径，
 * 其余子树被新旧两个版本共享。所以：
 *
 * - N 次追加的总代价是 O(N log N)，而不是复制版 O(N²)；
 * - 在 100 万元素上追加一个元素，只需 O(log N) 次节点分配；
 * - 任意历史版本都可以安全保留（这正是 undo / 时间旅行便宜的原因）。
 */
object SharingDemo {

    fun run() {
        // 版本 1 → 版本 2，v1 不会被改动
        val v1 = persistentListOf(1, 2, 3)
        val v2 = v1 + 4
        val v3 = v2.mutate { it.set(0, 100) }
        line("v1", v1)
        line("v2 = v1 + 4", v2)
        line("v3 = v2.mutate { set(0,100) }", v3)
        line("三个版本互不影响", "v1=$v1 v2=$v2 v3=$v3")

        // 同一份逻辑：persistent 追加 vs 普通 List 复制追加
        val n = 50_000
        lateinit var persistent: List<Int>
        val persistentMs = timed {
            var acc = persistentListOf<Int>()
            repeat(n) { acc = acc + it }
            persistent = acc
        }
        lateinit var copying: List<Int>
        val copyingMs = timed {
            var acc = listOf<Int>()
            repeat(n) { acc = acc + it }
            copying = acc
        }
        line("$n 次追加：persistentListOf", "%.2f ms → %d 个元素".format(persistentMs, persistent.size))
        line("$n 次追加：listOf + 每次复制", "%.2f ms → %d 个元素".format(copyingMs, copying.size))
        line("提速倍数", "%.0fx".format(copyingMs / persistentMs.coerceAtLeast(0.001)))

        // 单次追加：100 万元素上也几乎是常数时间
        val big = (0 until 1_000_000).toPersistentList()
        val bigCopy = (0 until 1_000_000).toList()
        repeat(100) { (big + 42).size } // 预热，避免把 JIT 编译时间算进来
        var checksum = 0
        val persistentAppendNanos = bestNanos(5) { checksum += (big + 42).size }
        val copyingAppendNanos = bestNanos(5) { checksum += (bigCopy + 42).size }
        line("两边结果一致（防止被 JIT 优化掉）", checksum == 10_000_010)
        line("100 万元素追加一个元素：persistent", "%.1f µs".format(persistentAppendNanos / 1000.0))
        line("100 万元素追加一个元素：listOf +", "%.1f µs".format(copyingAppendNanos / 1000.0))

        // 结构共享的“副产品”：可以随便留快照
        val versions = mutableListOf<Int>()
        var current = persistentListOf<Int>()
        repeat(5) {
            current = current + (it * 10)
            versions += current.sum() // 只留摘要，避免打印太长
        }
        line("保留 5 个历史版本", versions)
    }

    private fun bestOf(times: Int, block: () -> Unit): Double =
        (1..times).minOf { timed(block) }

    private fun bestNanos(times: Int, block: () -> Unit): Long =
        (1..times).minOf {
            val start = System.nanoTime()
            block()
            System.nanoTime() - start
        }
}
