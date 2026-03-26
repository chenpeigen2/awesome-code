//! Level 3: 所有权和借用 - Rust 的核心特性
//!
//! 学习目标:
//! - 深入理解所有权规则
//! - 掌握借用和引用
//! - 理解生命周期概念

use serde::{Deserialize, Serialize};
use std::collections::HashMap;

#[derive(Debug, Serialize, Deserialize)]
pub struct OwnershipDemo {
    pub rule: String,
    pub explanation: String,
    pub code_example: String,
    pub visual: String,
}

/// 所有权演示
#[tauri::command]
pub fn demo_ownership() -> Vec<OwnershipDemo> {
    vec![
        OwnershipDemo {
            rule: "规则1: 每个值都有一个所有者".to_string(),
            explanation: "在 Rust 中，每个值都有一个变量作为其所有者。这个变量负责该值的内存管理。".to_string(),
            code_example: r#"let s = String::from("hello");  // s 是 "hello" 的所有者"#.to_string(),
            visual: r#"┌─────────────┐
│  变量 s     │
│  (所有者)    │
└──────┬──────┘
       │ 拥有
       ▼
┌─────────────┐
│  String     │
│  "hello"    │
│  [堆内存]    │
└─────────────┘"#.to_string(),
        },
        OwnershipDemo {
            rule: "规则2: 同一时刻只能有一个所有者".to_string(),
            explanation: "当值被赋值给另一个变量时，所有权会转移（移动语义）。原变量不再有效。".to_string(),
            code_example: r#"let s1 = String::from("hello");
let s2 = s1;  // 所有权从 s1 移动到 s2
// println!("{}", s1);  // 编译错误！s1 已失效"#.to_string(),
            visual: r#"移动前:
s1 ──► "hello"

移动后 (s1 = s2):
s1 ──► [无效]
s2 ──► "hello""#.to_string(),
        },
        OwnershipDemo {
            rule: "规则3: 所有者离开作用域时值被释放".to_string(),
            explanation: "当变量离开作用域时，Rust 会自动调用 drop 函数释放内存。".to_string(),
            code_example: r#"{
    let s = String::from("hello");
    // s 在此作用域内有效
}   // <-- s 离开作用域，自动调用 drop，内存释放"#.to_string(),
            visual: r#"{  <-- 作用域开始
    s ──► "hello"
    ...
}  <-- 作用域结束
    调用 drop(s)
    内存被释放"#.to_string(),
        },
        OwnershipDemo {
            rule: "Clone: 显式深拷贝".to_string(),
            explanation: "使用 .clone() 可以创建值的深拷贝，两个变量各自拥有独立的内存。".to_string(),
            code_example: r#"let s1 = String::from("hello");
let s2 = s1.clone();  // 显式克隆，两个独立的值
println!("s1 = {}, s2 = {}", s1, s2);  // 都有效"#.to_string(),
            visual: r#"clone 前:
s1 ──► "hello"

clone 后:
s1 ──► "hello" (原始)
s2 ──► "hello" (副本) [独立内存]"#.to_string(),
        },
        OwnershipDemo {
            rule: "Copy Trait: 栈上数据的复制".to_string(),
            explanation: "实现了 Copy trait 的类型（如基本整数类型）在赋值时会自动复制，而不是移动。".to_string(),
            code_example: r#"let x = 5;
let y = x;  // i32 实现了 Copy，所以 x 仍然有效
println!("x = {}, y = {}", x, y);  // 都有效

// 实现 Copy 的类型：
// - 所有整数类型、浮点类型
// - bool, char
// - 元素都是 Copy 的元组"#.to_string(),
            visual: r#"Copy 类型赋值:
x ──► 5
y ──► 5  (自动复制)

两个变量都有效，值在栈上"#.to_string(),
        },
        OwnershipDemo {
            rule: "函数与所有权".to_string(),
            explanation: "将值传递给函数也会转移所有权。函数返回值可以将所有权返回给调用者。".to_string(),
            code_example: r#"fn take_ownership(s: String) {
    println!("{}", s);
}  // s 在这里被 drop

fn give_ownership() -> String {
    String::from("yours")
}

let s = String::from("hello");
take_ownership(s);
// s 不再有效！

let s2 = give_ownership();
// s2 现在拥有返回的字符串"#.to_string(),
            visual: r#"调用 take_ownership(s):
s ─────────────────┐
                   ▼
    fn take_ownership(s: String) {
        // s 现在拥有这个值
    } // 函数结束，s 被 drop"#.to_string(),
        },
    ]
}

/// 借用演示
#[tauri::command]
pub fn demo_borrowing() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "什么是借用？".to_string(),
        "借用允许我们在不获取所有权的情况下使用值。\n\
         通过引用（&T）创建借用，借用结束时所有权自动归还。".to_string(),
    );

    // 不可变借用
    let s = String::from("hello");
    let len = calculate_length(&s);
    result.insert(
        "不可变借用 (&T)".to_string(),
        format!(
            "代码:\nfn calculate_length(s: &String) -> usize {{\n    s.len()\n}}\n\n\
             let s = String::from(\"hello\");\nlet len = calculate_length(&s);\nprintln!(\"{{}} has {{}} characters\", s, len);\n\n\
             结果: \"{}\" has {} characters\n\n\
             注意: s 在借用后仍然有效！",
            s, len
        ),
    );

    // 可变借用
    result.insert(
        "可变借用 (&mut T)".to_string(),
        r#"代码:
fn append_world(s: &mut String) {
    s.push_str(", world");
}

let mut s = String::from("hello");
append_world(&mut s);
println!("{}", s);

结果: "hello, world"

⚠️ 注意: 同一时刻只能有一个可变引用"#.to_string(),
    );

    // 借用规则
    result.insert(
        "借用规则 ⚠️".to_string(),
        "可以有以下任一情况，但不能同时:\n\
         • 多个不可变引用 (&T)\n\
         • 一个可变引用 (&mut T)\n\n\
         这防止了数据竞争！".to_string(),
    );

    // 悬垂引用
    result.insert(
        "Rust 防止悬垂引用".to_string(),
        r#"// 这段代码会编译失败！
fn dangle() -> &String {
    let s = String::from("hello");
    &s  // 返回 s 的引用
}  // s 在这里被释放！

// 正确做法：返回所有权
fn no_dangle() -> String {
    let s = String::from("hello");
    s  // 移动所有权给调用者
}"#.to_string(),
    );

    // 切片
    let text = String::from("hello world");
    let hello = &text[0..5];
    let world = &text[6..11];
    result.insert(
        "字符串切片 (&str)".to_string(),
        format!(
            "let text = String::from(\"hello world\");\n\
             let hello = &text[0..5];  // \"{}\"\n\
             let world = &text[6..11];  // \"{}\"\n\n\
             切片是对字符串部分内容的借用",
            hello, world
        ),
    );

    // 借用可视化
    result.insert(
        "借用图解".to_string(),
        r#"所有权:
┌─────┐
│  s  │──► "hello world" [堆]
└─────┘

不可变借用:
┌─────┐     ┌─────────┐
│  s  │──►  │ String  │
└─────┘     └────┬────┘
                 │
┌─────┐     ┌────┴────┐
│ &s  │──►  │ 引用    │
└─────┘     └─────────┘"#.to_string(),
    );

    result
}

/// 生命周期演示
#[tauri::command]
pub fn demo_lifetimes() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "什么是生命周期？".to_string(),
        "生命周期是引用有效的范围。\n\
         Rust 使用生命周期确保引用始终有效，防止悬垂引用。\n\
         大多数时候生命周期是隐式的、自动推断的。".to_string(),
    );

    result.insert(
        "为什么需要显式生命周期？".to_string(),
        r#"当编译器无法确定返回引用的生命周期时，需要显式标注。

// 错误！编译器不知道返回谁的引用
fn longest(x: &str, y: &str) -> &str {
    if x.len() > y.len() { x } else { y }
}

// 正确：使用生命周期标注
fn longest<'a>(x: &'a str, y: &'a str) -> &'a str {
    if x.len() > y.len() { x } else { y }
}

<'a> 表示：x、y 和返回值至少活一样久"#.to_string(),
    );

    result.insert(
        "生命周期标注语法".to_string(),
        r#"// 语法: 'a, 'b, 'static 等

// 结构体中的生命周期
struct ImportantExcerpt<'a> {
    part: &'a str,  // 持有一个字符串切片的引用
}

// 方法中的生命周期
impl<'a> ImportantExcerpt<'a> {
    fn level(&self) -> i32 {
        3
    }

    fn announce_and_return_part(&self, announcement: &str) -> &str {
        println!("Attention: {}", announcement);
        self.part
    }
}"#.to_string(),
    );

    result.insert(
        "生命周期省略规则".to_string(),
        r#"编译器遵循三条规则自动推断生命周期:

规则1: 每个引用参数都获得一个生命周期
fn foo(x: &i32)        → fn foo<'a>(x: &'a i32)

规则2: 如果只有一个输入生命周期，赋给所有输出
fn foo(x: &str) -> &str  → fn foo<'a>(x: &'a str) -> &'a str

规则3: 如果有 &self 或 &mut self，self 的生命周期赋给所有输出
fn foo(&self, x: &str) -> &str  → 输出使用 self 的生命周期"#.to_string(),
    );

    result.insert(
        "'static 生命周期".to_string(),
        r#"'static 表示整个程序运行期间都有效。

// 静态字符串字面量
let s: &'static str = "I have a static lifetime.";

// 静态变量
static LANGUAGE: &str = "Rust";

// 注意：不要滥用 'static！
// 大多数时候应该让编译器推断"#.to_string(),
    );

    result.insert(
        "生命周期最佳实践".to_string(),
        "• 让编译器尽可能自动推断\n\
         • 返回引用时确保有明确的输入生命周期\n\
         • 如果需要返回新分配的数据，返回所有权而非引用\n\
         • 使用 'static 时要理解其含义\n\
         • 生命周期不会改变引用实际存活的时间，只是告诉编译器关系".to_string(),
    );

    result
}

// 辅助函数
fn calculate_length(s: &String) -> usize {
    s.len()
}
