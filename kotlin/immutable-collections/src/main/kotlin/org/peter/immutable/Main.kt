package org.peter.immutable

import org.peter.immutable.demo.BasicsDemo
import org.peter.immutable.demo.HistoryDemo
import org.peter.immutable.demo.OperationsDemo
import org.peter.immutable.demo.SharingDemo
import org.peter.immutable.demo.TodoStoreDemo
import org.peter.immutable.demo.section

/**
 * kotlinx.collections.immutable 演示入口。
 *
 * 运行：`./gradlew :kotlin:immutable-collections:run`
 *
 * 五个部分：
 * 1. 类型体系与常见误区
 * 2. 修改操作的四种姿势（+/-、mutate、builder、put）
 * 3. 结构共享与性能
 * 4. 撤销/重做历史
 * 5. 不可变状态 + reducer store
 */
fun main() {
    println("kotlinx.collections.immutable demo  (Kotlin 2.4 / JDK 25)")

    section("1. 类型体系：Immutable* 是契约，Persistent* 是可更新的不可变集合") {
        BasicsDemo.run()
    }
    section("2. 修改集合：+ / - 、mutate、builder、put") {
        OperationsDemo.run()
    }
    section("3. 结构共享：为什么历史版本可以随便留") {
        SharingDemo.run()
    }
    section("4. 撤销 / 重做：把历史本身当作不可变值") {
        HistoryDemo.run()
    }
    section("5. 不可变状态 + reducer：TodoStore 与时间旅行") {
        TodoStoreDemo.run()
    }
}
