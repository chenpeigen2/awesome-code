//! Level 1: Rust 基础 - 变量、类型和字符串
//!
//! 学习目标:
//! - 理解 Rust 的变量声明和可变性
//! - 掌握基本数据类型
//! - 熟悉字符串操作

use serde::{Deserialize, Serialize};
use std::collections::HashMap;

/// 变量演示的返回结构
#[derive(Debug, Serialize, Deserialize)]
pub struct VariableDemo {
    pub explanation: String,
    pub code: String,
    pub output: String,
    pub tips: Vec<String>,
}

/// 类型演示的返回结构
#[derive(Debug, Serialize, Deserialize)]
pub struct TypeDemo {
    pub category: String,
    pub types: Vec<TypeInfo>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct TypeInfo {
    pub name: String,
    pub size_bytes: usize,
    pub range_or_example: String,
    pub example_code: String,
}

/// 演示 Rust 变量系统
#[tauri::command]
pub fn demo_variables() -> VariableDemo {
    let mut outputs: Vec<String> = Vec::new();
    let mut tips: Vec<String> = Vec::new();

    // 1. 不可变变量 (默认)
    let x = 5;
    outputs.push(format!("不可变变量 x = {}", x));

    // 2. 可变变量
    let mut y = 10;
    outputs.push(format!("可变变量 y 初始值 = {}", y));
    y = 20;
    outputs.push(format!("y 修改后 = {}", y));

    // 3. 常量 (必须注明类型，编译时确定)
    const MAX_POINTS: u32 = 100_000; // 下划线提高可读性
    outputs.push(format!("常量 MAX_POINTS = {}", MAX_POINTS));

    // 4. 变量遮蔽 (Shadowing)
    let z = 10;
    outputs.push(format!("第一次声明 z = {}", z));
    let z = z + 5;
    outputs.push(format!("遮蔽后 z = {}", z));
    let z = "现在是字符串";
    outputs.push(format!("再次遮蔽 z = {}", z));

    // 5. 未使用变量警告消除
    let _unused = 42; // 下划线前缀表示有意不使用

    tips.push("Rust 变量默认不可变，这是安全性的重要特性".to_string());
    tips.push("使用 mut 关键字声明可变变量".to_string());
    tips.push("变量遮蔽允许复用变量名，甚至改变类型".to_string());
    tips.push("常量使用 const，必须注明类型且不能使用 mut".to_string());

    VariableDemo {
        explanation: "Rust 的变量系统设计强调安全性和可预测性。默认不可变的设计迫使开发者明确表达'我需要修改这个值'的意图。"
            .to_string(),
        code: r#"// 不可变变量
let x = 5;

// 可变变量
let mut y = 10;
y = 20;

// 常量
const MAX_POINTS: u32 = 100_000;

// 变量遮蔽
let z = 10;
let z = z + 5;
let z = "现在是字符串";"#
            .to_string(),
        output: outputs.join("\n"),
        tips,
    }
}

/// 演示 Rust 基本数据类型
#[tauri::command]
pub fn demo_types() -> Vec<TypeDemo> {
    vec![
        TypeDemo {
            category: "整数类型".to_string(),
            types: vec![
                TypeInfo {
                    name: "i8".to_string(),
                    size_bytes: 1,
                    range_or_example: "-128 到 127".to_string(),
                    example_code: "let a: i8 = -128;".to_string(),
                },
                TypeInfo {
                    name: "u8".to_string(),
                    size_bytes: 1,
                    range_or_example: "0 到 255".to_string(),
                    example_code: "let b: u8 = 255;".to_string(),
                },
                TypeInfo {
                    name: "i32 (默认整数类型)".to_string(),
                    size_bytes: 4,
                    range_or_example: "约 -21亿 到 21亿".to_string(),
                    example_code: "let c = 42; // 自动推断为 i32".to_string(),
                },
                TypeInfo {
                    name: "usize/isize".to_string(),
                    size_bytes: 8,
                    range_or_example: "取决于计算机架构".to_string(),
                    example_code: "let len: usize = vec.len();".to_string(),
                },
            ],
        },
        TypeDemo {
            category: "浮点类型".to_string(),
            types: vec![
                TypeInfo {
                    name: "f32".to_string(),
                    size_bytes: 4,
                    range_or_example: "单精度浮点数".to_string(),
                    example_code: "let pi: f32 = 3.14159;".to_string(),
                },
                TypeInfo {
                    name: "f64 (默认)".to_string(),
                    size_bytes: 8,
                    range_or_example: "双精度浮点数".to_string(),
                    example_code: "let e = 2.71828; // f64".to_string(),
                },
            ],
        },
        TypeDemo {
            category: "布尔和字符".to_string(),
            types: vec![
                TypeInfo {
                    name: "bool".to_string(),
                    size_bytes: 1,
                    range_or_example: "true 或 false".to_string(),
                    example_code: "let is_rust_fun: bool = true;".to_string(),
                },
                TypeInfo {
                    name: "char".to_string(),
                    size_bytes: 4,
                    range_or_example: "Unicode 标量值".to_string(),
                    example_code: r#"let emoji: char = '🦀';"#.to_string(),
                },
            ],
        },
        TypeDemo {
            category: "复合类型".to_string(),
            types: vec![
                TypeInfo {
                    name: "元组 (Tuple)".to_string(),
                    size_bytes: 0,
                    range_or_example: "固定长度，可包含不同类型".to_string(),
                    example_code: r#"let tuple: (i32, f64, &str) = (1, 2.0, "hello");
let (x, y, z) = tuple; // 解构"#.to_string(),
                },
                TypeInfo {
                    name: "数组 (Array)".to_string(),
                    size_bytes: 0,
                    range_or_example: "固定长度，相同类型".to_string(),
                    example_code: r#"let arr: [i32; 5] = [1, 2, 3, 4, 5];
let zeros = [0; 10]; // 10个0"#.to_string(),
                },
            ],
        },
    ]
}

/// 字符串操作演示
#[tauri::command]
pub fn demo_strings() -> HashMap<String, String> {
    let mut result = HashMap::new();

    // String vs &str 的区别
    result.insert(
        "类型说明".to_string(),
        "String: 堆分配的可增长字符串\n&str: 字符串切片，通常是对String或静态字符串的引用".to_string(),
    );

    // String 创建
    let s1 = String::new();
    let s2 = String::from("hello");
    let s3 = "world".to_string();
    result.insert(
        "创建String".to_string(),
        format!(
            "String::new() = \"{}\"\n\
             String::from(\"hello\") = \"{}\"\n\
             \"world\".to_string() = \"{}\"",
            s1, s2, s3
        ),
    );

    // 字符串更新
    let mut s = String::from("foo");
    s.push_str("bar");
    s.push('!');
    result.insert(
        "更新字符串".to_string(),
        format!("push_str + push = \"{}\"", s),
    );

    // 字符串拼接
    let hello = String::from("Hello, ");
    let world = String::from("world!");
    let concat = hello + &world; // hello 被移动，world 被借用
    result.insert(
        "字符串拼接".to_string(),
        format!("\"Hello, \" + \"world!\" = \"{}\"", concat),
    );

    // 格式化宏
    let name = "Rust";
    let version = 1.70;
    result.insert(
        "格式化".to_string(),
        format!(
            "format!(\"{{}} version {{:.2}}\") = \"{}\"",
            format!("{} version {:.2}", name, version)
        ),
    );

    // 字符串切片
    let text = "你好世界";
    let slice = &text[0..3]; // 注意：中文字符占3字节
    result.insert(
        "切片警告".to_string(),
        "⚠️ 对UTF-8字符串切片必须注意字符边界！\n使用 .chars() 迭代字符更安全".to_string(),
    );

    // 遍历字符串
    let chars_demo: String = "🦀你好".chars().map(|c| format!("'{}' ", c)).collect();
    result.insert("字符遍历".to_string(), chars_demo);

    result.insert(
        "最佳实践".to_string(),
        "• 使用 String 存储需要修改或拥有的字符串\n\
         • 使用 &str 作为函数参数以提高灵活性\n\
         • 处理 Unicode 时使用 .chars() 而非索引\n\
         • 考虑使用 format! 宏进行复杂拼接"
            .to_string(),
    );

    result
}
