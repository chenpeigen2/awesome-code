//! Level 8: 并发和异步

use serde::{Deserialize, Serialize};
use std::collections::HashMap;

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ConcurrencyDemo {
    pub title: String,
    pub description: String,
}

#[tauri::command]
pub fn demo_threads() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "Rust 并发模型".to_string(),
        "Rust 的并发是无畏并发 (Fearless Concurrency)，类型系统和所有权规则在编译时防止数据竞争！".to_string()
    );

    result.insert(
        "创建线程".to_string(),
        "use std::thread;\n\nlet handle = thread::spawn(|| {\n    println!(\"新线程!\");\n});\nhandle.join().unwrap();".to_string()
    );

    result.insert(
        "Arc + Mutex 共享状态".to_string(),
        "use std::sync::{Arc, Mutex};\n\nlet counter = Arc::new(Mutex::new(0));\nlet counter_clone = Arc::clone(&counter);\n\nthread::spawn(move || {\n    let mut num = counter_clone.lock().unwrap();\n    *num += 1;\n});".to_string()
    );

    result.insert(
        "Send 和 Sync".to_string(),
        "Send: 类型可以安全地在线程间转移所有权\nSync: 类型可以安全地在线程间共享引用".to_string()
    );

    result
}

#[tauri::command]
pub fn demo_channels() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "消息传递".to_string(),
        "不要通过共享内存来通讯，而要通过通讯来共享内存".to_string()
    );

    result.insert(
        "mpsc 通道".to_string(),
        "use std::sync::mpsc;\n\nlet (tx, rx) = mpsc::channel();\n\ntx.send(42).unwrap();\nlet received = rx.recv().unwrap();".to_string()
    );

    result.insert(
        "多生产者".to_string(),
        "let tx1 = tx.clone();\nlet tx2 = tx.clone();\n\n// 不同线程使用 tx1, tx2 发送".to_string()
    );

    result
}

#[tauri::command]
pub fn demo_async() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "异步编程基础".to_string(),
        "Rust 的异步是基于 Future trait 的零成本抽象。async/await 语法让异步代码看起来像同步代码。".to_string()
    );

    result.insert(
        "async/await".to_string(),
        "async fn fetch_data() -> String {\n    let response = http_get(\"url\").await;\n    response.text().await\n}\n\nlet data = fetch_data().await;".to_string()
    );

    result.insert(
        "运行时".to_string(),
        "tokio - 最流行的异步运行时\nasync-std - 另一个选择\n\n#[tokio::main]\nasync fn main() {\n    // 异步代码\n}".to_string()
    );

    result.insert(
        "异步 vs 多线程".to_string(),
        "多线程: 适合 CPU 密集型任务\n异步: 适合 I/O 密集型任务\n\n最佳实践：结合使用！".to_string()
    );

    result
}
