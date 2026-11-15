package org.peter.immutable.history

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate

class UndoRedoHistoryTest {

    private fun historyOf(vararg values: String): UndoRedoHistory<String> =
        values.fold(UndoRedoHistory("")) { acc, value -> acc.record(value) }

    @Test
    fun `initial history has neither undo nor redo`() {
        val history = UndoRedoHistory("v0")

        assertEquals("v0", history.current)
        assertFalse(history.canUndo)
        assertFalse(history.canRedo)
        assertEquals(listOf("v0"), history.snapshots)
    }

    @Test
    fun `record then undo restores the previous value`() {
        val history = historyOf("富文本", "富文本 + 粗体")

        assertEquals("富文本 + 粗体", history.current)
        assertTrue(history.canUndo)

        val undone = history.undo()
        assertEquals("富文本", undone.current)
        assertTrue(undone.canRedo)
        assertEquals("富文本 + 粗体", undone.redo().current)
    }

    @Test
    fun `recording after undo drops the redo branch`() {
        val history = historyOf("a", "b", "c").undo().undo()
        assertEquals("a", history.current)
        assertTrue(history.canRedo)

        val branched = history.record("d")

        assertEquals("d", branched.current)
        assertFalse(branched.canRedo)
        // 历史从初始快照 "" 开始，撤销两次后停在 "a"，重做分支 b、c 被丢弃
        assertEquals(listOf("", "a", "d"), branched.snapshots)
    }

    @Test
    fun `undo at the oldest state is a no-op that returns the same instance`() {
        val history = historyOf("a").undo()

        assertSame(history, history.undo())
        assertEquals("", history.current)
    }

    @Test
    fun `redo at the newest state is a no-op that returns the same instance`() {
        val history = historyOf("a", "b")

        assertSame(history, history.redo())
    }

    @Test
    fun `recording the current value again returns the same instance`() {
        val history = historyOf("a")

        assertSame(history, history.record("a"))
        assertEquals(listOf("", "a"), history.snapshots)
    }

    @Test
    fun `snapshots are immutable so an old history can never be changed`() {
        val history = historyOf("a")
        val snapshots = history.snapshots

        assertTrue(snapshots is ImmutableList<*>)
        // 走“先改副本”的正确姿势：原快照不受影响
        assertEquals(listOf("", "a", "x"), snapshots.mutate { it.add("x") })
        assertEquals(listOf("", "a"), snapshots)
    }
}
