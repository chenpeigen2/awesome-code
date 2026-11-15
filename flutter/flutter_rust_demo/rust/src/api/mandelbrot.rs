//! Mandelbrot 分形渲染：纯 CPU 密集计算，通过 FFI 回传 RGBA 像素缓冲。
//!
//! 展示：重计算走异步（不阻塞 UI）、大块二进制数据跨桥传输、数学核心与配色解耦。

/// 渲染参数（坐标为复平面中心，scale 为视口宽度）
#[derive(Debug, Clone)]
pub struct MandelbrotConfig {
    pub width: i32,
    pub height: i32,
    pub center_x: f64,
    pub center_y: f64,
    /// 视口在复平面上覆盖的宽度（如 3.0 覆盖经典 [-2.5, 0.5] 的大部分）
    pub scale: f64,
    /// 最大迭代次数，越大越精细
    pub max_iter: i32,
}

/// 渲染结果
#[derive(Debug, Clone)]
pub struct MandelbrotResult {
    pub width: i32,
    pub height: i32,
    /// RGBA 像素缓冲，长度 = width * height * 4
    pub pixels: Vec<u8>,
    /// 平均逃逸迭代数（衡量画面"繁忙程度"的统计量）
    pub avg_escape: f64,
    /// 渲染耗时（毫秒）
    pub elapsed_ms: i64,
    /// 内部点（永不逃逸，属于集合）占全部像素的比例
    pub interior_ratio: f64,
}

/// 单点逃逸迭代数：z₀ = 0，zₙ₊₁ = zₙ² + c，返回首次 |z| > 2 的迭代数；
/// 达到 max_iter 仍未逃逸则返回 max_iter（视作集合内部点）。
fn escape_count(c: (f64, f64), max_iter: u32) -> u32 {
    let (cx, cy) = c;
    let (mut zx, mut zy) = (0.0f64, 0.0f64);
    let mut iter = 0u32;
    // |z|² = zx² + zy² > 4 即逃逸，避免开方
    while zx * zx + zy * zy <= 4.0 && iter < max_iter {
        let next_zx = zx * zx - zy * zy + cx;
        zy = 2.0 * zx * zy + cy;
        zx = next_zx;
        iter += 1;
    }
    iter
}

/// 迭代数 → RGBA 颜色。
///
/// 集合内部（iter == max_iter）为不透明黑；
/// 外部用平滑双段渐变（深蓝 → 青 → 白）凸显结构。
fn iteration_to_color(iter: u32, max_iter: u32) -> [u8; 4] {
    if iter >= max_iter {
        return [0, 0, 0, 255];
    }
    // 归一化到 (0,1]，再取平方根让低迭代区间的色彩层次更丰富
    let t = (iter as f64 / max_iter as f64).sqrt();

    // 双段渐变：t < 0.5 从 (8, 12, 60) 到 (32, 178, 220)；t >= 0.5 从青色到白
    let (r, g, b) = if t < 0.5 {
        let u = t / 0.5;
        lerp3((8.0, 12.0, 60.0), (32.0, 178.0, 220.0), u)
    } else {
        let u = (t - 0.5) / 0.5;
        lerp3((32.0, 178.0, 220.0), (245.0, 250.0, 255.0), u)
    };
    [r as u8, g as u8, b as u8, 255]
}

fn lerp3(a: (f64, f64, f64), b: (f64, f64, f64), t: f64) -> (f64, f64, f64) {
    (
        a.0 + (b.0 - a.0) * t,
        a.1 + (b.1 - a.1) * t,
        a.2 + (b.2 - a.2) * t,
    )
}

/// 纯计算核心：按 config 渲染 RGBA 缓冲并统计。与 FFI/异步无关，便于单测。
pub fn render(config: &MandelbrotConfig) -> MandelbrotResult {
    let start = std::time::Instant::now();
    let width = config.width.clamp(1, 4096) as usize;
    let height = config.height.clamp(1, 4096) as usize;
    let max_iter = config.max_iter.clamp(1, 10_000) as u32;
    let scale = if config.scale.is_finite() && config.scale > 0.0 {
        config.scale
    } else {
        3.0
    };

    let aspect = height as f64 / width as f64;
    let x_min = config.center_x - scale / 2.0;
    let y_min = config.center_y - scale * aspect / 2.0;
    let dx = scale / width as f64;
    let dy = scale * aspect / height as f64;

    let mut pixels = vec![0u8; width * height * 4];
    let mut escape_sum = 0.0f64;
    let mut interior = 0u64;

    for row in 0..height {
        let cy = y_min + (row as f64 + 0.5) * dy;
        for col in 0..width {
            let cx = x_min + (col as f64 + 0.5) * dx;
            let iter = escape_count((cx, cy), max_iter);
            if iter == max_iter {
                interior += 1;
            }
            escape_sum += iter as f64;
            let [r, g, b, a] = iteration_to_color(iter, max_iter);
            let idx = (row * width + col) * 4;
            pixels[idx] = r;
            pixels[idx + 1] = g;
            pixels[idx + 2] = b;
            pixels[idx + 3] = a;
        }
    }

    let total = (width * height) as f64;
    MandelbrotResult {
        width: width as i32,
        height: height as i32,
        pixels,
        avg_escape: escape_sum / total,
        elapsed_ms: start.elapsed().as_millis() as i64,
        interior_ratio: interior as f64 / total,
    }
}

/// 异步渲染入口：FRB 会把 async fn 调度到 Rust 侧线程池，UI 保持流畅。
pub async fn render_mandelbrot(config: MandelbrotConfig) -> MandelbrotResult {
    render(&config)
}

// ==================== 测试 ====================
// 预期值来自独立真值：Mandelbrot 集的数学性质（c=0 属于集合；c=2 立即逃逸；
// 经典整图集合内部点约占 21% 量级——用宽松区间断言）。

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn origin_never_escapes() {
        // c = 0 是不动点 z=0，属于集合
        assert_eq!(escape_count((0.0, 0.0), 100), 100);
    }

    #[test]
    fn point_far_outside_escapes_quickly() {
        // c = (3, 0)：z₁ = 3，|z|² = 9 > 4，一步逃逸
        assert_eq!(escape_count((3.0, 0.0), 100), 1);
        // c = (10, 10)：z₁ = (10, 10)，|z|² = 200 > 4，同样一步逃逸
        assert_eq!(escape_count((10.0, 10.0), 100), 1);
        // c = (2, 0)：z₁ = 2 时 |z|² = 4 不严格大于 4，再迭代一步到 z₂ = 6 才逃逸
        assert_eq!(escape_count((2.0, 0.0), 100), 2);
    }

    #[test]
    fn known_interior_point_stays() {
        // c = (-1, 0)：周期 2 轨道 0 → -1 → 0，属于集合
        assert_eq!(escape_count((-1.0, 0.0), 200), 200);
    }

    #[test]
    fn known_exterior_point_escapes_finite() {
        // c = (1, 1)：z₁ = (1,1)，|z|² = 2 ≤ 4；z₂ = (1,3)，|z|² = 10 > 4 → 两步逃逸
        assert_eq!(escape_count((1.0, 1.0), 1000), 2);
    }

    #[test]
    fn interior_color_is_opaque_black() {
        assert_eq!(iteration_to_color(256, 256), [0, 0, 0, 255]);
    }

    #[test]
    fn exterior_color_is_opaque_and_bounded() {
        for iter in [1u32, 5, 50, 255] {
            let [r, g, b, a] = iteration_to_color(iter, 256);
            assert_eq!(a, 255);
            // u8 本身就保证 <= 255，只需检查颜色有值（非纯黑，否则分形不可见）；
            // 求和用 u16 防止溢出
            assert!(r as u16 + g as u16 + b as u16 > 0);
        }
    }

    #[test]
    fn render_output_shape_matches_config() {
        let cfg = MandelbrotConfig {
            width: 64,
            height: 48,
            center_x: -0.75,
            center_y: 0.0,
            scale: 3.0,
            max_iter: 64,
        };
        let result = render(&cfg);
        assert_eq!(result.width, 64);
        assert_eq!(result.height, 48);
        assert_eq!(result.pixels.len(), 64 * 48 * 4);
    }

    /// 经典视图（中心 -0.75,0，宽 3）：集合内部点比例应有界且为正。
    /// 文献常用值约 0.21（心形 + 主圆），这里用宽松区间防脆弱。
    #[test]
    fn classic_view_interior_ratio_in_expected_band() {
        let cfg = MandelbrotConfig {
            width: 200,
            height: 150,
            center_x: -0.75,
            center_y: 0.0,
            scale: 3.0,
            max_iter: 128,
        };
        let result = render(&cfg);
        assert!(
            (0.10..=0.40).contains(&result.interior_ratio),
            "interior_ratio = {}",
            result.interior_ratio
        );
        assert!(result.avg_escape > 0.0);
        assert!(result.elapsed_ms >= 0);
    }
}
