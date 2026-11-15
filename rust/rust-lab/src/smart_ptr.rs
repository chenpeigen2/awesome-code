//! 智能指针：Box / Rc / RefCell / Cow 各自的典型场景。

use std::borrow::Cow;
use std::cell::RefCell;
use std::rc::Rc;

/// `Box<T>`：把大数据放堆上、栈上只留指针；也用于递归类型。
pub fn boxed_squares(n: usize) -> Box<[i64]> {
    (0..n as i64)
        .map(|i| i * i)
        .collect::<Vec<_>>()
        .into_boxed_slice()
}

/// `Rc<T>`：引用计数的共享所有权（单线程）。
///
/// 返回克隆后的 Rc 与当前引用计数（3：原始 + a + b）。
/// Rc::clone 只增加计数，不深拷贝。
pub fn shared_greeting(name: &str) -> (Rc<String>, usize) {
    let greeting = Rc::new(format!("Hello, {name}!"));
    let a = Rc::clone(&greeting);
    let b = Rc::clone(&greeting);
    let count = Rc::strong_count(&greeting);
    drop(a); // 计数回降到 2，但返回的 b 仍然有效
    (b, count)
}

/// `Rc<RefCell<T>>`：共享所有权 + 内部可变性（运行时借用检查）。
///
/// 多个"读者"共同持有数据，其中一方可以通过 RefCell 修改它。
/// 违反借用规则会在运行时 panic（而不是编译错误）—— 这就是代价。
pub fn shared_counter() -> i64 {
    let counter = Rc::new(RefCell::new(0i64));
    let mut handles = Vec::new();
    for _ in 0..10 {
        let c = Rc::clone(&counter);
        handles.push(c);
    }
    // 每个克隆各自加 1
    for c in handles {
        *c.borrow_mut() += 1;
    }
    let result = *counter.borrow();
    result
}

/// `Cow<'a, T>`：写时克隆 —— 读多写少时避免不必要的分配。
///
/// 输入是 ASCII 时直接借用原串（零拷贝）；否则分配新串替换空格。
pub fn normalize_whitespace(input: &str) -> Cow<'_, str> {
    if input.is_ascii() {
        Cow::Borrowed(input)
    } else {
        Cow::Owned(input.replace(' ', "_"))
    }
}

// ==================== 测试 ====================
// 预期值来自独立真值：平方数表、引用计数定义。

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn boxed_squares_matches_table() {
        assert_eq!(&*boxed_squares(5), &[0, 1, 4, 9, 16][..]);
        assert_eq!(boxed_squares(0).len(), 0);
    }

    #[test]
    fn rc_shares_without_copy() {
        let (rc, count) = shared_greeting("Rust");
        assert_eq!(&*rc, "Hello, Rust!");
        // greeting + a + b = 3 份强引用
        assert_eq!(count, 3);
    }

    #[test]
    fn refcell_interior_mutation() {
        assert_eq!(shared_counter(), 10);
    }

    #[test]
    fn cow_borrows_ascii_avoids_allocation() {
        let input = String::from("plain ascii");
        let cow = normalize_whitespace(&input);
        match cow {
            Cow::Borrowed(s) => assert_eq!(s, "plain ascii"),
            Cow::Owned(_) => panic!("ASCII 输入应零拷贝借用"),
        }
    }

    #[test]
    fn cow_clones_non_ascii() {
        let cow = normalize_whitespace("你好 世界");
        assert_eq!(&*cow, "你好_世界");
        assert!(matches!(cow, Cow::Owned(_)));
    }

    /// RefCell 的借用规则在运行时生效：同时持有可变借用再取借用会 panic。
    #[test]
    #[should_panic]
    fn refcell_enforces_rules_at_runtime() {
        let cell = RefCell::new(5);
        let _w = cell.borrow_mut();
        let _r = cell.borrow(); // 不可变借用与可变借用冲突 → panic
    }
}
