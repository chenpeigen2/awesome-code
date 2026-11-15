//! 矩阵运算：乘法、行列式、转置、单位阵。
//!
//! 展示：自定义结构体跨 FFI、结构化错误枚举（Dart 侧 sealed class 模式匹配）、
//! 递归算法（余子式展开）。

/// 错误类别（Dart 侧会生成为枚举）
#[derive(Debug, Clone, PartialEq)]
pub enum MatrixErrorKind {
    /// 形状不匹配
    DimensionMismatch,
    /// 不是方阵（行列式要求）
    NotSquare,
    /// 数据长度与 rows*cols 不符
    DataLengthMismatch,
    /// 形状非法（0 行或 0 列）
    InvalidShape,
}

/// 运算结果：成功带矩阵，失败带错误类别与说明。
/// 用普通枚举而非 Result，让 Dart 侧拿到可模式匹配的 sealed class。
#[derive(Debug, Clone)]
pub enum MatrixOutcome {
    Success { matrix: Matrix },
    Failure { kind: MatrixErrorKind, message: String },
}

impl MatrixOutcome {
    fn ok(matrix: Matrix) -> Self {
        MatrixOutcome::Success { matrix }
    }
    fn err(kind: MatrixErrorKind, message: impl Into<String>) -> Self {
        MatrixOutcome::Failure { kind, message: message.into() }
    }
}

/// 行主序矩阵
#[derive(Debug, Clone)]
pub struct Matrix {
    pub rows: i32,
    pub cols: i32,
    /// 行主序存储，长度必须 = rows * cols
    pub data: Vec<f64>,
}

impl Matrix {
    fn validate(&self) -> Result<(), (MatrixErrorKind, String)> {
        if self.rows <= 0 || self.cols <= 0 {
            return Err((MatrixErrorKind::InvalidShape, "行列数必须为正".into()));
        }
        if self.data.len() != (self.rows as usize) * (self.cols as usize) {
            return Err((
                MatrixErrorKind::DataLengthMismatch,
                format!("data 长度 {} ≠ {}×{}", self.data.len(), self.rows, self.cols),
            ));
        }
        Ok(())
    }

    fn get(&self, r: usize, c: usize) -> f64 {
        self.data[r * self.cols as usize + c]
    }
}

/// n 阶单位阵
pub fn identity(n: i32) -> MatrixOutcome {
    if n <= 0 {
        return MatrixOutcome::err(MatrixErrorKind::InvalidShape, "阶数必须为正");
    }
    let n = n as usize;
    let mut data = vec![0.0; n * n];
    for i in 0..n {
        data[i * n + i] = 1.0;
    }
    MatrixOutcome::ok(Matrix { rows: n as i32, cols: n as i32, data })
}

/// 矩阵乘法：A(m×k) · B(k×n) → m×n
pub fn multiply(a: Matrix, b: Matrix) -> MatrixOutcome {
    if let Err((kind, msg)) = a.validate().and_then(|_| b.validate()) {
        return MatrixOutcome::err(kind, msg);
    }
    let (m, k) = (a.rows as usize, a.cols as usize);
    let (k2, n) = (b.rows as usize, b.cols as usize);
    if k != k2 {
        return MatrixOutcome::err(
            MatrixErrorKind::DimensionMismatch,
            format!("A 的列数 {k} ≠ B 的行数 {k2}"),
        );
    }
    let mut data = vec![0.0; m * n];
    // i-k-j 循环序对行主序缓存更友好
    for i in 0..m {
        for kk in 0..k {
            let aik = a.get(i, kk);
            if aik == 0.0 {
                continue;
            }
            for j in 0..n {
                data[i * n + j] += aik * b.get(kk, j);
            }
        }
    }
    MatrixOutcome::ok(Matrix { rows: a.rows, cols: b.cols, data })
}

/// 转置
pub fn transpose(a: Matrix) -> MatrixOutcome {
    if let Err((kind, msg)) = a.validate() {
        return MatrixOutcome::err(kind, msg);
    }
    let (m, n) = (a.rows as usize, a.cols as usize);
    let mut data = vec![0.0; m * n];
    for i in 0..m {
        for j in 0..n {
            data[j * m + i] = a.get(i, j);
        }
    }
    MatrixOutcome::ok(Matrix { rows: a.cols, cols: a.rows, data })
}

/// 行列式：1×1 直接取值；否则按第一行余子式展开递归。
pub fn determinant(a: Matrix) -> MatrixOutcome {
    if let Err((kind, msg)) = a.validate() {
        return MatrixOutcome::err(kind, msg);
    }
    if a.rows != a.cols {
        return MatrixOutcome::err(
            MatrixErrorKind::NotSquare,
            format!("行列式要求方阵，得到 {0}×{1}", a.rows, a.cols),
        );
    }
    MatrixOutcome::Success { matrix: Matrix {
        rows: 1,
        cols: 1,
        data: vec![det_recursive(&a)],
    } }
}

fn det_recursive(m: &Matrix) -> f64 {
    let n = m.rows as usize;
    if n == 1 {
        return m.data[0];
    }
    if n == 2 {
        return m.get(0, 0) * m.get(1, 1) - m.get(0, 1) * m.get(1, 0);
    }
    let mut sum = 0.0;
    for col in 0..n {
        // 构造去掉第 0 行第 col 列的余子式
        let mut minor_data = Vec::with_capacity((n - 1) * (n - 1));
        for r in 1..n {
            for c in 0..n {
                if c != col {
                    minor_data.push(m.get(r, c));
                }
            }
        }
        let minor = Matrix {
            rows: (n - 1) as i32,
            cols: (n - 1) as i32,
            data: minor_data,
        };
        let sign = if col % 2 == 0 { 1.0 } else { -1.0 };
        sum += sign * m.get(0, col) * det_recursive(&minor);
    }
    sum
}

/// 解析便捷入口：把 Dart 传来的扁平输入构造矩阵（演示工厂函数）。
pub fn from_rows(rows: i32, cols: i32, data: Vec<f64>) -> MatrixOutcome {
    let m = Matrix { rows, cols, data };
    match m.validate() {
        Ok(()) => MatrixOutcome::ok(m),
        Err((kind, msg)) => MatrixOutcome::err(kind, msg),
    }
}

/// 测试辅助：断言成功并取出矩阵（仅测试使用）
#[cfg(test)]
trait ExpectMatrix {
    fn expect_matrix(self) -> Matrix;
}

#[cfg(test)]
impl ExpectMatrix for MatrixOutcome {
    fn expect_matrix(self) -> Matrix {
        match self {
            MatrixOutcome::Success { matrix } => matrix,
            MatrixOutcome::Failure { kind, message } => panic!("expected success, got {kind:?}: {message}"),
        }
    }
}

// ==================== 测试 ====================
// 预期值来自独立真值：手工计算的矩阵结果、教科书行列式例题。

#[cfg(test)]
mod tests {
    use super::*;

    fn mat(rows: i32, cols: i32, data: Vec<f64>) -> Matrix {
        Matrix { rows, cols, data }
    }

    #[test]
    fn identity_is_correct() {
        match identity(3) {
            MatrixOutcome::Success { matrix } => {
                assert_eq!(matrix.data, vec![1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0]);
            }
            _ => panic!("expected success"),
        }
        assert!(matches!(
            identity(0),
            MatrixOutcome::Failure { kind: MatrixErrorKind::InvalidShape, .. }
        ));
    }

    /// [[1,2],[3,4]] · [[5,6],[7,8]] = [[19,22],[43,50]]
    #[test]
    fn multiply_2x2_known_value() {
        let result = multiply(mat(2, 2, vec![1.0, 2.0, 3.0, 4.0]), mat(2, 2, vec![5.0, 6.0, 7.0, 8.0]));
        match result {
            MatrixOutcome::Success { matrix } => {
                assert_eq!(matrix.data, vec![19.0, 22.0, 43.0, 50.0]);
            }
            _ => panic!("expected success"),
        }
    }

    /// (2×3)·(3×2) → 2×2
    #[test]
    fn multiply_rectangular_shapes() {
        let a = mat(2, 3, vec![1.0, 2.0, 3.0, 4.0, 5.0, 6.0]);
        let b = mat(3, 2, vec![7.0, 8.0, 9.0, 10.0, 11.0, 12.0]);
        match multiply(a, b) {
            MatrixOutcome::Success { matrix } => {
                assert_eq!((matrix.rows, matrix.cols), (2, 2));
                assert_eq!(matrix.data, vec![58.0, 64.0, 139.0, 154.0]);
            }
            _ => panic!("expected success"),
        }
    }

    #[test]
    fn multiply_rejects_dimension_mismatch() {
        let result = multiply(mat(2, 3, vec![0.0; 6]), mat(2, 3, vec![0.0; 6]));
        assert!(matches!(
            result,
            MatrixOutcome::Failure { kind: MatrixErrorKind::DimensionMismatch, .. }
        ));
    }

    /// A·I = A 与 I·A = A（性质校验）
    #[test]
    fn multiply_by_identity_is_identity() {
        let a = mat(3, 3, vec![1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0]);
        let expected = a.data.clone();
        let i3 = identity(3).expect_matrix();
        for outcome in [multiply(a.clone(), i3.clone()), multiply(i3, a)] {
            match outcome {
                MatrixOutcome::Success { matrix } => assert_eq!(matrix.data, expected),
                other => panic!("expected success, got {other:?}"),
            }
        }
    }

    #[test]
    fn transpose_swaps_shape() {
        match transpose(mat(2, 3, vec![1.0, 2.0, 3.0, 4.0, 5.0, 6.0])) {
            MatrixOutcome::Success { matrix } => {
                assert_eq!((matrix.rows, matrix.cols), (3, 2));
                assert_eq!(matrix.data, vec![1.0, 4.0, 2.0, 5.0, 3.0, 6.0]);
            }
            _ => panic!("expected success"),
        }
    }

    /// 教科书例：det([[1,2,3],[4,5,6],[7,8,10]]) = -3
    #[test]
    fn determinant_3x3_known_value() {
        let result = determinant(mat(3, 3, vec![1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 10.0]));
        match result {
            MatrixOutcome::Success { matrix } => {
                assert_eq!((matrix.rows, matrix.cols), (1, 1));
                assert!((matrix.data[0] - (-3.0)).abs() < 1e-9);
            }
            _ => panic!("expected success"),
        }
    }

    /// det([[6,1,1],[4,-2,5],[2,8,7]]) = -306
    #[test]
    fn determinant_3x3_second_known_value() {
        let result = determinant(mat(3, 3, vec![6.0, 1.0, 1.0, 4.0, -2.0, 5.0, 2.0, 8.0, 7.0]));
        match result {
            MatrixOutcome::Success { matrix } => {
                assert!((matrix.data[0] - (-306.0)).abs() < 1e-9);
            }
            _ => panic!("expected success"),
        }
    }

    #[test]
    fn determinant_rejects_non_square() {
        let result = determinant(mat(2, 3, vec![0.0; 6]));
        assert!(matches!(
            result,
            MatrixOutcome::Failure { kind: MatrixErrorKind::NotSquare, .. }
        ));
    }

    #[test]
    fn data_length_mismatch_is_reported() {
        let result = from_rows(2, 2, vec![1.0, 2.0, 3.0]);
        assert!(matches!(
            result,
            MatrixOutcome::Failure { kind: MatrixErrorKind::DataLengthMismatch, .. }
        ));
    }
}
