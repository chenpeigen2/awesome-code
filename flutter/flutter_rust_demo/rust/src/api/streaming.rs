//! 流式任务：通过 FRB 的 StreamSink 向 Dart 持续推送进度事件。
//!
//! 演示任务：蒙特卡洛法估算 π —— 均匀撒点于单位正方形，
//! 落入四分之一圆的比例 × 4 ≈ π。
//!
//! 展示：Rust → Dart 的多事件流、后台线程、分块上报进度。

/// 进度事件（Dart 侧生成为 sealed class ProgressEvent_*）
#[derive(Debug, Clone)]
pub enum ProgressEvent {
    /// 阶段性进度
    Progress {
        /// 已完成采样数
        done: i64,
        /// 总采样数
        total: i64,
        /// 当前部分估算值
        partial_estimate: f64,
    },
    /// 任务完成，带最终估算值与耗时
    Done {
        estimate: f64,
        /// 与真值 π 的绝对误差
        abs_error: f64,
        elapsed_ms: i64,
    },
}

/// 确定性线性同余发生器（LCG）：
/// 选用与 glibc / Windows 不同的参数组合（Musl 风格），跨平台结果一致。
struct Lcg(u64);

impl Lcg {
    fn next_u64(&mut self) -> u64 {
        self.0 = self
            .0
            .wrapping_mul(6364136223846793005)
            .wrapping_add(1442695040888963407);
        self.0
    }

    /// 伪随机 [0, 1) 浮点
    fn next_f64(&mut self) -> f64 {
        // 取高 53 位保证精度
        (self.next_u64() >> 11) as f64 / (1u64 << 53) as f64
    }
}

/// 蒙特卡洛核心：采样 iterations 个点，返回落入四分之一圆内的点数。
/// 确定性 —— 同一 (iterations, seed) 永远返回同一结果，便于测试与复现。
pub fn count_inside(iterations: u64, seed: u64) -> u64 {
    if iterations == 0 {
        return 0;
    }
    let mut rng = Lcg(seed ^ 0x9E3779B97F4A7C15);
    let mut inside = 0u64;
    for _ in 0..iterations {
        let x = rng.next_f64();
        let y = rng.next_f64();
        if x * x + y * y <= 1.0 {
            inside += 1;
        }
    }
    inside
}

/// 由落入点数估算 π
pub fn pi_from_counts(inside: u64, total: u64) -> f64 {
    if total == 0 {
        0.0
    } else {
        4.0 * inside as f64 / total as f64
    }
}

/// 便捷入口：一次估算完
pub fn estimate_pi(iterations: u64, seed: u64) -> f64 {
    pi_from_counts(count_inside(iterations, seed), iterations)
}

/// 带进度回调的运行：每完成一个 chunk 调用一次 on_progress，
/// 回调携带的是**合并所有 chunk 后的累计估算值**（而非单块值）。
/// 流函数本身只是薄薄的胶水层，核心逻辑在这里，可脱离 FRB 单测。
pub fn run_monte_carlo(
    iterations: u64,
    chunks: u32,
    mut on_progress: impl FnMut(u64, u64, f64),
) -> (f64, i64) {
    let start = std::time::Instant::now();
    let chunks = chunks.clamp(1, 1000) as u64;
    let per_chunk = (iterations / chunks).max(1);
    let mut done: u64 = 0;
    let mut inside: u64 = 0;
    while done < iterations {
        let step = per_chunk.min(iterations - done);
        inside += count_inside(step, 0xC0FFEE ^ done);
        done += step;
        on_progress(done, iterations, pi_from_counts(inside, done));
    }
    (pi_from_counts(inside, done), start.elapsed().as_millis() as i64)
}

/// 流式入口：Dart 侧 `monteCarloPi(...)` 订阅 ProgressEvent 流。
/// 在独立线程运行，不阻塞 UI 线程，也不阻塞 Rust 线程池。
pub fn monte_carlo_pi(
    sink: crate::frb_generated::StreamSink<ProgressEvent>,
    iterations: i64,
    chunks: i32,
) -> Result<(), anyhow::Error> {
    std::thread::spawn(move || {
        let (estimate, elapsed_ms) = run_monte_carlo(iterations.max(0) as u64, chunks.max(1) as u32, |done, total, partial| {
            let _ = sink.add(ProgressEvent::Progress {
                done: done as i64,
                total: total as i64,
                partial_estimate: partial,
            });
        });
        let _ = sink.add(ProgressEvent::Done {
            estimate,
            abs_error: (estimate - std::f64::consts::PI).abs(),
            elapsed_ms,
        });
    });
    Ok(())
}

// ==================== 测试 ====================
// 预期值来自独立真值：数学常数 π、蒙特卡洛误差的统计界。

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn lcg_is_deterministic() {
        let mut a = Lcg(42);
        let mut b = Lcg(42);
        for _ in 0..1000 {
            assert_eq!(a.next_u64(), b.next_u64());
        }
        let mut c = Lcg(42);
        c.next_u64();
        let mut d = Lcg(43);
        // 不同种子大概率产生不同序列（首个值碰撞概率 ≈ 2⁻⁶⁴）
        assert_ne!(c.next_u64(), d.next_u64());
    }

    #[test]
    fn lcg_uniform_in_range() {
        let mut rng = Lcg(7);
        for _ in 0..10_000 {
            let x = rng.next_f64();
            assert!((0.0..1.0).contains(&x));
        }
    }

    #[test]
    fn zero_iterations_gives_zero() {
        assert_eq!(estimate_pi(0, 1), 0.0);
    }

    /// 统计界：N 次采样的估计误差标准差 ≈ 1.64/√N。
    /// N = 200_000 时 σ ≈ 0.0037，断言 |误差| < 0.05 约为 13σ，安全。
    #[test]
    fn estimate_converges_to_pi() {
        for seed in [1u64, 42, 0xDEADBEEF] {
            let est = estimate_pi(200_000, seed);
            assert!(
                (est - std::f64::consts::PI).abs() < 0.05,
                "seed={seed} estimate={est}"
            );
        }
    }

    #[test]
    fn estimate_is_reproducible() {
        assert_eq!(estimate_pi(50_000, 123), estimate_pi(50_000, 123));
    }

    #[test]
    fn run_monte_carlo_reports_every_chunk() {
        let iterations = 100u64;
        let mut progress_calls = Vec::new();
        let (estimate, elapsed) = run_monte_carlo(iterations, 4, |done, total, _partial| {
            progress_calls.push((done, total));
        });
        // 4 个 chunk，per_chunk = 25，正好 4 次回调
        assert_eq!(progress_calls.len(), 4);
        assert_eq!(progress_calls.last(), Some(&(100, 100)));
        assert!(elapsed >= 0);
        assert!(estimate > 0.0);
    }

    #[test]
    fn run_monte_carlo_single_chunk_when_chunks_exceed_iterations() {
        // chunks 被 clamp 到至少每块 1 次，10 次迭代 20 块 → per_chunk=1，10 次回调
        let mut calls = 0;
        run_monte_carlo(10, 20, |_, _, _| calls += 1);
        assert_eq!(calls, 10);
    }
}
