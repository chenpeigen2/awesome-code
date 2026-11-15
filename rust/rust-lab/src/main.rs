//! Rust Lab CLI：直接运行查看各模块演示输出。
//!
//! ```bash
//! cargo run -p rust-lab                       # 全部演示
//! cargo run -p rust-lab ownership fibonacci   # 单个演示
//! cargo test  -p rust-lab                     # 全部测试
//! ```

use rust_lab::{concurrency, errors, iter_play, ownership, smart_ptr};
use std::error::Error;

fn main() {
    let args: Vec<String> = std::env::args().skip(1).collect();

    let demos: &[(&str, &str, fn())] = &[
        ("ownership", "所有权：链表、借用、生命周期", demo_ownership),
        ("smart-ptr", "智能指针：Box/Rc/RefCell/Cow", demo_smart_ptr),
        ("iter", "迭代器：适配器链与自定义 Iterator", demo_iter),
        ("errors", "错误处理：错误链与 ? 传播", demo_errors),
        ("concurrency", "并发：线程/channel/Mutex", demo_concurrency),
    ];

    if args.is_empty() {
        println!("Rust Lab —— 核心概念实验室\n");
        for (name, desc, demo) in demos {
            println!("━━━ {name}: {desc} ━━━");
            demo();
            println!();
        }
        return;
    }

    for arg in args {
        match demos.iter().find(|(name, _, _)| *name == arg) {
            Some((_, desc, demo)) => {
                println!("━━━ {desc} ━━━");
                demo();
            }
            None => {
                eprintln!("未知演示: {arg}");
                eprintln!(
                    "可用: {}",
                    demos
                        .iter()
                        .map(|(n, _, _)| *n)
                        .collect::<Vec<_>>()
                        .join(", ")
                );
                std::process::exit(1);
            }
        }
    }
}

fn demo_ownership() {
    let list = ownership::List::nil().cons(3).cons(2).cons(1);
    println!("链表: {:?}，长度 {}", list.to_vec(), list.len());
    println!("map 翻倍: {:?}（原链表不变）", list.map(|v| v * 2).to_vec());

    let text = "Rust ownership borrowing lifetime";
    if let Some((len, word)) = ownership::longest_word(text) {
        println!("最长单词: {word:?}（{len} 字符，借用自输入）");
    }
}

fn demo_smart_ptr() {
    println!("Box 平方表: {:?}", &*smart_ptr::boxed_squares(6));

    let (rc, count) = smart_ptr::shared_greeting("Lab");
    println!("Rc 共享: {rc}（创建时引用计数 = {count}）");
    println!(
        "RefCell 共享计数器（10 个克隆各加 1）: {}",
        smart_ptr::shared_counter()
    );

    let ascii = smart_ptr::normalize_whitespace("plain ascii");
    let cjk = smart_ptr::normalize_whitespace("你好 世界");
    println!("Cow ASCII（零拷贝借用）: {ascii}");
    println!("Cow 中文（写时克隆）: {cjk}");
}

fn demo_iter() {
    let fib10: Vec<u64> = iter_play::fibonacci().take(10).collect();
    println!("菲波那契前 10 项: {fib10:?}");

    let numbers: Vec<i64> = (1..=6).collect();
    println!("1..6 偶数平方和: {}", iter_play::sum_even_squares(&numbers));

    let avg = iter_play::moving_average(&[1.0, 3.0, 5.0, 7.0], 2);
    println!("滑动平均(窗口2): {avg:?}");
}

fn demo_errors() {
    let transcript = "Alice:95.5\nBob:88\n坏行\nCarol:120";
    match errors::parse_transcript(transcript) {
        Ok(students) => println!("全部解析成功: {students:?}"),
        Err(err) => println!(
            "fail-fast 首个错误: {err}（来源: {:?}）",
            err.source().is_some()
        ),
    }

    let (ok, bad) = errors::parse_transcript_lenient(transcript);
    println!("宽容模式: {} 条好记录，{} 条坏记录", ok.len(), bad.len());
    for msg in bad {
        println!("  - {msg}");
    }
}

fn demo_concurrency() {
    let data: Vec<u64> = (0..1000).collect();
    println!(
        "多线程求和 (0..1000, 4线程): {}",
        concurrency::parallel_sum(&data, 4)
    );
    let msgs = concurrency::fan_in(4, 100);
    println!(
        "fan-in (4 生产者 × 100 消息): 收到 {} 条，首条 {:?}",
        msgs.len(),
        msgs.first().copied()
    );
    println!(
        "Mutex 计数器 (8线程 × 10000 次): {}",
        concurrency::shared_counter_increments(8, 10_000)
    );
}
