//! 引擎信息与最小异步示例：验证 FFI 通路的最简接缝。

/// Rust 引擎自描述信息
#[derive(Debug, Clone)]
pub struct EngineInfo {
    pub name: String,
    pub version: String,
    /// 编译目标三元组，如 aarch64-apple-darwin
    pub target: String,
    /// 是否为 debug 构建
    pub debug_build: bool,
}

/// 同步获取引擎信息（`#[frb(sync)]`：调用零开销，不走异步调度）
#[flutter_rust_bridge::frb(sync)]
pub fn engine_info() -> EngineInfo {
    EngineInfo {
        name: "rust-lab-engine".to_string(),
        version: env!("CARGO_PKG_VERSION").to_string(),
        target: std::env::var("TARGET").unwrap_or_else(|_| option_env!("TARGET").unwrap_or("unknown").to_string()),
        debug_build: cfg!(debug_assertions),
    }
}

/// 最小异步示例：延迟后返回 x 的平方（验证 async 跨桥通路）
pub async fn slow_square(x: i64, delay_ms: i64) -> i64 {
    tokio::time::sleep(std::time::Duration::from_millis(delay_ms.clamp(0, 10_000) as u64)).await;
    x.wrapping_mul(x)
}

// ==================== 测试 ====================

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn engine_info_has_expected_name_and_version() {
        let info = engine_info();
        assert_eq!(info.name, "rust-lab-engine");
        assert_eq!(info.version, "0.1.0");
        assert!(!info.name.is_empty());
    }

    #[tokio::test]
    async fn slow_square_computes() {
        assert_eq!(slow_square(7, 0).await, 49);
        assert_eq!(slow_square(-3, 0).await, 9);
        assert_eq!(slow_square(0, 0).await, 0);
    }
}
