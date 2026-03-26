//! Level 4: 结构体和枚举
//!
//! 学习目标:
//! - 掌握结构体的各种定义方式
//! - 理解枚举和模式匹配
//! - 熟悉 Option 和 Result

use serde::{Deserialize, Serialize};
use std::collections::HashMap;

// ==================== 结构体示例 ====================

/// 经典结构体
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Person {
    pub name: String,
    pub age: u32,
    pub email: Option<String>,
}

impl Person {
    /// 关联函数 (类似静态方法)
    pub fn new(name: String, age: u32) -> Self {
        Self {
            name,
            age,
            email: None,
        }
    }

    /// 方法 (需要 &self)
    pub fn introduce(&self) -> String {
        match &self.email {
            Some(email) => format!(
                "我是 {}，今年 {} 岁，邮箱: {}",
                self.name, self.age, email
            ),
            None => format!("我是 {}，今年 {} 岁", self.name, self.age),
        }
    }

    /// 可变方法
    pub fn have_birthday(&mut self) {
        self.age += 1;
    }
}

/// 元组结构体
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Point(pub f64, pub f64, pub f64);

impl Point {
    pub fn new(x: f64, y: f64, z: f64) -> Self {
        Self(x, y, z)
    }

    pub fn distance_from_origin(&self) -> f64 {
        (self.0.powi(2) + self.1.powi(2) + self.2.powi(2)).sqrt()
    }
}

/// 单元结构体 (无字段)
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct UnitMarker;

// ==================== 枚举示例 ====================

/// 基本枚举
#[derive(Debug, Clone, Serialize, Deserialize)]
pub enum Direction {
    Up,
    Down,
    Left,
    Right,
}

/// 带数据的枚举
#[derive(Debug, Clone, Serialize, Deserialize)]
pub enum Message {
    Quit,
    Move { x: i32, y: i32 },
    Write(String),
    ChangeColor(u8, u8, u8),
}

impl Message {
    pub fn process(&self) -> String {
        match self {
            Message::Quit => "退出程序".to_string(),
            Message::Move { x, y } => format!("移动到 ({}, {})", x, y),
            Message::Write(text) => format!("发送消息: {}", text),
            Message::ChangeColor(r, g, b) => format!("改变颜色为 RGB({}, {}, {})", r, g, b),
        }
    }
}

/// Option 演示
#[derive(Debug, Serialize, Deserialize)]
pub struct OptionDemo {
    pub description: String,
    pub code: String,
    pub result: String,
}

// ==================== Tauri 命令 ====================

/// 结构体演示
#[tauri::command]
pub fn demo_structs() -> HashMap<String, String> {
    let mut result = HashMap::new();

    // 创建经典结构体
    let mut person = Person::new("Alice".to_string(), 30);
    person.email = Some("alice@example.com".to_string());

    result.insert(
        "经典结构体".to_string(),
        format!(
            "struct Person {{\n    name: String,\n    age: u32,\n    email: Option<String>,\n}}\n\n\
             创建: Person::new(\"Alice\", 30)\n{}\n\
             生日后: age = {}",
            person.introduce(),
            {
                let mut p = person.clone();
                p.have_birthday();
                p.age
            }
        ),
    );

    // 元组结构体
    let point = Point::new(3.0, 4.0, 0.0);
    result.insert(
        "元组结构体".to_string(),
        format!(
            "struct Point(f64, f64, f64);\n\n\
             创建: Point(3.0, 4.0, 0.0)\n\
             访问: point.0 = {}, point.1 = {}\n\
             距离原点: {:.2}",
            point.0,
            point.1,
            point.distance_from_origin()
        ),
    );

    // 结构体更新语法
    let person2 = Person {
        name: "Bob".to_string(),
        ..person.clone()
    };
    result.insert(
        "结构体更新语法".to_string(),
        format!(
            "let person2 = Person {{\n    name: \"Bob\".to_string(),\n    ..person\n}};\n\n\
             结果: {}",
            person2.introduce()
        ),
    );

    result.insert(
        "派生宏 (Derive)".to_string(),
        "#[derive(Debug, Clone, Serialize, Deserialize)]\n\
         • Debug: 允许 {:?} 格式化\n\
         • Clone: 允许 .clone()\n\
         • Serialize/Deserialize: JSON 序列化\n\
         • PartialEq: 允许 == 比较\n\
         • Copy: 栈上复制（需要 Clone）".to_string(),
    );

    result
}

/// 枚举演示
#[tauri::command]
pub fn demo_enums() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "什么是枚举？".to_string(),
        "枚举定义一个类型可以是几种不同变体之一。\n\
         Rust 的枚举可以携带数据，非常强大！".to_string(),
    );

    // 简单枚举
    let dir = Direction::Up;
    let dir_str = match dir {
        Direction::Up => "上",
        Direction::Down => "下",
        Direction::Left => "左",
        Direction::Right => "右",
    };
    result.insert(
        "简单枚举".to_string(),
        format!(
            "enum Direction {{\n    Up,\n    Down,\n    Left,\n    Right,\n}}\n\n\
             Direction::Up → \"{}\"",
            dir_str
        ),
    );

    // 带数据的枚举
    let messages = vec![
        Message::Quit,
        Message::Move { x: 10, y: 20 },
        Message::Write("Hello".to_string()),
        Message::ChangeColor(255, 0, 128),
    ];
    let msg_results: Vec<String> = messages.iter().map(|m| m.process()).collect();
    result.insert(
        "带数据的枚举".to_string(),
        format!(
            "enum Message {{\n    Quit,\n    Move {{ x: i32, y: i32 }},\n    Write(String),\n    ChangeColor(u8, u8, u8),\n}}\n\n\
             处理结果:\n{}",
            msg_results.join("\n")
        ),
    );

    // Option 类型
    result.insert(
        "Option<T> - Rust 的空值处理".to_string(),
        "enum Option<T> {\n    Some(T),\n    None,\n}\n\n\
         Rust 没有 null，使用 Option 表示可能缺失的值。\n\
         这强制你显式处理空值情况！".to_string(),
    );

    // Result 类型
    result.insert(
        "Result<T, E> - 错误处理".to_string(),
        "enum Result<T, E> {\n    Ok(T),\n    Err(E),\n}\n\n\
         函数返回 Result 表示可能失败的操作。\n\
         调用者必须处理错误情况。".to_string(),
    );

    result
}

/// 模式匹配演示
#[tauri::command]
pub fn demo_pattern_matching() -> Vec<HashMap<String, String>> {
    vec![
        // 基本匹配
        {
            let mut m = HashMap::new();
            m.insert(
                "基本模式匹配".to_string(),
                "match value {\n    Pattern1 => expr1,\n    Pattern2 => expr2,\n    _ => default,  // 通配符\n}".to_string(),
            );
            let number = 13;
            let result = match number {
                1 => "一",
                2 | 3 | 5 | 7 | 11 | 13 => "质数",
                13..=19 => "青少年",
                _ => "其他",
            };
            m.insert(
                "示例".to_string(),
                format!("match {} {{\n    1 => \"一\",\n    2 | 3 | 5 | 7 | 11 | 13 => \"质数\",\n    13..=19 => \"青少年\",\n    _ => \"其他\",\n}}\n\n结果: \"{}\"", number, result),
            );
            m
        },
        // 解构结构体
        {
            let mut m = HashMap::new();
            m.insert(
                "解构结构体".to_string(),
                "let Person { name, age, .. } = person;".to_string(),
            );
            let person = Person::new("Charlie".to_string(), 25);
            let Person { name, age, .. } = person;
            m.insert(
                "示例".to_string(),
                format!(
                    "let Person {{ name, age, .. }} = Person::new(\"Charlie\", 25);\n// name = \"{}\", age = {}",
                    name, age
                ),
            );
            m
        },
        // 解构枚举
        {
            let mut m = HashMap::new();
            m.insert(
                "解构枚举".to_string(),
                "match message {\n    Message::Move { x, y } => ...,\n    Message::Write(text) => ...,\n    Message::ChangeColor(r, g, b) => ...,\n}".to_string(),
            );
            let msg = Message::Move { x: 100, y: 200 };
            let result = match msg {
                Message::Move { x, y } => format!("移动到 ({}, {})", x, y),
                _ => "其他消息".to_string(),
            };
            m.insert("示例".to_string(), format!("Message::Move {{ x: 100, y: 200 }}\n结果: {}", result));
            m
        },
        // Option 匹配
        {
            let mut m = HashMap::new();
            m.insert(
                "Option 匹配".to_string(),
                "match option {\n    Some(value) => ...,\n    None => ...,\n}".to_string(),
            );
            let some = Some(42);
            let result = match some {
                Some(x) if x > 40 => format!("大数: {}", x),
                Some(x) => format!("数: {}", x),
                None => "没有值".to_string(),
            };
            m.insert("示例".to_string(), format!("Some(42) 匹配结果: {}", result));
            m
        },
        // if let
        {
            let mut m = HashMap::new();
            m.insert(
                "if let 简化".to_string(),
                "if let Pattern = value {\n    // 匹配时执行\n}".to_string(),
            );
            let some = Some(5);
            let if_let_result = if let Some(x) = some {
                format!("值是 {}", x)
            } else {
                "没有值".to_string()
            };
            m.insert(
                "示例".to_string(),
                format!("if let Some(x) = Some(5) {{ ... }}\n结果: {}", if_let_result),
            );
            m
        },
        // while let
        {
            let mut m = HashMap::new();
            let mut stack = vec![1, 2, 3];
            let mut collected = Vec::new();
            while let Some(top) = stack.pop() {
                collected.push(top);
            }
            m.insert(
                "while let 循环".to_string(),
                "while let Some(top) = stack.pop() {\n    // 处理 top\n}".to_string(),
            );
            m.insert(
                "示例".to_string(),
                format!("弹出顺序: {:?}", collected),
            );
            m
        },
    ]
}
