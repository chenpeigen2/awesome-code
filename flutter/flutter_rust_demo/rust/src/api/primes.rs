//! 素数实验室：埃拉托斯特尼筛法、因式分解、第 n 个素数。
//!
//! 展示：切片/向量操作、迭代器链、u64 溢出防护。

/// 素数因子（素数与指数对）
#[derive(Debug, Clone, PartialEq)]
pub struct PrimeFactor {
    pub prime: u64,
    pub exponent: u32,
}

/// 筛法统计结果
#[derive(Debug, Clone)]
pub struct SieveStats {
    /// 筛选上限
    pub limit: u64,
    /// 素数个数 π(limit)
    pub count: u64,
    /// 最大的那个素数（limit < 2 时为 0）
    pub largest: u64,
    /// 素数密度（count / limit，limit = 0 时为 0）
    pub density: f64,
    /// 耗时（毫秒）
    pub elapsed_ms: i64,
}

/// 埃拉托斯特尼筛法：返回 <= limit 的全部素数。
///
/// 核心逻辑与 UI 无关，纯函数，便于单测。
pub fn sieve(limit: u64) -> Vec<u64> {
    if limit < 2 {
        return Vec::new();
    }
    let n = limit as usize;
    let mut is_prime = vec![true; n + 1];
    is_prime[0] = false;
    is_prime[1] = false;

    let mut p = 2usize;
    while p * p <= n {
        if is_prime[p] {
            // 从 p*p 开始标记合数，步长 p
            let mut m = p * p;
            while m <= n {
                is_prime[m] = false;
                m += p;
            }
        }
        p += 1;
    }

    is_prime
        .iter()
        .enumerate()
        .filter(|(_, &prime)| prime)
        .map(|(i, _)| i as u64)
        .collect()
}

/// 筛法 + 统计（异步包装：真机上 limit 很大时不阻塞 UI 线程）
pub async fn sieve_stats(limit: u64) -> SieveStats {
    let start = std::time::Instant::now();
    let primes = sieve(limit);
    let count = primes.len() as u64;
    let largest = primes.last().copied().unwrap_or(0);
    let density = if limit == 0 { 0.0 } else { count as f64 / limit as f64 };
    SieveStats {
        limit,
        count,
        largest,
        density,
        elapsed_ms: start.elapsed().as_millis() as i64,
    }
}

/// 算术基本定理：质因数分解。
///
/// 例如 360 = 2³ × 3² × 5¹ → [(2,3),(3,2),(5,1)]。
/// n < 2 返回空表。
pub fn factorize(mut n: u64) -> Vec<PrimeFactor> {
    if n < 2 {
        return Vec::new();
    }
    let mut factors: Vec<PrimeFactor> = Vec::new();

    // 处理 2
    let mut exp = 0u32;
    while n.is_multiple_of(2) {
        n /= 2;
        exp += 1;
    }
    if exp > 0 {
        factors.push(PrimeFactor { prime: 2, exponent: exp });
    }

    // 奇数试除到 √n
    let mut p = 3u64;
    while p * p <= n {
        let mut exp = 0u32;
        while n.is_multiple_of(p) {
            n /= p;
            exp += 1;
        }
        if exp > 0 {
            factors.push(PrimeFactor { prime: p, exponent: exp });
        }
        p += 2;
    }
    // 剩下的若 > 1 必为素数
    if n > 1 {
        factors.push(PrimeFactor { prime: n, exponent: 1 });
    }
    factors
}

/// 返回第 n 个素数（n 从 1 开始：第 1 个素数是 2）。
///
/// n == 0 属于非法输入，返回 Err。
pub fn nth_prime(n: u64) -> Result<u64, String> {
    if n == 0 {
        return Err("n 必须从 1 开始".into());
    }
    if n > 100_000 {
        return Err(format!("n={n} 过大，最多支持 100000"));
    }
    // 素数定理上界估计：p_n < n(ln n + ln ln n)，n >= 6
    let bound = if n < 6 {
        15
    } else {
        let nf = n as f64;
        (nf * (nf.ln() + nf.ln().ln()) + 10.0) as u64
    };
    sieve(bound)
        .get((n - 1) as usize)
        .copied()
        .ok_or_else(|| format!("上界 {bound} 估计不足，未能找到第 {n} 个素数"))
}

// ==================== 测试 ====================
// 预期值来自独立真值：素数定义、OEIS A000040（素数序列）、OEIS A000720（素数计数）。

#[cfg(test)]
mod tests {
    use super::*;

    /// OEIS A000040: 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, ...
    #[test]
    fn sieve_matches_known_prime_list() {
        assert_eq!(sieve(30), vec![2, 3, 5, 7, 11, 13, 17, 19, 23, 29]);
    }

    #[test]
    fn sieve_edge_cases() {
        assert!(sieve(0).is_empty());
        assert!(sieve(1).is_empty());
        assert_eq!(sieve(2), vec![2]);
        assert_eq!(sieve(3), vec![2, 3]);
    }

    /// OEIS A000720: π(10)=4, π(100)=25, π(1000)=168, π(10000)=1229
    #[test]
    fn sieve_count_matches_pi_table() {
        assert_eq!(sieve(10).len(), 4);
        assert_eq!(sieve(100).len(), 25);
        assert_eq!(sieve(1000).len(), 168);
        assert_eq!(sieve(10_000).len(), 1229);
    }

    /// 360 = 2³·3²·5；97 本身是素数；1 无分解
    #[test]
    fn factorize_known_values() {
        assert_eq!(
            factorize(360),
            vec![
                PrimeFactor { prime: 2, exponent: 3 },
                PrimeFactor { prime: 3, exponent: 2 },
                PrimeFactor { prime: 5, exponent: 1 },
            ]
        );
        assert_eq!(factorize(97), vec![PrimeFactor { prime: 97, exponent: 1 }]);
        assert!(factorize(1).is_empty());
        assert!(factorize(0).is_empty());
    }

    /// 分解结果连乘必须还原原数（性质校验）
    #[test]
    fn factorize_roundtrip() {
        for n in [2u64, 12, 9973, 123456, 8675309] {
            let product: u64 = factorize(n).iter().map(|f| f.prime.pow(f.exponent)).product();
            assert_eq!(product, n, "roundtrip failed for {n}");
        }
    }

    /// OEIS A000040 第 n 项：p(1)=2, p(6)=13, p(100)=541, p(1000)=7919
    #[test]
    fn nth_prime_matches_oeis() {
        assert_eq!(nth_prime(1).unwrap(), 2);
        assert_eq!(nth_prime(6).unwrap(), 13);
        assert_eq!(nth_prime(100).unwrap(), 541);
        assert_eq!(nth_prime(1000).unwrap(), 7919);
    }

    #[test]
    fn nth_prime_rejects_invalid() {
        assert!(nth_prime(0).is_err());
        assert!(nth_prime(200_000).is_err());
    }
}
