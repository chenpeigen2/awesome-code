//! Level 6: 错误处理
//!
//! 学习目标:
//! - 掌握 Result<T, E> 的使用
//! - 理解 Option<T> 的各种操作
//! - 学会创建自定义错误类型

use serde::{Deserialize, Serialize};
use std::collections::HashMap;
use std::fmt;
use std::num::ParseIntError;

// ==================== 自定义错误类型 ====================

#[derive(Debug, Clone, Serialize, Deserialize)]
pub enum AppError {
    ParseError(String),
    NotFound(String),
    InvalidInput(String),
    IoError(String),
}

impl fmt::Display for AppError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            AppError::ParseError(msg) => write!(f, "解析错误: {}", msg),
            AppError::NotFound(msg) => write!(f, "未找到: {}", msg),
            AppError::InvalidInput(msg) => write!(f, "无效输入: {}", msg),
            AppError::IoError(msg) => write!(f, "IO错误: {}", msg),
        }
    }
}

impl std::error::Error for AppError {}

// 实现 From 转换
impl From<ParseIntError> for AppError {
    fn from(err: ParseIntError) -> Self {
        AppError::ParseError(err.to_string())
    }
}

// ==================== 业务函数示例 ====================

/// 可能失败的解析函数
fn parse_age(input: &str) -> Result<u32, AppError> {
    let age: u32 = input.trim().parse().map_err(|e: ParseIntError| {
        AppError::ParseError(format!("无法解析年龄 '{}': {}", input, e))
    })?;

    if age > 150 {
        return Err(AppError::InvalidInput(format!("年龄 {} 不合理", age)));
    }

    Ok(age)
}

/// 查找用户
fn find_user(id: u32) -> Result<String, AppError> {
    let users = vec![(1, "Alice"), (2, "Bob"), (3, "Charlie")];

    users
        .iter()
        .find(|(uid, _)| *uid == id)
        .map(|(_, name)| name.to_string())
        .ok_or_else(|| AppError::NotFound(format!("用户 ID {}", id)))
}

/// 除法（可能除零）
fn safe_divide(a: f64, b: f64) -> Result<f64, String> {
    if b == 0.0 {
        Err("除数不能为零".to_string())
    } else {
        Ok(a / b)
    }
}

// ==================== Tauri 命令 ====================

/// Result 演示
#[tauri::command]
pub fn demo_result() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "Result<T, E> 类型".to_string(),
        "enum Result<T, E> {\n    Ok(T),   // 成功，包含值\n    Err(E),  // 失败，包含错误\n}\n\n\
         Result 用于表示可能失败的操作".to_string(),
    );

    // 基本使用
    result.insert(
        "基本使用".to_string(),
        format!(
            "// 成功情况\nlet ok: Result<i32, &str> = Ok(42);\n// 失败情况\nlet err: Result<i32, &str> = Err(\"出错了\");\n\n\
             safe_divide(10.0, 2.0) = {:?}\n\
             safe_divide(10.0, 0.0) = {:?}",
            safe_divide(10.0, 2.0),
            safe_divide(10.0, 0.0)
        ),
    );

    // match 处理
    let div_result = safe_divide(10.0, 2.0);
    let match_example = match div_result {
        Ok(value) => format!("结果是: {}", value),
        Err(e) => format!("错误: {}", e),
    };
    result.insert(
        "match 处理".to_string(),
        format!(
            "match safe_divide(10.0, 2.0) {{\n    Ok(value) => format!(\"结果是: {{}}\", value),\n    Err(e) => format!(\"错误: {{}}\", e),\n}}\n\n结果: {}",
            match_example
        ),
    );

    // ? 运算符
    result.insert(
        "? 运算符 (错误传播)".to_string(),
        r#"// ? 运算符：成功则解包，失败则提前返回错误
fn parse_and_double(input: &str) -> Result<i32, ParseIntError> {
    let num: i32 = input.parse()?;  // 失败时自动返回 Err
    Ok(num * 2)
}

// 等价于:
fn parse_and_double_verbose(input: &str) -> Result<i32, ParseIntError> {
    let num: i32 = match input.parse() {
        Ok(n) => n,
        Err(e) => return Err(e),
    };
    Ok(num * 2)
}"#.to_string(),
    );

    // map 和 and_then
    result.insert(
        "map / and_then (函数式处理)".to_string(),
        r#"// map: 转换 Ok 值
let result: Result<i32, &str> = Ok(5);
let doubled = result.map(|x| x * 2);  // Ok(10)

// and_then: 链式操作 (flatMap)
fn parse_and_validate(s: &str) -> Result<i32, String> {
    s.parse::<i32>()
        .map_err(|_| "解析失败".to_string())
        .and_then(|n| {
            if n > 0 { Ok(n) }
            else { Err("必须为正数".to_string()) }
        })
}"#.to_string(),
    );

    // unwrap 和 expect
    result.insert(
        "unwrap / expect (谨慎使用)".to_string(),
        r#"// unwrap: 成功返回值，失败 panic!
let x: Result<i32, &str> = Ok(5);
let value = x.unwrap();  // 5

// expect: 同上，但可自定义 panic 消息
let value = x.expect("应该有值");

// ⚠️ 警告：这些会在 Err 时 panic
// 只在确定不会失败，或原型开发时使用
// 生产代码应该使用 ? 或 match"#.to_string(),
    );

    // 错误转换
    result.insert(
        "错误类型转换".to_string(),
        r#"// 使用 map_err 转换错误类型
fn parse_input(input: &str) -> Result<i32, String> {
    input
        .parse::<i32>()
        .map_err(|e| format!("解析错误: {}", e))
}

// 或实现 From trait 自动转换
impl From<ParseIntError> for MyError {
    fn from(err: ParseIntError) -> Self {
        MyError::Parse(err.to_string())
    }
}"#.to_string(),
    );

    result
}

/// Option 演示
#[tauri::command]
pub fn demo_option() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "Option<T> 类型".to_string(),
        "enum Option<T> {\n    Some(T),  // 有值\n    None,     // 无值\n}\n\n\
         Option 用于表示可能缺失的值，替代 null".to_string(),
    );

    // 基本使用
    let some = Some(42);
    let none: Option<i32> = None;
    result.insert(
        "基本使用".to_string(),
        format!(
            "let some = Some(42);\nlet none: Option<i32> = None;\n\nsome = {:?}\nnone = {:?}",
            some, none
        ),
    );

    // match 处理
    result.insert(
        "match 处理".to_string(),
        r#"match option {
    Some(value) => println!("值: {}", value),
    None => println!("没有值"),
}"#.to_string(),
    );

    // if let
    result.insert(
        "if let 简化".to_string(),
        r#"if let Some(value) = option {
    println!("值: {}", value);
}

// 或使用 let-else (Rust 1.65+)
let Some(value) = option else {
    return;  // 或 break/continue
};"#.to_string(),
    );

    // 常用方法
    let opt = Some(5);
    result.insert(
        "常用方法".to_string(),
        format!(
            r#"let opt = Some(5);

// unwrap_or: 提供默认值
opt.unwrap_or(0);  // 5
None::<i32>.unwrap_or(0);  // 0

// unwrap_or_else: 懒计算默认值
opt.unwrap_or_else(|| {{ 0 }});

// map: 转换 Some 中的值
opt.map(|x| x * 2);  // Some(10)
None::<i32>.map(|x| x * 2);  // None

// and_then: 链式操作
opt.and_then(|x| if x > 0 {{ Some(x) }} else {{ None }});

// or: 提供备选 Option
None.or(Some(10));  // Some(10)

// ok_or: 转换为 Result
opt.ok_or("没有值");  // Ok(5)
None::<i32>.ok_or("没有值");  // Err("没有值")

// 实际运行结果:
opt.unwrap_or(0) = {}
opt.map(|x| x * 2) = {:?}
None.or(Some(10)) = {:?}"#,
            opt.unwrap_or(0),
            opt.map(|x| x * 2),
            None::<i32>.or(Some(10))
        ),
    );

    // 组合器
    result.insert(
        "组合器".to_string(),
        r#"// and: 两个 Option 都是 Some 时返回第二个
Some(2).and(Some(10));  // Some(10)
Some(2).and(None);      // None

// or: 返回第一个 Some
Some(2).or(Some(10));   // Some(2)
None.or(Some(10));      // Some(10)

// filter: 条件过滤
Some(4).filter(|x| x % 2 == 0);  // Some(4)
Some(3).filter(|x| x % 2 == 0);  // None

// zip: 合并两个 Option
Some(1).zip(Some("a"));  // Some((1, "a"))
Some(1).zip(None);       // None"#.to_string(),
    );

    // 最佳实践
    result.insert(
        "Option 最佳实践".to_string(),
        "• 使用 ? 传播 None\n\
         • 使用 map/and_then 而非嵌套 match\n\
         • 提供合理的默认值 (unwrap_or)\n\
         • 避免直接 unwrap，除非确定有值\n\
         • 使用 ok_or 转换为 Result 当需要错误信息".to_string(),
    );

    result
}

/// 自定义错误演示
#[tauri::command]
pub fn demo_custom_errors() -> HashMap<String, String> {
    let mut result = HashMap::new();

    // 简单自定义错误
    result.insert(
        "简单枚举错误".to_string(),
        r#"#[derive(Debug)]
enum MyError {
    NotFound,
    InvalidInput(String),
    NetworkError,
}

impl std::fmt::Display for MyError {
    fn fmt(&self, f: &mut std::fmt::Formatter) -> std::fmt::Result {
        match self {
            MyError::NotFound => write!(f, "资源未找到"),
            MyError::InvalidInput(msg) => write!(f, "无效输入: {}", msg),
            MyError::NetworkError => write!(f, "网络错误"),
        }
    }
}

// 实现 std::error::Error (可选，但推荐)
impl std::error::Error for MyError {}"#.to_string(),
    );

    // 使用 thiserror
    result.insert(
        "使用 thiserror 库 (推荐)".to_string(),
        r#"// Cargo.toml: thiserror = "1.0"

use thiserror::Error;

#[derive(Error, Debug)]
pub enum DataStoreError {
    #[error("数据删除错误")]
    Delete(#[from] std::io::Error),

    #[error("未找到键: {0}")]
    NotFound(String),

    #[error("连接超时: {timeout_ms}ms")]
    Timeout { timeout_ms: u64 },

    #[error("无效数据: {details}")]
    InvalidData { details: String },
}

// #[from] 自动生成 From 实现"#.to_string(),
    );

    // 使用 anyhow
    result.insert(
        "使用 anyhow 库 (应用层)".to_string(),
        r#"// Cargo.toml: anyhow = "1.0"

use anyhow::{Context, Result, anyhow};

fn read_config() -> Result<Config> {
    let content = std::fs::read_to_string("config.toml")
        .context("无法读取配置文件")?;  // 添加上下文

    let config: Config = toml::from_str(&content)
        .context("配置文件格式错误")?;

    Ok(config)
}

// 创建错误
return Err(anyhow!("用户 {} 不存在", user_id));

// anyhow 适合应用程序
// thiserror 适合库"#.to_string(),
    );

    // 实际示例
    let age_result = parse_age("25");
    let invalid_age = parse_age("200");
    let parse_fail = parse_age("abc");

    result.insert(
        "实际示例".to_string(),
        format!(
            r#"fn parse_age(input: &str) -> Result<u32, AppError> {{
    let age: u32 = input.trim().parse()?;
    if age > 150 {{
        return Err(AppError::InvalidInput(format!("年龄 {{}} 不合理", age)));
    }}
    Ok(age)
}}

parse_age("25") = {:?}
parse_age("200") = {:?}
parse_age("abc") = {:?}"#,
            age_result,
            invalid_age,
            parse_fail.map_err(|e| e.to_string())
        ),
    );

    // 错误链
    result.insert(
        "错误链和上下文".to_string(),
        r#"// 使用 ? 传播错误时，添加上下文信息
fn load_user(id: u32) -> Result<User, AppError> {
    let data = fs::read_to_string(format!("users/{}.json", id))
        .map_err(|e| AppError::IoError(format!("读取用户文件失败: {}", e)))?;

    let user: User = serde_json::from_str(&data)
        .map_err(|e| AppError::ParseError(format!("解析用户数据失败: {}", e)))?;

    Ok(user)
}"#.to_string(),
    );

    result.insert(
        "错误处理最佳实践".to_string(),
        "• 库使用 thiserror，应用使用 anyhow\n\
         • 为错误提供有意义的上下文\n\
         • 使用 ? 运算符传播错误\n\
         • 实现 From trait 简化错误转换\n\
         • 在适当层级处理错误（不要到处 unwrap）\n\
         • 记录错误日志以便调试".to_string(),
    );

    result
}
