//! Level 2: 函数和控制流
//!
//! 学习目标:
//! - 掌握函数定义和参数传递
//! - 理解表达式和语句的区别
//! - 熟悉各种控制流结构

use serde::{Deserialize, Serialize};
use std::collections::HashMap;

#[derive(Debug, Serialize, Deserialize)]
pub struct FunctionDemo {
    pub concept: String,
    pub code: String,
    pub result: String,
}

/// 函数演示
#[tauri::command]
pub fn demo_functions() -> Vec<FunctionDemo> {
    vec![
        // 基本函数
        FunctionDemo {
            concept: "基本函数定义".to_string(),
            code: r#"fn greet(name: &str) -> String {
    format!("Hello, {}!", name)
}"#
            .to_string(),
            result: greet("Rustacean"),
        },
        // 表达式作为返回值
        FunctionDemo {
            concept: "表达式返回值 (无return关键字)".to_string(),
            code: r#"fn add(a: i32, b: i32) -> i32 {
    a + b  // 注意：没有分号，这是表达式
}"#
            .to_string(),
            result: format!("5 + 3 = {}", add(5, 3)),
        },
        // 提前返回
        FunctionDemo {
            concept: "提前返回 (return关键字)".to_string(),
            code: r#"fn abs(x: i32) -> i32 {
    if x < 0 {
        return -x;  // 提前返回
    }
    x  // 正常返回
}"#
            .to_string(),
            result: format!("|-42| = {}", abs(-42)),
        },
        // 发散函数
        FunctionDemo {
            concept: "发散函数 (永不返回)".to_string(),
            code: r#"fn infinite_loop() -> ! {
    loop {
        // 永不返回，类型为 !
    }
}

fn panic_example() -> ! {
    panic!("程序崩溃！");
}"#
            .to_string(),
            result: "发散函数用 ! 作为返回类型，表示永不返回".to_string(),
        },
        // 函数指针
        FunctionDemo {
            concept: "函数指针".to_string(),
            code: r#"fn apply(f: fn(i32, i32) -> i32, a: i32, b: i32) -> i32 {
    f(a, b)
}

let result = apply(add, 10, 20);"#
                .to_string(),
            result: format!("apply(add, 10, 20) = {}", apply(add, 10, 20)),
        },
        // 闭包
        FunctionDemo {
            concept: "闭包 (匿名函数)".to_string(),
            code: r#"// 闭包语法: |参数| { 函数体 }
let add = |a, b| a + b;
let greet = |name| format!("Hi, {}!", name);

// 捕获环境变量
let multiplier = 10;
let multiply = |x| x * multiplier;"#
                .to_string(),
            result: {
                let multiplier = 10;
                let multiply = |x| x * multiplier;
                format!("闭包捕获环境变量: multiply(5) = {}", multiply(5))
            },
        },
        // 迭代器方法作为高阶函数
        FunctionDemo {
            concept: "迭代器和高阶函数".to_string(),
            code: r#"let numbers = vec![1, 2, 3, 4, 5];

// map: 转换每个元素
let doubled: Vec<i32> = numbers.iter().map(|x| x * 2).collect();

// filter: 过滤元素
let evens: Vec<&i32> = numbers.iter().filter(|x| *x % 2 == 0).collect();

// fold: 累积计算
let sum: i32 = numbers.iter().fold(0, |acc, x| acc + x);"#
                .to_string(),
            result: {
                let numbers = vec![1, 2, 3, 4, 5];
                let doubled: Vec<i32> = numbers.iter().map(|x| x * 2).collect();
                let sum: i32 = numbers.iter().fold(0, |acc, x| acc + x);
                format!(
                    "原数组: {:?}\n翻倍: {:?}\n求和: {}",
                    numbers, doubled, sum
                )
            },
        },
    ]
}

// 辅助函数
fn greet(name: &str) -> String {
    format!("Hello, {}!", name)
}

fn add(a: i32, b: i32) -> i32 {
    a + b
}

fn abs(x: i32) -> i32 {
    if x < 0 {
        return -x;
    }
    x
}

fn apply(f: fn(i32, i32) -> i32, a: i32, b: i32) -> i32 {
    f(a, b)
}

/// 控制流演示
#[tauri::command]
pub fn demo_control_flow() -> HashMap<String, String> {
    let mut result = HashMap::new();

    // if 表达式
    let number = 7;
    let if_result = if number < 5 {
        "小于5"
    } else if number < 10 {
        "5到10之间"
    } else {
        "大于等于10"
    };
    result.insert(
        "if 表达式".to_string(),
        format!("代码:\nlet result = if number < 5 {{\n    \"小于5\"\n}} else if number < 10 {{\n    \"5到10之间\"\n}} else {{\n    \"大于等于10\"\n}};\n\n结果: {}", if_result),
    );

    // if 在 let 语句中
    let condition = true;
    let ternary_like = if condition { 1 } else { 0 };
    result.insert(
        "if 作为表达式 (类似三元运算符)".to_string(),
        format!("let x = if true {{ 1 }} else {{ 0 }}; // x = {}", ternary_like),
    );

    // match 表达式
    let grade = 'B';
    let grade_result = match grade {
        'A' => "优秀",
        'B' => "良好",
        'C' => "及格",
        'D' => "不及格",
        _ => "未知等级", // _ 是通配符
    };
    result.insert(
        "match 表达式".to_string(),
        format!("match '{}' {{\n    'A' => \"优秀\",\n    'B' => \"良好\",\n    'C' => \"及格\",\n    'D' => \"不及格\",\n    _ => \"未知等级\",\n}}\n\n结果: {}", grade, grade_result),
    );

    // match 带守卫
    let num = 15;
    let guard_result = match num {
        n if n < 10 => "小于10",
        n if n < 20 => "10到20之间",
        _ => "大于等于20",
    };
    result.insert(
        "match 守卫".to_string(),
        format!("match {} {{\n    n if n < 10 => \"小于10\",\n    n if n < 20 => \"10到20之间\",\n    _ => \"大于等于20\",\n}}\n\n结果: {}", num, guard_result),
    );

    // if let 语法糖
    let some_value = Some(42);
    let if_let_result = if let Some(x) = some_value {
        format!("值为: {}", x)
    } else {
        "没有值".to_string()
    };
    result.insert(
        "if let (模式匹配简化)".to_string(),
        format!("if let Some(x) = {:?} {{\n    \"值为: {{}}\", x\n}}\n\n结果: {}", some_value, if_let_result),
    );

    // let-else 模式 (Rust 1.65+)
    result.insert(
        "let-else 模式".to_string(),
        r#"// Rust 1.65+ 的 let-else 语法
let Some(x) = some_value else {
    return; // 或 break/continue/panic
};
// 这里 x 已经解构出来了"#.to_string(),
    );

    result
}

/// 循环演示
#[tauri::command]
pub fn demo_loops() -> HashMap<String, String> {
    let mut result = HashMap::new();

    // loop
    let mut count = 0;
    let loop_result = loop {
        count += 1;
        if count == 3 {
            break count * 10; // loop 可以返回值
        }
    };
    result.insert(
        "loop 无限循环".to_string(),
        format!(
            "let mut count = 0;\nlet result = loop {{\n    count += 1;\n    if count == 3 {{\n        break count * 10;\n    }}\n}};\n\n结果: {}",
            loop_result
        ),
    );

    // loop 标签
    let mut outer = 0;
    let nested_result = 'outer: loop {
        let mut inner = 0;
        loop {
            inner += 1;
            if inner == 2 {
                break; // 只跳出内层
            }
        }
        outer += 1;
        if outer == 2 {
            break 'outer (outer, 0); // 跳出外层并返回值
        }
    };
    result.insert(
        "loop 标签".to_string(),
        format!("使用 'outer: 标签来跳出嵌套循环\n结果: {:?}", nested_result),
    );

    // while
    let mut n = 5;
    let mut while_output = Vec::new();
    while n > 0 {
        while_output.push(n);
        n -= 1;
    }
    result.insert(
        "while 条件循环".to_string(),
        format!("let mut n = 5;\nwhile n > 0 {{\n    vec.push(n);\n    n -= 1;\n}}\n\n结果: {:?}", while_output),
    );

    // for range
    let for_range: Vec<i32> = (1..=5).collect();
    result.insert(
        "for 范围迭代".to_string(),
        format!(
            "(1..=5) 包含5: {:?}\n(1..5) 不包含5: {:?}",
            for_range,
            (1..5).collect::<Vec<i32>>()
        ),
    );

    // for 迭代器
    let arr = [10, 20, 30];
    let for_iter: Vec<(usize, i32)> = arr.iter().enumerate().map(|(i, &v)| (i, v)).collect();
    result.insert(
        "for 迭代数组".to_string(),
        format!(
            "for (index, value) in arr.iter().enumerate() {{ ... }}\n结果: {:?}",
            for_iter
        ),
    );

    // for 反向迭代
    let reverse: Vec<i32> = (1..=5).rev().collect();
    result.insert("反向迭代".to_string(), format!("(1..=5).rev(): {:?}", reverse));

    // continue 和 break
    let skip_odds: Vec<i32> = (1..=10).filter(|x| x % 2 == 0).collect();
    result.insert(
        "跳过奇数 (continue等效)".to_string(),
        format!("filter(|x| x % 2 == 0): {:?}", skip_odds),
    );

    result.insert(
        "循环最佳实践".to_string(),
        "• 优先使用 for 循环迭代集合\n\
         • 需要索引时使用 .enumerate()\n\
         • 避免在循环中修改正在迭代的集合\n\
         • 使用迭代器方法 (map, filter, fold) 替代显式循环\n\
         • loop 适合不确定迭代次数的情况"
            .to_string(),
    );

    result
}
