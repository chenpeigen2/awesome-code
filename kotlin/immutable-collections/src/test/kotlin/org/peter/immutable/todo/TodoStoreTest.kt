package org.peter.immutable.todo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TodoStoreTest {

    private fun storeWith(vararg titles: String) = TodoStore().apply {
        titles.forEach { dispatch(TodoAction.Add(it)) }
    }

    @Test
    fun `adding todo appends it in order with increasing ids`() {
        val store = storeWith("写 demo", "写测试")

        assertEquals(listOf("写 demo", "写测试"), store.state.todos.map { it.title })
        assertEquals(listOf(1, 2), store.state.todos.map { it.id })
        assertEquals(2, store.state.revision)
    }

    @Test
    fun `blank titles are ignored and do not notify subscribers`() {
        val store = TodoStore()
        val seen = mutableListOf<TodoState>()
        store.subscribe { seen += it }

        store.dispatch(TodoAction.Add("   "))

        assertTrue(store.state.todos.isEmpty())
        assertEquals(0, store.state.revision)
        assertTrue(seen.isEmpty())
    }

    @Test
    fun `toggling marks todo completed without touching the others`() {
        val store = storeWith("a", "b")

        store.dispatch(TodoAction.Toggle(1))

        assertEquals(listOf(true, false), store.state.todos.map { it.completed })
        assertEquals(listOf("a", "b"), store.state.todos.map { it.title })
    }

    @Test
    fun `removing keeps the remaining order`() {
        val store = storeWith("a", "b", "c")

        store.dispatch(TodoAction.Remove(2))

        assertEquals(listOf("a", "c"), store.state.todos.map { it.title })
    }

    @Test
    fun `clear completed removes only completed todos`() {
        val store = storeWith("a", "b", "c")
        store.dispatch(TodoAction.Toggle(1))
        store.dispatch(TodoAction.Toggle(3))

        store.dispatch(TodoAction.ClearCompleted)

        assertEquals(listOf("b"), store.state.todos.map { it.title })
    }

    @Test
    fun `filter only affects the visible projection`() {
        val store = storeWith("a", "b")
        store.dispatch(TodoAction.Toggle(1))

        store.dispatch(TodoAction.SetFilter(TodoFilter.ACTIVE))
        assertEquals(listOf("b"), store.state.visibleTodos.map { it.title })
        assertEquals(2, store.state.todos.size)

        store.dispatch(TodoAction.SetFilter(TodoFilter.COMPLETED))
        assertEquals(listOf("a"), store.state.visibleTodos.map { it.title })
    }

    @Test
    fun `subscribers get the new state and stop after unsubscribe`() {
        val store = TodoStore()
        val seen = mutableListOf<TodoState>()
        val unsubscribe = store.subscribe { seen += it }

        store.dispatch(TodoAction.Add("a"))
        unsubscribe()
        store.dispatch(TodoAction.Add("b"))

        assertEquals(1, seen.size)
        assertEquals(listOf("a"), seen.single().todos.map { it.title })
        assertEquals(listOf("a", "b"), store.state.todos.map { it.title })
    }

    @Test
    fun `a captured state is a frozen snapshot`() {
        val store = TodoStore()
        store.dispatch(TodoAction.Add("a"))
        val snapshot = store.state

        store.dispatch(TodoAction.Add("b"))
        store.dispatch(TodoAction.Toggle(1))

        assertEquals(listOf("a"), snapshot.todos.map { it.title })
        assertEquals(listOf(false), snapshot.todos.map { it.completed })
    }

    @Test
    fun `every revision is kept so the whole session can be replayed`() {
        val store = storeWith("a", "b")
        store.dispatch(TodoAction.Toggle(1))

        assertEquals(listOf(0, 1, 2, 3), store.snapshots.map { it.revision })
        assertEquals(listOf(0, 1, 2, 2), store.snapshots.map { it.todos.size })
    }
}
