//! 迭代器：适配器链、惰性求值、自定义 Iterator 实现。

/// 惰性迭代器：菲波那契数列。
///
/// 惰性意味着不预先计算——`take(10)` 只算 10 项，要多少算多少。
pub struct Fibonacci {
    prev: u64,
    curr: u64,
}

impl Iterator for Fibonacci {
    type Item = u64;

    fn next(&mut self) -> Option<u64> {
        let result = self.curr;
        // u64 溢出即数列尽头（自然终止，不 panic）
        let next = self.curr.checked_add(self.prev)?;
        self.prev = self.curr;
        self.curr = next;
        Some(result)
    }
}

pub fn fibonacci() -> Fibonacci {
    Fibonacci { prev: 1, curr: 0 }
}

/// 统计适配器流水线：偶数 → 平方 → 求和。
///
/// 演示适配器（filter/map）与消费器（sum）的组合。
pub fn sum_even_squares(numbers: &[i64]) -> i64 {
    numbers
        .iter()
        .filter(|&&n| n % 2 == 0)
        .map(|&n| n * n)
        .sum()
}

/// 词频归类：把单词按首字母分组（HashMap 消费器 collect 的典型用法）。
pub fn group_by_initial<'a>(words: &[&'a str]) -> std::collections::BTreeMap<char, Vec<&'a str>> {
    words
        .iter()
        .fold(std::collections::BTreeMap::new(), |mut acc, &w| {
            if let Some(first) = w.chars().next() {
                acc.entry(first.to_ascii_lowercase()).or_default().push(w);
            }
            acc
        })
}

/// 窗口滑动平均：zip 相邻元素的经典模式。
pub fn moving_average(data: &[f64], window: usize) -> Vec<f64> {
    if window == 0 || data.len() < window {
        return Vec::new();
    }
    data.windows(window)
        .map(|w| w.iter().sum::<f64>() / window as f64)
        .collect()
}

// ==================== 测试 ====================
// 预期值来自独立真值：菲波那契数列（OEIS A000045）、手算平方和。

#[cfg(test)]
mod tests {
    use super::*;

    /// OEIS A000045 前缀：0, 1, 1, 2, 3, 5, 8, 13, 21, 34
    #[test]
    fn fibonacci_matches_oeis() {
        let first10: Vec<u64> = fibonacci().take(10).collect();
        assert_eq!(first10, vec![0, 1, 1, 2, 3, 5, 8, 13, 21, 34]);
    }

    /// 惰性验证：take(5) 后迭代器只前进了 5 步（没有死循环或多余计算）
    #[test]
    fn fibonacci_is_lazy_and_terminates_on_overflow() {
        let mut count = 0;
        for _ in fibonacci() {
            count += 1;
            if count == 5 {
                break;
            }
        }
        assert_eq!(count, 5);
        // 溢出后自然结束而不是 panic：u64 最大斐波那契是 F(93)
        assert_eq!(fibonacci().count(), 93);
    }

    #[test]
    fn sum_even_squares_hand_computed() {
        // 输入 [1..=6]，偶数 2,4,6 → 4+16+36 = 56
        assert_eq!(sum_even_squares(&[1, 2, 3, 4, 5, 6]), 56);
        assert_eq!(sum_even_squares(&[1, 3, 5]), 0);
        assert_eq!(sum_even_squares(&[]), 0);
    }

    #[test]
    fn group_by_initial_sorts_and_normalizes() {
        let words = vec!["apple", "Banana", "avocado", "cherry"];
        let groups = group_by_initial(&words);
        assert_eq!(groups.len(), 3);
        assert_eq!(groups[&'a'], vec!["apple", "avocado"]);
        assert_eq!(groups[&'b'], vec!["Banana"]);
        assert_eq!(groups[&'c'], vec!["cherry"]);
    }

    #[test]
    fn moving_average_matches_hand_computation() {
        let data = [1.0, 3.0, 5.0, 7.0];
        assert_eq!(moving_average(&data, 2), vec![2.0, 4.0, 6.0]);
        // 窗口大于数据长度 → 空
        assert!(moving_average(&data, 5).is_empty());
        // 窗口为 0 → 空（避免除零）
        assert!(moving_average(&data, 0).is_empty());
    }
}
