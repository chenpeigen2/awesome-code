//! 错误处理：自定义错误类型、错误链、`?` 传播、From 转换。
//!
//! 场景：解析 "姓名:分数" 格式的成绩单。

use std::fmt;

/// 领域错误类型：实现 `Display` + `Error`，提供错误链（source）。
#[derive(Debug)]
pub enum ParseError {
    /// 行格式不对（缺少冒号）
    MissingColon(String),
    /// 分数不是数字
    BadScore(String, std::num::ParseFloatError),
    /// 分数越界 [0, 100]
    ScoreOutOfRange(String, f64),
    /// 空行
    EmptyLine(usize),
}

impl fmt::Display for ParseError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            ParseError::MissingColon(line) => write!(f, "缺少冒号分隔符: {line:?}"),
            ParseError::BadScore(field, err) => write!(f, "分数不是数字 {field:?}: {err}"),
            ParseError::ScoreOutOfRange(name, score) => {
                write!(f, "分数越界 [0,100]: {name} = {score}")
            }
            ParseError::EmptyLine(idx) => write!(f, "第 {idx} 行为空"),
        }
    }
}

impl std::error::Error for ParseError {
    fn source(&self) -> Option<&(dyn std::error::Error + 'static)> {
        match self {
            // 保留底层解析错误形成错误链
            ParseError::BadScore(_, err) => Some(err),
            _ => None,
        }
    }
}

/// 学生成绩
#[derive(Debug, PartialEq)]
pub struct Student {
    pub name: String,
    pub score: f64,
}

/// 解析单行 "姓名:分数"。
///
/// `?` 与 `From` 转换配合：内部用 String 返回错误，
/// 泛型 caller 侧可以 `impl Into<ParseError>` 简化书写。
pub fn parse_line(line: &str, line_no: usize) -> Result<Student, ParseError> {
    let line = line.trim();
    if line.is_empty() {
        return Err(ParseError::EmptyLine(line_no));
    }
    let (name, score_str) = line
        .split_once(':')
        .ok_or_else(|| ParseError::MissingColon(line.to_string()))?;
    let name = name.trim().to_string();
    if name.is_empty() {
        return Err(ParseError::MissingColon(line.to_string()));
    }
    let score: f64 = score_str
        .trim()
        .parse()
        .map_err(|err| ParseError::BadScore(score_str.trim().to_string(), err))?;
    if !(0.0..=100.0).contains(&score) {
        return Err(ParseError::ScoreOutOfRange(name, score));
    }
    Ok(Student { name, score })
}

/// 解析整份成绩单：第一个错误即终止（fail-fast）。
pub fn parse_transcript(text: &str) -> Result<Vec<Student>, ParseError> {
    text.lines()
        .enumerate()
        .map(|(i, line)| parse_line(line, i + 1))
        .collect()
}

/// 宽容模式：跳过坏行，返回（好记录, 错误摘要）。
pub fn parse_transcript_lenient(text: &str) -> (Vec<Student>, Vec<String>) {
    let mut ok = Vec::new();
    let mut bad = Vec::new();
    for (i, line) in text.lines().enumerate() {
        match parse_line(line, i + 1) {
            Ok(s) => ok.push(s),
            Err(e) => bad.push(e.to_string()),
        }
    }
    (ok, bad)
}

// ==================== 测试 ====================
// 预期值来自独立真值：手工构造的输入/输出对。

#[cfg(test)]
mod tests {
    use super::*;
    use std::error::Error;

    #[test]
    fn parse_line_happy_path() {
        let s = parse_line("Alice: 95.5", 1).unwrap();
        assert_eq!(
            s,
            Student {
                name: "Alice".into(),
                score: 95.5
            }
        );
    }

    #[test]
    fn parse_line_missing_colon() {
        assert!(matches!(
            parse_line("Alice 95", 1),
            Err(ParseError::MissingColon(_))
        ));
    }

    #[test]
    fn parse_line_bad_score_chains_source() {
        let err = parse_line("Bob:abc", 1).unwrap_err();
        // 错误链：BadScore 底层携带 ParseFloatError
        assert!(err.source().is_some());
        assert!(matches!(err, ParseError::BadScore(_, _)));
    }

    #[test]
    fn parse_line_score_bounds() {
        assert!(matches!(
            parse_line("C:101", 1),
            Err(ParseError::ScoreOutOfRange(_, 101.0))
        ));
        assert!(matches!(
            parse_line("C:-1", 1),
            Err(ParseError::ScoreOutOfRange(_, -1.0))
        ));
        // 边界可取等号
        assert!(parse_line("C:100", 1).is_ok());
        assert!(parse_line("C:0", 1).is_ok());
    }

    #[test]
    fn transcript_fail_fast() {
        let text = "A:90\n\nB:80";
        let err = parse_transcript(text).unwrap_err();
        assert!(matches!(err, ParseError::EmptyLine(2)));
    }

    #[test]
    fn transcript_collects_all() {
        let text = "A:90\nB:80\nC:70";
        let students = parse_transcript(text).unwrap();
        assert_eq!(students.len(), 3);
        assert_eq!(students[0].name, "A");
        assert_eq!(students[2].score, 70.0);
    }

    /// 宽容模式：好行照收，坏行汇总
    #[test]
    fn lenient_mode_partitions() {
        let text = "A:90\nbroken line\nB:80\nC:999";
        let (ok, bad) = parse_transcript_lenient(text);
        assert_eq!(ok.len(), 2);
        assert_eq!(bad.len(), 2);
        assert!(bad[0].contains("缺少冒号"));
        assert!(bad[1].contains("越界"));
    }

    /// Display 输出人类可读
    #[test]
    fn error_display_is_readable() {
        let err = parse_line("Alice 95", 3).unwrap_err();
        assert_eq!(err.to_string(), "缺少冒号分隔符: \"Alice 95\"");
    }
}
