package org.peter.immutable

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * 这些断言全部来自 kotlinx.collections.immutable 的 README / KDoc 承诺，
 * 用来把 demo 里讲解的“契约”钉死，避免库升级后 demo 讲错。
 */
class CollectionsContractTest {

    @Test
    fun `persistent collections are immutable views and keep stdlib equality`() {
        val list = persistentListOf(1, 2, 3)
        val set = persistentSetOf(1, 2, 3)
        val map = persistentMapOf("a" to 1)

        assertTrue(list is PersistentList<*>)
        assertTrue(list is ImmutableList<*>)
        assertTrue(set is ImmutableSet<*>)
        assertTrue(map is ImmutableMap<*, *>)

        assertEquals(listOf(1, 2, 3), list)
        assertEquals(setOf(1, 2, 3), set)
        assertEquals(mapOf("a" to 1), map)
    }

    @Test
    fun `converting to the same immutable type returns the very same instance`() {
        val persistent = persistentListOf(1, 2)

        assertSame(persistent, persistent.toPersistentList())
        assertSame(persistent, persistent.toImmutableList())
    }

    @Test
    fun `toImmutableList freezes a mutable source`() {
        val source = mutableListOf(1, 2)
        val frozen = source.toImmutableList()

        source.add(3)

        assertEquals(listOf(1, 2), frozen)
        assertTrue(frozen is ImmutableList<*>)
    }

    @Test
    fun `plus and minus return persistent collections and never touch the receiver`() {
        val base = persistentListOf(1, 2, 3)

        val longer = base + 4
        val shorter = base - 2

        assertTrue(longer is PersistentList<*>)
        assertEquals(listOf(1, 2, 3, 4), longer)
        assertEquals(listOf(1, 3), shorter)
        assertEquals(listOf(1, 2, 3), base)
    }

    @Test
    fun `put on a map returns a new map and leaves the old readable`() {
        val before = persistentMapOf("a" to 1)

        val after = before.put("b", 2)

        assertEquals(mapOf("a" to 1), before)
        assertEquals(mapOf("a" to 1, "b" to 2), after)
    }

    @Test
    fun `ordered variants keep insertion order and hash variants only keep set equality`() {
        val ordered = persistentMapOf("z" to 1, "a" to 2, "m" to 3)
        assertEquals(listOf("z", "a", "m"), ordered.keys.toList())

        val orderedSet = persistentSetOf("b", "a", "c")
        assertEquals(listOf("b", "a", "c"), orderedSet.toList())

        val hashed = persistentHashMapOf("z" to 1, "a" to 2, "m" to 3)
        assertEquals(setOf("z", "a", "m"), hashed.keys)
    }

    @Test
    fun `re-putting an existing key of an ordered map keeps its original position`() {
        val map = persistentMapOf("a" to 1, "b" to 2)

        val updated = map.put("a", 9)

        assertEquals(listOf("a", "b"), updated.keys.toList())
        assertEquals(mapOf("a" to 9, "b" to 2), updated)
    }

    @Test
    fun `mutate without modifications reuses the receiver`() {
        val base = persistentListOf(1, 2)

        assertSame(base, base.mutate { })
    }

    @Test
    fun `mutate returns a new collection and leaves the old one usable`() {
        val base = persistentListOf(1, 2)

        val changed = base.mutate { it.add(3) }

        assertEquals(listOf(1, 2), base)
        assertEquals(listOf(1, 2, 3), changed)
    }

    @Test
    fun `a built builder snapshot is isolated from later builder writes`() {
        val builder = persistentListOf(1, 2).builder()
        val snapshot = builder.build()

        builder.add(3)

        assertEquals(listOf(1, 2), snapshot)
        assertEquals(listOf(1, 2, 3), builder.build())
    }

    @Test
    fun `empty persistent collections are shared singletons`() {
        assertSame(persistentListOf<Int>(), persistentListOf<Int>())
        assertSame(persistentSetOf<Int>(), persistentSetOf<Int>())
    }

    @Test
    fun `stdlib transformations drop the persistent type and need an explicit conversion back`() {
        val filtered = persistentListOf(1, 2, 3).filter { it % 2 == 1 }

        assertFalse(filtered is ImmutableList<*>)
        assertTrue(filtered.toImmutableList() is ImmutableList<*>)
    }
}
