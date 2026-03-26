//! Level 5: Trait 和泛型
//!
//! 学习目标:
//! - 理解 Trait 的定义和实现
//! - 掌握泛型编程
//! - 熟悉 Trait Bounds

use serde::{Deserialize, Serialize};
use std::collections::HashMap;
use std::fmt;

// ==================== Trait 定义 ====================

/// 自定义 Trait: 描述
pub trait Describe {
    fn describe(&self) -> String;

    // 默认实现
    fn describe_short(&self) -> String {
        self.describe()
    }
}

/// 自定义 Trait: 面积
pub trait Area {
    fn area(&self) -> f64;
}

/// 自定义 Trait: 缩放
pub trait Scale {
    fn scale(&mut self, factor: f64);
}

// ==================== 实现类型 ====================

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Circle {
    pub radius: f64,
}

impl Area for Circle {
    fn area(&self) -> f64 {
        std::f64::consts::PI * self.radius.powi(2)
    }
}

impl Scale for Circle {
    fn scale(&mut self, factor: f64) {
        self.radius *= factor;
    }
}

impl Describe for Circle {
    fn describe(&self) -> String {
        format!("圆形，半径: {:.2}", self.radius)
    }
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Rectangle {
    pub width: f64,
    pub height: f64,
}

impl Area for Rectangle {
    fn area(&self) -> f64 {
        self.width * self.height
    }
}

impl Scale for Rectangle {
    fn scale(&mut self, factor: f64) {
        self.width *= factor;
        self.height *= factor;
    }
}

impl Describe for Rectangle {
    fn describe(&self) -> String {
        format!("矩形，宽: {:.2}, 高: {:.2}", self.width, self.height)
    }
}

// ==================== 泛型结构体 ====================

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Container<T> {
    pub value: T,
}

impl<T: fmt::Display> Describe for Container<T> {
    fn describe(&self) -> String {
        format!("容器包含: {}", self.value)
    }
}

// 多泛型参数
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Pair<T, U> {
    pub first: T,
    pub second: U,
}

// ==================== 泛型函数 ====================

/// 泛型函数示例
pub fn largest<T: PartialOrd>(list: &[T]) -> Option<&T> {
    if list.is_empty() {
        return None;
    }
    let mut largest = &list[0];
    for item in list {
        if item > largest {
            largest = item;
        }
    }
    Some(largest)
}

/// 多 Trait Bound 函数
pub fn compare_and_describe<T>(a: &T, b: &T) -> String
where
    T: Describe + PartialOrd,
{
    let comparison = if a > b { "大于" } else { "小于等于" };
    format!("{} {} {}", a.describe(), comparison, b.describe())
}

// ==================== Tauri 命令 ====================

/// Trait 演示
#[tauri::command]
pub fn demo_traits() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "什么是 Trait？".to_string(),
        "Trait 定义了类型共享的行为，类似于其他语言的接口。\n\
         但 Trait 更强大：支持默认实现、关联类型、关联常量等。".to_string(),
    );

    // 定义和实现
    result.insert(
        "定义和实现 Trait".to_string(),
        r#"// 定义 Trait
pub trait Describe {
    fn describe(&self) -> String;

    // 默认实现
    fn describe_short(&self) -> String {
        self.describe()
    }
}

// 为类型实现 Trait
impl Describe for Circle {
    fn describe(&self) -> String {
        format!("圆形，半径: {:.2}", self.radius)
    }
}"#.to_string(),
    );

    // Trait 作为参数
    let shapes: Vec<Box<dyn Area>> = vec![
        Box::new(Circle { radius: 5.0 }),
        Box::new(Rectangle { width: 4.0, height: 3.0 }),
    ];
    let total_area: f64 = shapes.iter().map(|s| s.area()).sum();
    result.insert(
        "Trait 对象 (动态分发)".to_string(),
        format!(
            "let shapes: Vec<Box<dyn Area>> = vec![\n    Box::new(Circle {{ radius: 5.0 }}),\n    Box::new(Rectangle {{ width: 4.0, height: 3.0 }}),\n];\n\n总面积: {:.2}",
            total_area
        ),
    );

    // 默认实现
    result.insert(
        "Trait 默认实现".to_string(),
        r#"trait Summary {
    fn summarize(&self) -> String {
        String::from("(阅读更多...)")  // 默认实现
    }
}

impl Summary for Article {
    // 可以不实现 summarize，使用默认版本
}"#.to_string(),
    );

    // Trait Bound
    result.insert(
        "Trait Bound 语法".to_string(),
        r#"// 方式1: 泛型约束
fn notify<T: Summary>(item: &T) {
    println!("{}", item.summarize());
}

// 方式2: where 子句（推荐用于复杂约束）
fn some_function<T, U>(t: &T, u: &U)
where
    T: Display + Clone,
    U: Clone + Debug,
{
    // ...
}"#.to_string(),
    );

    // 标准 Trait
    result.insert(
        "常用标准 Trait".to_string(),
        "• Debug: 调试输出 {:?}\n\
         • Display: 用户显示 {}\n\
         • Clone: 显式克隆 .clone()\n\
         • Copy: 隐式复制\n\
         • PartialEq/Eq: 相等比较\n\
         • PartialOrd/Ord: 排序比较\n\
         • Hash: 哈希计算\n\
         • Default: 默认值\n\
         • From/Into: 类型转换\n\
         • AsRef/AsMut: 引用转换\n\
         • Deref/DerefMut: 智能指针".to_string(),
    );

    // 关联类型
    result.insert(
        "关联类型".to_string(),
        r#"trait Container {
    type Item;  // 关联类型

    fn get(&self) -> Option<&Self::Item>;
    fn add(&mut self, item: Self::Item);
}

impl Container for MyBox {
    type Item = i32;  // 指定具体类型

    fn get(&self) -> Option<&Self::Item> {
        self.value.as_ref()
    }
    // ...
}"#.to_string(),
    );

    result
}

/// 泛型演示
#[tauri::command]
pub fn demo_generics() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "什么是泛型？".to_string(),
        "泛型允许编写适用于多种类型的代码，\n\
         同时保持类型安全和性能（单态化，零成本抽象）。".to_string(),
    );

    // 泛型函数
    let numbers = vec![34, 50, 12, 100, 65];
    let largest_num = largest(&numbers);
    let chars = vec!['y', 'm', 'a', 'q'];
    let largest_char = largest(&chars);

    result.insert(
        "泛型函数".to_string(),
        format!(
            "fn largest<T: PartialOrd>(list: &[T]) -> Option<&T> {{ ... }}\n\n\
             largest(&[34, 50, 12, 100, 65]) = {:?}\n\
             largest(&['y', 'm', 'a', 'q']) = {:?}",
            largest_num, largest_char
        ),
    );

    // 泛型结构体
    let int_container = Container { value: 42 };
    let str_container = Container { value: String::from("hello") };

    result.insert(
        "泛型结构体".to_string(),
        format!(
            "struct Container<T> {{\n    value: T,\n}}\n\n\
             Container {{ value: 42 }} -> {}\n\
             Container {{ value: String::from(\"hello\") }} -> {}",
            int_container.value, str_container.value
        ),
    );

    // 多泛型参数
    let pair = Pair {
        first: 1,
        second: "two",
    };
    result.insert(
        "多泛型参数".to_string(),
        format!(
            "struct Pair<T, U> {{\n    first: T,\n    second: U,\n}}\n\n\
             Pair {{ first: 1, second: \"two\" }} -> Pair {{ first: {}, second: \"{}\" }}",
            pair.first, pair.second
        ),
    );

    // 泛型枚举
    result.insert(
        "泛型枚举".to_string(),
        r#"// 标准库中的泛型枚举
enum Option<T> {
    Some(T),
    None,
}

enum Result<T, E> {
    Ok(T),
    Err(E),
}

// 使用示例
let some_number: Option<i32> = Some(5);
let some_char: Option<char> = Some('e');
let absent_number: Option<i32> = None;"#.to_string(),
    );

    // 单态化
    result.insert(
        "单态化 (Monomorphization)".to_string(),
        "编译时，泛型代码会为每个具体类型生成专用版本。\n\
         这意味着泛型没有运行时开销！\n\n\
         例如: largest::<i32> 和 largest::<char>\n\
         会编译成两个不同的函数。".to_string(),
    );

    result
}

/// Trait Bounds 演示
#[tauri::command]
pub fn demo_trait_bounds() -> HashMap<String, String> {
    let mut result = HashMap::new();

    // 基本 Trait Bound
    result.insert(
        "基本 Trait Bound".to_string(),
        r#"// T 必须实现 Display trait
fn print_it<T: std::fmt::Display>(item: T) {
    println!("{}", item);
}

print_it(42);       // i32 实现了 Display
print_it("hello");  // &str 也实现了 Display"#.to_string(),
    );

    // 多重 Trait Bound
    result.insert(
        "多重 Trait Bound (+)".to_string(),
        r#"// T 必须同时实现 Display 和 Clone
fn notify<T: std::fmt::Display + Clone>(item: T) {
    let cloned = item.clone();
    println!("{}", cloned);
}"#.to_string(),
    );

    // where 子句
    result.insert(
        "where 子句 (推荐)".to_string(),
        r#"// 复杂约束时，使用 where 更清晰
fn some_function<T, U>(t: &T, u: &U) -> i32
where
    T: std::fmt::Display + Clone,
    U: Clone + std::fmt::Debug,
{
    // 函数体
    0
}

// 等价于:
// fn some_function<T: Display + Clone, U: Clone + Debug>(t: &T, u: &U) -> i32"#.to_string(),
    );

    // 有条件实现方法
    result.insert(
        "有条件实现方法".to_string(),
        r#"struct Pair<T> {
    x: T,
    y: T,
}

impl<T> Pair<T> {
    fn new(x: T, y: T) -> Self {
        Self { x, y }
    }
}

// 只有当 T 实现了 Display + PartialOrd 时才有这个方法
impl<T: std::fmt::Display + PartialOrd> Pair<T> {
    fn cmp_display(&self) {
        if self.x >= self.y {
            println!("最大的是 {}", self.x);
        } else {
            println!("最大的是 {}", self.y);
        }
    }
}"#.to_string(),
    );

    // Blanket Implementation
    result.insert(
        "Blanket Implementation (覆盖实现)".to_string(),
        r#"// 为所有实现了 Display 的类型自动实现 ToString
impl<T: std::fmt::Display> ToString for T {
    fn to_string(&self) -> String {
        // ...
    }
}

// 这就是为什么任何实现了 Display 的类型
// 都可以调用 .to_string() 方法！"#.to_string(),
    );

    // Newtype 模式
    result.insert(
        "Newtype 模式绕过孤儿规则".to_string(),
        r#"// 孤儿规则：不能为外部类型实现外部 trait
// 解决方法：使用 newtype 模式

struct Wrapper(Vec<String>);

impl std::fmt::Display for Wrapper {
    fn fmt(&self, f: &mut std::fmt::Formatter) -> std::fmt::Result {
        write!(f, "[{}]", self.0.join(", "))
    }
}

let w = Wrapper(vec!["hello".to_string(), "world".to_string()]);
println!("{}", w);  // 输出: [hello, world]"#.to_string(),
    );

    result.insert(
        "Trait Bound 最佳实践".to_string(),
        "• 只约束真正需要的 trait\n\
         • 复杂约束使用 where 子句\n\
         • 考虑使用 Trait 对象 (dyn Trait) 减少代码膨胀\n\
         • 了解标准库的 blanket implementations\n\
         • 使用 newtype 模式解决孤儿规则".to_string(),
    );

    result
}
