//! Level 9: 文件操作和序列化

use serde::{Deserialize, Serialize};
use std::collections::HashMap;

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct User {
    pub id: u32,
    pub name: String,
    pub email: String,
}

#[tauri::command]
pub fn demo_file_io() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "写入文件".to_string(),
        "use std::fs;\n\nfs::write(\"hello.txt\", \"Hello, World!\").unwrap();\n\n// 追加写入\nuse std::fs::OpenOptions;\nlet mut file = OpenOptions::new().append(true).open(\"hello.txt\").unwrap();".to_string()
    );

    result.insert(
        "读取文件".to_string(),
        "use std::fs;\n\n// 读取全部\nlet content = fs::read_to_string(\"hello.txt\").unwrap();\n\n// 逐行读取\nuse std::io::BufRead;\nfor line in std::io::BufReader::new(file).lines() {\n    println!(\"{}\", line.unwrap());\n}".to_string()
    );

    result.insert(
        "文件操作".to_string(),
        "fs::copy(\"src\", \"dst\") - 复制\nfs::rename(\"old\", \"new\") - 重命名\nfs::remove_file(\"path\") - 删除\nfs::create_dir(\"path\") - 创建目录".to_string()
    );

    result
}

#[tauri::command]
pub fn demo_json() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "Serde 简介".to_string(),
        "Serde 是 Rust 的序列化框架，支持 JSON、YAML、TOML 等格式。\n\n依赖: serde = { version = \"1\", features = [\"derive\"] }\n       serde_json = \"1\"".to_string()
    );

    result.insert(
        "序列化 (Rust -> JSON)".to_string(),
        "#[derive(Serialize)]\nstruct User {\n    id: u32,\n    name: String,\n}\n\nlet user = User { id: 1, name: \"Alice\".into() };\nlet json = serde_json::to_string(&user).unwrap();\n// {\"id\":1,\"name\":\"Alice\"}".to_string()
    );

    result.insert(
        "反序列化 (JSON -> Rust)".to_string(),
        "#[derive(Deserialize)]\nstruct User {\n    id: u32,\n    name: String,\n}\n\nlet json = r#\"{\"id\":1,\"name\":\"Alice\"}\"#;\nlet user: User = serde_json::from_str(json).unwrap();".to_string()
    );

    result.insert(
        "文件读写".to_string(),
        "use std::fs::File;\n\n// 读取\nlet file = File::open(\"user.json\").unwrap();\nlet user: User = serde_json::from_reader(file).unwrap();\n\n// 写入\nlet file = File::create(\"user.json\").unwrap();\nserde_json::to_writer(file, &user).unwrap();".to_string()
    );

    result
}

#[tauri::command]
pub fn demo_path_operations() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "Path vs PathBuf".to_string(),
        "Path: 类似 &str，是借用\nPathBuf: 类似 String，拥有所有权\n\nlet path = Path::new(\"src/main.rs\");\nlet mut buf = PathBuf::new();\nbuf.push(\"src\");\nbuf.push(\"main.rs\");".to_string()
    );

    result.insert(
        "路径组件".to_string(),
        "path.file_name() - 文件名\npath.extension() - 扩展名\npath.parent() - 父目录\npath.file_stem() - 无扩展名的文件名".to_string()
    );

    result.insert(
        "路径检查".to_string(),
        "path.exists() - 是否存在\npath.is_file() - 是否为文件\npath.is_dir() - 是否为目录\npath.is_absolute() - 是否绝对路径".to_string()
    );

    result.insert(
        "遍历目录".to_string(),
        "use std::fs;\n\nfor entry in fs::read_dir(\"src\").unwrap() {\n    let entry = entry.unwrap();\n    println!(\"{:?}\", entry.path());\n}".to_string()
    );

    result
}
