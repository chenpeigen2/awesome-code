# kotlinx.collections.immutable demo

[`kotlinx.collections.immutable`](https://github.com/Kotlin/kotlinx.collections.immutable) 的实践 demo：
不可变 / 持久化集合（`ImmutableList`、`PersistentMap`、结构共享）到底解决什么问题，以及适合用在哪。

版本：`kotlinx-collections-immutable 0.5.2`（版本由根目录 `gradle/libs.versions.toml` 统一管理）。

## 运行

```bash
# 跑 demo（五个部分依次打印）
./gradlew :kotlin:immutable-collections:run

# 跑测试（28 个用例）
./gradlew :kotlin:immutable-collections:test
```

> 如果配置阶段因为**其它**模块（例如 `explore:spring-shell` 的 BOM 插件）拉不到依赖而失败，
> 加上 `--configure-on-demand` 只配置本模块即可：
> `./gradlew --configure-on-demand :kotlin:immutable-collections:run`

## 这个 demo 讲什么

| 部分 | 入口 | 内容 |
|------|------|------|
| 1. 类型体系 | `demo/BasicsDemo.kt` | `Immutable*` 是只读契约，`Persistent*` 增加 `add/put/mutate`；标准库 `listOf()` **不是** `ImmutableList`；`toImmutableList()` 冻结可变源 |
| 2. 修改姿势 | `demo/OperationsDemo.kt` | `+` / `-`、`mutate { }`、`builder()`、`put()`；ordered 与 hash 两套实现；stdlib 变换会掉回 `ArrayList` |
| 3. 结构共享 | `demo/SharingDemo.kt` | 只重建根到叶子的路径，顺手跑一个 persistent vs 复制的对比 |
| 4. 撤销 / 重做 | `history/UndoRedoHistory.kt` | 把“历史”本身做成不可变值，`record/undo/redo` 返回新版本，零深拷贝 |
| 5. 不可变状态 | `todo/TodoStore.kt` | reducer + 订阅者，每个版本都是冻结快照，所以能白送时间旅行 |

实测输出（M 系列 Mac，JDK 25）：

```
50000 次追加：persistentListOf        ~10 ms
50000 次追加：listOf + 每次复制        ~700 ms   （约 70x）
100 万元素追加一个元素：persistent     ~0.2 µs
100 万元素追加一个元素：listOf +       ~1.2 ms
```

## API 速查

```kotlin
// 工厂
persistentListOf(1, 2, 3)
persistentSetOf("b", "a")        // 保插入顺序
persistentMapOf("a" to 1)        // 保插入顺序
persistentHashSetOf(1, 2)        // 更快，不保序
persistentHashMapOf("a" to 1)    // 更快，不保序

// 转换
list.toImmutableList()           // 可变源 -> 冻结副本；已是不可变则原样返回
list.toPersistentList()          // 需要继续 add/mutate 时用
list.toImmutableSet() / toPersistentMap()
// stdlib 的 filter/map/sorted 返回普通 List，要显式收回不可变类型

// 更新：全部返回新集合，旧集合永久可读
val v2 = v1 + 4                  // 注意导入 kotlinx.collections.immutable.plus
val v3 = v2 - 2                  // 注意导入 kotlinx.collections.immutable.minus
val v4 = v3.mutate { it.addAll(listOf(7, 8)) }   // 一次 builder 完成批量修改
val v5 = v4.builder().apply { add(9) }.build()   // 跨多次调用攒修改

// Map / Set
map.put(key, value) / map.remove(key) / set.add(x) / set.remove(x)
```

## 几个容易踩的点

1. **只读 ≠ 不可变**：`listOf()` 返回的只是“没有修改方法”的视图，`is ImmutableList` 为 `false`。
   真正需要“永不改变”的语义（缓存 key、跨线程共享、Compose 稳定性）时用本库的类型。
2. **`+` / `-` 要导入扩展**：不导入 `kotlinx.collections.immutable.plus` 就会用到 stdlib 版本，
   结果退化成普通 `List`（复制且失去 `PersistentList` 类型）。
3. **`mutate` 的 lambda 参数是 builder**：`mutate { it.add(x) }`（0.5.x），
   不是 receiver 风格；无改动时 `mutate {}` 会返回同一个实例（零分配）。
4. **builder 快照是隔离的**：`val snapshot = builder.build()` 之后继续改 builder，
   `snapshot` 不受影响；但同一个 builder 不要跨线程共享。
5. **ordered 与 hash 选一个**：`persistentMapOf/persistentSetOf` 保插入顺序，代价是结构更复杂；
   只关心集合语义时用 `persistentHashMapOf/persistentHashSetOf`。
6. **不可变性靠类型系统而非运行时**：强转成 `MutableList` 会直接 `ClassCastException`，
   但这不是刚性保证；把它当成“契约 + 便利”而不是安全边界。
7. **Compose / 多线程**：`ImmutableList`、`ImmutableMap`、`ImmutableSet` 被标记为不可变契约，
   适合放进 UI state 与快照；跨线程共享不需要加锁，也不再需要深拷贝做防御性复制。

## 结构

```
kotlin/immutable-collections/
├── build.gradle.kts
└── src/
    ├── main/kotlin/org/peter/immutable/
    │   ├── Main.kt                     # 入口，串起五个部分
    │   ├── demo/Demo.kt                # section/line/timed 打印工具
    │   ├── demo/BasicsDemo.kt
    │   ├── demo/OperationsDemo.kt
    │   ├── demo/SharingDemo.kt
    │   ├── demo/HistoryDemo.kt
    │   ├── demo/TodoStoreDemo.kt
    │   ├── history/UndoRedoHistory.kt  # 持久化列表实现 undo/redo
    │   └── todo/TodoStore.kt           # 不可变 state + reducer
    └── test/kotlin/org/peter/immutable/
        ├── CollectionsContractTest.kt  # 库契约（README/KDoc）
        ├── history/UndoRedoHistoryTest.kt
        └── todo/TodoStoreTest.kt
```
