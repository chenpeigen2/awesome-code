# Rust Lab

Rust 核心概念实验室。零依赖（只用 std），每个模块对应一个核心主题，全部带单元测试。

## 运行

```bash
cd rust-lab

cargo run                  # 运行全部演示
cargo run -- ownership     # 只跑某个主题（可选: ownership / smart-ptr / iter / errors / concurrency）
cargo test                 # 30 个单元测试
cargo clippy --all-targets # lint（当前零警告）
```

## 模块与知识点

| 模块 | 主题 | 亮点 |
|------|------|------|
| `ownership` | 所有权 / 借用 / 生命周期 | Box 打破递归类型；借用切片 vs 拥有 String；生命周期绑定输入输出 |
| `smart_ptr` | 智能指针 | Box / Rc（引用计数）/ RefCell（运行时借用检查，含 `#[should_panic]` 演示）/ Cow（写时克隆零拷贝） |
| `iter_play` | 迭代器 | 自定义惰性 Iterator（菲波那契，溢出自然终止）；适配器链；windows/zip 模式 |
| `errors` | 错误处理 | 自定义错误 enum + Display + Error::source 错误链；fail-fast 与宽容两种解析策略 |
| `concurrency` | 并发 | scoped thread（借用栈上数据）；mpsc channel 多生产者单消费者；Arc\<Mutex\> 无丢失更新计数器 |

## 设计原则

- **预期值全部来自独立真值**：OEIS 数列（A000040/A000045）、手算结果、数学常数——测试不是同义反复
- **每个测试对应一个可命名行为**，注释说明真值来源
- **不用任何第三方 crate**，方便学习时聚焦语言本身而非生态
