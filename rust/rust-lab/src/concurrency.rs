//! 并发：线程、channel、Arc<Mutex>、scoped thread。
//!
//! 零依赖 —— 不用 tokio，只用 std，展示 Rust 并发原语的本来面目。

use std::sync::mpsc;
use std::sync::{Arc, Mutex};
use std::thread;

/// 多线程求和：把切片分块，各线程算部分和，主线程汇总。
///
/// 展示 scoped thread：借用栈上数据而无需 'static 或 Arc。
pub fn parallel_sum(data: &[u64], threads: usize) -> u64 {
    if data.is_empty() {
        return 0;
    }
    let threads = threads.clamp(1, 32);
    let chunk_size = data.len().div_ceil(threads);

    thread::scope(|s| {
        let mut handles = Vec::new();
        for chunk in data.chunks(chunk_size) {
            handles.push(s.spawn(move || chunk.iter().sum::<u64>()));
        }
        handles.into_iter().map(|h| h.join().unwrap()).sum()
    })
}

/// 生产者-消费者：N 个生产者各发 M 条消息，接收方收集总数。
///
/// channel 是 Rust 并发消息传递的核心：发送端克隆给各生产者，
/// 接收端在所有发送端 drop 后自然收到断开信号。
pub fn fan_in(producers: usize, messages_per_producer: usize) -> Vec<u64> {
    let (tx, rx) = mpsc::channel::<u64>();
    let mut handles = Vec::new();
    for p in 0..producers {
        let tx = tx.clone();
        handles.push(thread::spawn(move || {
            for m in 0..messages_per_producer {
                let _ = tx.send((p * messages_per_producer + m) as u64);
            }
            // tx 在此 drop：该生产者关闭
        }));
    }
    drop(tx); // 主发送端也关闭，rx 循环才能结束

    let mut received = Vec::new();
    for msg in rx {
        received.push(msg);
    }
    for h in handles {
        h.join().unwrap();
    }
    received.sort_unstable();
    received
}

/// 共享可变状态：Arc<Mutex<T>> 计数器竞态防护。
///
/// 对照实验：裸 u64 多线程自增会丢更新；Mutex 保证每个自增原子完成。
pub fn shared_counter_increments(threads: usize, increments: usize) -> i64 {
    let counter = Arc::new(Mutex::new(0i64));
    let mut handles = Vec::new();
    for _ in 0..threads {
        let counter = Arc::clone(&counter);
        handles.push(thread::spawn(move || {
            for _ in 0..increments {
                *counter.lock().unwrap() += 1;
            }
        }));
    }
    for h in handles {
        h.join().unwrap();
    }
    let result = *counter.lock().unwrap();
    result
}

// ==================== 测试 ====================
// 预期值来自独立真值：等差数列求和公式、组合计数。

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn parallel_sum_matches_sequential() {
        // 0..1000 的和 = 999*1000/2 = 499500
        let data: Vec<u64> = (0..1000).collect();
        assert_eq!(parallel_sum(&data, 1), 499_500);
        assert_eq!(parallel_sum(&data, 4), 499_500);
        assert_eq!(parallel_sum(&data, 1000), 499_500);
        assert_eq!(parallel_sum(&[], 4), 0);
    }

    #[test]
    fn fan_in_receives_every_message_exactly_once() {
        let messages = fan_in(4, 100);
        assert_eq!(messages.len(), 400);
        // 每个消息 [0, 400) 恰好一次
        assert_eq!(messages.first(), Some(&0));
        assert_eq!(messages.last(), Some(&399));
    }

    #[test]
    fn fan_in_zero_producers() {
        assert!(fan_in(0, 10).is_empty());
    }

    #[test]
    fn mutex_counter_has_no_lost_updates() {
        assert_eq!(shared_counter_increments(8, 10_000), 80_000);
    }

    #[test]
    fn mutex_counter_single_thread() {
        assert_eq!(shared_counter_increments(1, 1000), 1000);
    }
}
