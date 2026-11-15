//! Rust Lab —— 核心概念实验室（零依赖，只用 std）。
//!
//! 每个模块对应一个 Rust 核心主题，带独立单元测试：
//! - [`ownership`]：所有权 / 借用 / 生命周期的经典练习（链表与最长词查找）
//! - [`smart_ptr`]：Box / Rc / RefCell / Cow 的使用场景
//! - [`iter_play`]：迭代器链、惰性求值、自定义 Iterator
//! - [`errors`]：自定义错误类型、错误链、`?` 传播
//! - [`concurrency`]：线程、channel、Arc、scoped thread

pub mod concurrency;
pub mod errors;
pub mod iter_play;
pub mod ownership;
pub mod smart_ptr;
