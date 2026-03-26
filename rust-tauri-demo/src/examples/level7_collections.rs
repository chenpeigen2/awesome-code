//! Level 7: 集合和迭代器

use serde::{Deserialize, Serialize};
use std::collections::{HashMap, HashSet};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct CollectionDemo {
    pub title: String,
    pub description: String,
    pub code_example: String,
    pub output: String,
}

#[tauri::command]
pub fn demo_vectors() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "创建 Vector".to_string(),
        "let v1 = vec![1, 2, 3, 4, 5];\nlet mut v2: Vec<i32> = Vec::new();\nv2.push(1);".to_string()
    );

    result.insert(
        "访问元素".to_string(),
        "let v = vec![10, 20, 30];\nv[0] = 10\nv.get(0) = Some(10)".to_string()
    );

    result.insert(
        "常用操作".to_string(),
        "v.push(4) - 添加元素\nv.pop() - 移除最后一个\nv.len() - 获取长度\nv.contains(&2) - 检查包含".to_string()
    );

    result.insert(
        "性能技巧".to_string(),
        "使用 Vec::with_capacity() 预分配容量\n避免频繁在开头插入\nextend 比循环 push 更快".to_string()
    );

    result
}

#[tauri::command]
pub fn demo_hashmaps() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "创建 HashMap".to_string(),
        "let mut map: HashMap<String, i32> = HashMap::new();\nmap.insert(\"key\".to_string(), 42);".to_string()
    );

    result.insert(
        "访问和更新".to_string(),
        "map.get(\"key\") - 获取值\nmap.entry(\"key\").or_insert(0) - 不存在时插入\nmap.remove(\"key\") - 删除".to_string()
    );

    result.insert(
        "HashSet 集合".to_string(),
        "let set: HashSet<i32> = [1, 2, 3].iter().cloned().collect();\nset.union(&other) - 并集\nset.intersection(&other) - 交集".to_string()
    );

    result
}

#[tauri::command]
pub fn demo_iterators() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "什么是迭代器".to_string(),
        "迭代器实现了 Iterator trait，提供惰性求值的元素序列。零成本抽象！".to_string()
    );

    result.insert(
        "常用适配器".to_string(),
        ".map(|x| x * 2) - 转换\n.filter(|x| *x > 0) - 过滤\n.take(5) - 取前n个\n.skip(2) - 跳过前n个".to_string()
    );

    result.insert(
        "消费适配器".to_string(),
        ".collect::<Vec<_>>() - 收集\n.sum::<i32>() - 求和\n.fold(0, |acc, x| acc + x) - 累积\n.for_each(|x| {}) - 遍历".to_string()
    );

    result.insert(
        "链式调用示例".to_string(),
        "let sum: i32 = (1..=10)\n    .filter(|x| x % 2 == 0)\n    .map(|x| x * x)\n    .sum();\n// 结果: 220".to_string()
    );

    result
}
