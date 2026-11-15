package org.peter.immutable.demo

import org.peter.immutable.todo.TodoAction
import org.peter.immutable.todo.TodoFilter
import org.peter.immutable.todo.TodoState
import org.peter.immutable.todo.TodoStore

/**
 * 第五部分：不可变状态 + reducer（类似 Redux / Compose 的状态提升）。
 *
 * `TodoState` 里用 `PersistentList` 存数据，dispatch 生成新 state，
 * 订阅者拿到的都是永远不会被后续操作改写的快照。
 */
object TodoStoreDemo {

    fun run() {
        val store = TodoStore()
        val notifications = mutableListOf<String>()
        store.subscribe { state ->
            notifications += "rev=${state.revision} ${state.todos.map { it.title }}"
        }

        store.dispatch(TodoAction.Add("写 demo"))
        store.dispatch(TodoAction.Add("写测试"))
        store.dispatch(TodoAction.Add("   ")) // 空白标题被忽略
        store.dispatch(TodoAction.Toggle(1))

        line("全部待办", render(store.state))
        line("空白标题被忽略，revision", store.state.revision)

        store.dispatch(TodoAction.SetFilter(TodoFilter.ACTIVE))
        line("过滤 = ACTIVE", render(store.state))
        store.dispatch(TodoAction.SetFilter(TodoFilter.COMPLETED))
        line("过滤 = COMPLETED", render(store.state))

        // 订阅者收到的是“每一版”状态，历史版本不会被后来的 dispatch 污染
        line("订阅通知次数", notifications.size)
        notifications.forEach { println("    ← $it") }

        // 时间旅行：所有 state 都留着，随手回放
        store.dispatch(TodoAction.SetFilter(TodoFilter.ALL))
        store.dispatch(TodoAction.ClearCompleted)
        line("清理已完成", render(store.state))
        line("快照总数（含初始）", store.snapshots.size)
        store.snapshots.forEach { snapshot -> println("    rev=${snapshot.revision}  ${render(snapshot)}") }
    }

    private fun render(state: TodoState): String {
        val mark = if (state.filter == TodoFilter.ALL) "" else " [filter=${state.filter}]"
        return state.visibleTodos.joinToString(prefix = "[", postfix = "]$mark") { todo ->
            (if (todo.completed) "✓ " else "· ") + todo.title
        }
    }
}
