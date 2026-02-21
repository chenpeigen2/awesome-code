/// Flutter + Rust Bridge Demo
/// 展示异步操作、结构体、枚举等高级特性

use std::time::Duration;
use tokio::time::sleep;

// ==================== 枚举定义 ====================

/// 运算类型
#[derive(Debug, Clone)]
pub enum Operation {
    Add,
    Subtract,
    Multiply,
    Divide,
}

impl Operation {
    pub fn symbol(&self) -> String {
        match self {
            Operation::Add => "+".to_string(),
            Operation::Subtract => "-".to_string(),
            Operation::Multiply => "×".to_string(),
            Operation::Divide => "÷".to_string(),
        }
    }
}

/// 计算结果状态
#[derive(Debug, Clone)]
pub enum CalcResult {
    Success { value: f64 },
    Error { message: String },
}

// ==================== 结构体定义 ====================

/// 计算历史记录
#[derive(Debug, Clone)]
pub struct CalculationRecord {
    pub a: f64,
    pub b: f64,
    pub operation: Operation,
    pub result: CalcResult,
    pub timestamp: i64,
}

/// 计算器统计信息
#[derive(Debug, Clone)]
pub struct CalculatorStats {
    pub total_calculations: i32,
    pub success_count: i32,
    pub error_count: i32,
    pub average_value: f64,
}

// ==================== 核心计算函数 ====================

/// 异步计算函数 - 不阻塞 UI
pub async fn calculate_async(a: f64, b: f64, operation: Operation) -> CalcResult {
    // 模拟耗时操作
    sleep(Duration::from_millis(100)).await;
    
    let result = match operation {
        Operation::Add => a + b,
        Operation::Subtract => a - b,
        Operation::Multiply => a * b,
        Operation::Divide => {
            if b == 0.0 {
                return CalcResult::Error {
                    message: "除数不能为零".to_string(),
                };
            }
            a / b
        }
    };
    
    CalcResult::Success { value: result }
}

/// 同步计算函数（简单场景）
pub fn calculate_sync(a: f64, b: f64, operation: Operation) -> CalcResult {
    match operation {
        Operation::Add => CalcResult::Success { value: a + b },
        Operation::Subtract => CalcResult::Success { value: a - b },
        Operation::Multiply => CalcResult::Success { value: a * b },
        Operation::Divide => {
            if b == 0.0 {
                CalcResult::Error {
                    message: "除数不能为零".to_string(),
                }
            } else {
                CalcResult::Success { value: a / b }
            }
        }
    }
}

// ==================== 高级功能 ====================

/// 批量计算
pub async fn batch_calculate(requests: Vec<CalcRequest>) -> Vec<CalcResult> {
    let mut results = Vec::with_capacity(requests.len());
    
    for req in requests {
        let result = calculate_async(req.a, req.b, req.operation).await;
        results.push(result);
    }
    
    results
}

/// 计算请求结构
#[derive(Debug, Clone)]
pub struct CalcRequest {
    pub a: f64,
    pub b: f64,
    pub operation: Operation,
}

/// 格式化计算结果
pub fn format_result(a: f64, b: f64, operation: Operation, result: CalcResult) -> String {
    let op_symbol = operation.symbol();
    
    match result {
        CalcResult::Success { value } => {
            format!("{:.2} {} {:.2} = {:.4}", a, op_symbol, b, value)
        }
        CalcResult::Error { message } => {
            format!("错误: {}", message)
        }
    }
}

/// 生成计算历史记录
pub async fn create_record(a: f64, b: f64, operation: Operation) -> CalculationRecord {
    let result = calculate_async(a, b, operation.clone()).await;
    let timestamp = std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .unwrap()
        .as_secs() as i64;
    
    CalculationRecord {
        a,
        b,
        operation,
        result,
        timestamp,
    }
}

/// 计算统计信息
pub fn compute_stats(records: Vec<CalculationRecord>) -> CalculatorStats {
    let total = records.len() as i32;
    let success_count = records.iter().filter(|r| matches!(r.result, CalcResult::Success { .. })).count() as i32;
    let error_count = total - success_count;
    
    let successful_values: Vec<f64> = records
        .iter()
        .filter_map(|r| match &r.result {
            CalcResult::Success { value } => Some(*value),
            _ => None,
        })
        .collect();
    
    let average = if successful_values.is_empty() {
        0.0
    } else {
        successful_values.iter().sum::<f64>() / successful_values.len() as f64
    };
    
    CalculatorStats {
        total_calculations: total,
        success_count,
        error_count,
        average_value: average,
    }
}

// ==================== 工具函数 ====================

/// 字符串转运算类型
pub fn parse_operation(s: String) -> Option<Operation> {
    match s.as_str() {
        "+" | "add" | "Add" => Some(Operation::Add),
        "-" | "subtract" | "Subtract" => Some(Operation::Subtract),
        "*" | "×" | "multiply" | "Multiply" => Some(Operation::Multiply),
        "/" | "÷" | "divide" | "Divide" => Some(Operation::Divide),
        _ => None,
    }
}

/// 获取支持的运算列表
pub fn get_supported_operations() -> Vec<String> {
    vec![
        "+".to_string(),
        "-".to_string(),
        "×".to_string(),
        "÷".to_string(),
    ]
}

/// 模拟耗时计算（展示异步优势）
pub async fn heavy_computation(iterations: i32) -> f64 {
    let mut result = 0.0;
    
    for i in 0..iterations {
        sleep(Duration::from_micros(100)).await;
        result += (i as f64).sin() * (i as f64).cos();
    }
    
    result
}

#[flutter_rust_bridge::frb(init)]
pub fn init_app() {
    flutter_rust_bridge::setup_default_user_utils();
}