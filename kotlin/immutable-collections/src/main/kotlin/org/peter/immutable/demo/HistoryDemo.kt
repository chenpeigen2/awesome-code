package org.peter.immutable.demo

import org.peter.immutable.history.UndoRedoHistory

/**
 * 第四部分：撤销 / 重做（持久化列表的经典用法）。
 *
 * 历史本身是一个不可变值：`record` / `undo` / `redo` 都返回“下一个版本”，
 * 旧版本永久可读，所以不需要深拷贝任何东西。
 */
object HistoryDemo {

    fun run() {
        var editor = UndoRedoHistory("")
        editor = editor.record("Hello")
        editor = editor.record("Hello, Kotlin")
        editor = editor.record("Hello, Kotlin!")

        line("输入三次后的文本", editor.current)
        line("canUndo / canRedo", "${editor.canUndo} / ${editor.canRedo}")
        line("全部快照", editor.snapshots)

        editor = editor.undo().undo()
        line("撤销两步", editor.current)
        line("canUndo / canRedo", "${editor.canUndo} / ${editor.canRedo}")

        editor = editor.redo()
        line("重做一步", editor.current)

        // 撤销中途再输入：redo 分支被丢弃
        editor = editor.record("Hello, Kotlin?")
        line("分叉（撤销后重新输入）", editor.current)
        line("分叉后的快照", editor.snapshots)
        line("分叉后 canRedo", editor.canRedo)

        // 无变化的 record / 越界 undo 都零分配
        val before = editor
        line("重复记录相同文本 === 原对象", editor.record(editor.current) === before)
        val oldest = editor.undo().undo().undo()
        line("一直撤销到最早", oldest.current)
        line("再撤销 === 原对象", oldest.undo() === oldest)
    }
}
