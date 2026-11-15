//! 所有权与借用：两个经典练习。
//!
//! 1. 不可变链表 —— 体会 `Box` 与递归结构、`Drop`；
//! 2. 最长单词查找 —— 体会借用切片 vs 拥有 String 的取舍。

/// 不可变单向链表（cons list）。
///
/// `Cons(Box<Node>)` 用 Box 打破无限大小的递归类型——
/// 这是所有权系统下递归数据结构的标准解法。
#[derive(Debug)]
pub enum List {
    Nil,
    Cons(Box<Node>),
}

#[derive(Debug)]
pub struct Node {
    pub value: i64,
    pub next: List,
}

impl List {
    pub fn nil() -> Self {
        List::Nil
    }

    /// 头部插入：消耗 self 返回新链表，O(1)。
    pub fn cons(self, value: i64) -> Self {
        List::Cons(Box::new(Node { value, next: self }))
    }

    pub fn len(&self) -> usize {
        let mut count = 0;
        let mut cur = self;
        while let List::Cons(node) = cur {
            count += 1;
            cur = &node.next;
        }
        count
    }

    pub fn is_empty(&self) -> bool {
        matches!(self, List::Nil)
    }

    /// 转成 Vec（头 → 尾）
    pub fn to_vec(&self) -> Vec<i64> {
        let mut out = Vec::new();
        let mut cur = self;
        while let List::Cons(node) = cur {
            out.push(node.value);
            cur = &node.next;
        }
        out
    }

    /// 惰性 map：返回新链表，原链表保持可用（借用而非消耗）。
    pub fn map(&self, f: impl Fn(i64) -> i64) -> List {
        let values = self.to_vec();
        let mut list = List::nil();
        for v in values.into_iter().rev() {
            list = list.cons(f(v));
        }
        list
    }
}

/// 在一段文本中找出最长的单词，返回其字符长度与借用引用。
///
/// 返回 `&str` 借用输入 —— 生命周期 `'a` 把返回值的存活期
/// 与输入绑定，编译期防止悬垂引用。
pub fn longest_word(text: &str) -> Option<(usize, &str)> {
    text.split_whitespace()
        .max_by_key(|w| w.chars().count())
        .map(|w| (w.chars().count(), w))
}

/// 统计某字符出现次数（演示借用多个不可变引用可并存）
pub fn count_char(text: &str, target: char) -> usize {
    text.chars().filter(|&c| c == target).count()
}

// ==================== 测试 ====================

#[cfg(test)]
mod tests {
    use super::*;

    /// 1 → 2 → 3（cons 顺序：3 最先插入）
    #[test]
    fn cons_list_builds_correctly() {
        let list = List::nil().cons(3).cons(2).cons(1);
        assert_eq!(list.len(), 3);
        assert_eq!(list.to_vec(), vec![1, 2, 3]);
    }

    #[test]
    fn empty_list() {
        let list = List::nil();
        assert_eq!(list.len(), 0);
        assert!(list.to_vec().is_empty());
    }

    /// map 之后原链表仍可用（借用不消耗）
    #[test]
    fn map_keeps_original() {
        let list = List::nil().cons(3).cons(2).cons(1);
        let doubled = list.map(|v| v * 2);
        assert_eq!(doubled.to_vec(), vec![2, 4, 6]);
        // 原链表仍然完整
        assert_eq!(list.to_vec(), vec![1, 2, 3]);
    }

    #[test]
    fn longest_word_finds_max() {
        let text = "Rust ownership borrowing lifetime";
        // 最长单词是 "ownership" 与 "borrowing" 都是 9 —— max_by_key 取最后一个
        let (len, word) = longest_word(text).unwrap();
        assert_eq!(len, 9);
        assert_eq!(word, "borrowing");
    }

    #[test]
    fn longest_word_empty_input() {
        assert!(longest_word("   ").is_none());
    }

    /// 生命周期检查：返回值不能活得比输入长。
    /// 下面这段如果取消注释将无法编译 —— 这正是所有权系统在保护我们。
    #[test]
    fn borrowed_reference_tied_to_input() {
        let text = String::from("alpha beta");
        let (len, word) = longest_word(&text).unwrap();
        assert_eq!((len, word), (5, "alpha"));
        // text 在这里仍然可用（不可变借用已结束）：alpha 有 2 个 a + beta 有 1 个 = 3
        assert_eq!(count_char(&text, 'a'), 3);
    }
}
