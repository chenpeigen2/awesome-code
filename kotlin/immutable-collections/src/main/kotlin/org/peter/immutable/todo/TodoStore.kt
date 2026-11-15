package org.peter.immutable.todo

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

enum class TodoFilter { ALL, ACTIVE, COMPLETED }

data class Todo(
    val id: Int,
    val title: String,
    val completed: Boolean = false,
)

/**
 * 一整屏 UI 的状态就是一个不可变值：列表、过滤器、版本号。
 * 用 `ImmutableList` 而不是 `List`，表示“这份状态一经创建就永不改变”，
 * 因此可以放心地在多个版本之间共享、缓存、回放。
 */
data class TodoState(
    val todos: PersistentList<Todo> = persistentListOf(),
    val filter: TodoFilter = TodoFilter.ALL,
    val revision: Int = 0,
) {
    /** 过滤器只是“投影”，底层数据始终完整保留。 */
    val visibleTodos: ImmutableList<Todo>
        get() = when (filter) {
            TodoFilter.ALL -> todos
            TodoFilter.ACTIVE -> todos.filter { !it.completed }.toImmutableList()
            TodoFilter.COMPLETED -> todos.filter { it.completed }.toImmutableList()
        }
}

sealed interface TodoAction {
    data class Add(val title: String) : TodoAction
    data class Toggle(val id: Int) : TodoAction
    data class Remove(val id: Int) : TodoAction
    data class SetFilter(val filter: TodoFilter) : TodoAction
    data object ClearCompleted : TodoAction
}

/**
 * 单线程极简 store：dispatch(action) -> 新 state -> 通知订阅者。
 *
 * 不可变集合让它能顺便提供一个“时间旅行”能力：所有历史 state 都留在
 * `snapshots` 里，而每个 state 都是独立且不可变的。
 */
class TodoStore(initial: TodoState = TodoState()) {

    private val listeners = mutableListOf<(TodoState) -> Unit>()

    var state: TodoState = initial
        private set

    private var allSnapshots: PersistentList<TodoState> = persistentListOf(initial)

    /** 从启动到现在的全部 state，用于调试 / 时间旅行 / 回放。 */
    val snapshots: ImmutableList<TodoState> get() = allSnapshots

    fun dispatch(action: TodoAction) {
        val next = reduce(state, action)
        if (next === state) return // 无变化：不分配、不通知
        state = next
        allSnapshots = allSnapshots.add(next)
        listeners.toList().forEach { it(next) }
    }

    /** 订阅状态变化，返回取消订阅的函数。 */
    fun subscribe(listener: (TodoState) -> Unit): () -> Unit {
        listeners += listener
        return { listeners -= listener }
    }

    private fun reduce(state: TodoState, action: TodoAction): TodoState = when (action) {
        is TodoAction.Add -> {
            val title = action.title.trim()
            if (title.isEmpty()) {
                state
            } else {
                val nextId = (state.todos.maxOfOrNull { it.id } ?: 0) + 1
                state.copy(todos = state.todos.add(Todo(nextId, title)), revision = state.revision + 1)
            }
        }

        is TodoAction.Toggle -> state.updateTodo(action.id) { it.copy(completed = !it.completed) }
        is TodoAction.Remove -> {
            val remaining = state.todos.filterNot { it.id == action.id }
            if (remaining.size == state.todos.size) {
                state
            } else {
                state.copy(todos = remaining.toPersistentList(), revision = state.revision + 1)
            }
        }

        is TodoAction.SetFilter ->
            if (state.filter == action.filter) state
            else state.copy(filter = action.filter, revision = state.revision + 1)

        TodoAction.ClearCompleted -> {
            val remaining = state.todos.filterNot { it.completed }
            if (remaining.size == state.todos.size) {
                state
            } else {
                // filterNot 返回普通 List，需要显式收回不可变类型
                state.copy(todos = remaining.toPersistentList(), revision = state.revision + 1)
            }
        }
    }

    private fun TodoState.updateTodo(id: Int, transform: (Todo) -> Todo): TodoState {
        val index = todos.indexOfFirst { it.id == id }
        if (index < 0) return this
        return copy(
            todos = todos.mutate { it[index] = transform(it[index]) },
            revision = revision + 1,
        )
    }
}
