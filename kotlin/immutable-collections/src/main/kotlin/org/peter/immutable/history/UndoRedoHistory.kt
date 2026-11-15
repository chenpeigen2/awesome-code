package org.peter.immutable.history

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

/**
 * 基于持久化列表的撤销 / 重做历史。
 *
 * 核心思想：历史本身也是不可变值。`record` / `undo` / `redo` 不修改当前对象，
 * 而是返回“下一个版本”，旧版本因为结构共享而几乎不占额外内存，可以随意保留。
 *
 * @param T 每一步快照的类型（文本、画布状态、不可变 reducer state…）
 */
class UndoRedoHistory<T> private constructor(
    private val states: PersistentList<T>,
    private val cursor: Int,
) {

    constructor(initial: T) : this(persistentListOf(initial), 0)

    /** 当前指向的快照。 */
    val current: T get() = states[cursor]

    /** 是否还能撤销（前面还有更早的快照）。 */
    val canUndo: Boolean get() = cursor > 0

    /** 是否还能重做（撤销后 cursor 之后还有快照）。 */
    val canRedo: Boolean get() = cursor < states.lastIndex

    /**
     * 全部快照，按时间顺序。cursor 之后的元素属于 redo 分支。
     * 返回不可变视图，调用方无法“偷偷改坏”历史。
     */
    val snapshots: PersistentList<T> get() = states

    /**
     * 记录一个新快照并前进一步。
     *
     * - 与当前快照相同：直接返回自身（持久化集合的“无变化不分配”特性）。
     * - 正处于撤销中途：截断 redo 分支，新快照成为最新版本。
     */
    fun record(state: T): UndoRedoHistory<T> {
        if (state == current) return this
        // cursor 在末尾时无需截断，直接追加，结构共享最省内存
        val base: PersistentList<T> =
            if (cursor == states.lastIndex) states else states.subList(0, cursor + 1).toPersistentList()
        return UndoRedoHistory(base.add(state), cursor + 1)
    }

    /** 后退一步；已经在最早快照时返回自身。 */
    fun undo(): UndoRedoHistory<T> =
        if (canUndo) UndoRedoHistory(states, cursor - 1) else this

    /** 前进一步；已经在最新快照时返回自身。 */
    fun redo(): UndoRedoHistory<T> =
        if (canRedo) UndoRedoHistory(states, cursor + 1) else this
}
