//! Rust 从简单到复杂的学习 Demo
//!
//! 这个项目展示了 Rust 语言从基础到高级的各种概念
//! 通过 Tauri 桌面应用进行交互式演示

// Prevents additional console window on Windows in release, DO NOT REMOVE!!
#![windows_subsystem = "windows"]

mod examples;

use examples::{
    level1_basics, level2_functions, level3_ownership, level4_structs,
    level5_traits, level6_errors, level7_collections, level8_concurrency,
    level9_files, level10_real_world,
};

/// 应用程序主入口
fn main() {
    tauri::Builder::default()
        .plugin(tauri_plugin_shell::init())
        .invoke_handler(tauri::generate_handler![
            // Level 1: 基础类型和变量
            level1_basics::demo_variables,
            level1_basics::demo_types,
            level1_basics::demo_strings,

            // Level 2: 函数和控制流
            level2_functions::demo_functions,
            level2_functions::demo_control_flow,
            level2_functions::demo_loops,

            // Level 3: 所有权和借用
            level3_ownership::demo_ownership,
            level3_ownership::demo_borrowing,
            level3_ownership::demo_lifetimes,

            // Level 4: 结构体和枚举
            level4_structs::demo_structs,
            level4_structs::demo_enums,
            level4_structs::demo_pattern_matching,

            // Level 5: Trait 和泛型
            level5_traits::demo_traits,
            level5_traits::demo_generics,
            level5_traits::demo_trait_bounds,

            // Level 6: 错误处理
            level6_errors::demo_result,
            level6_errors::demo_option,
            level6_errors::demo_custom_errors,

            // Level 7: 集合和迭代器
            level7_collections::demo_vectors,
            level7_collections::demo_hashmaps,
            level7_collections::demo_iterators,

            // Level 8: 并发和异步
            level8_concurrency::demo_threads,
            level8_concurrency::demo_channels,
            level8_concurrency::demo_async,

            // Level 9: 文件操作和序列化
            level9_files::demo_file_io,
            level9_files::demo_json,
            level9_files::demo_path_operations,

            // Level 10: 实际应用示例
            level10_real_world::demo_api_client,
            level10_real_world::demo_cache,
            level10_real_world::demo_state_machine,
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
