//! 文本统计分析：字符/词/行统计、词频 Top-K、回文判断。
//!
//! 展示：String/&str 处理、HashMap、Unicode 感知的字符遍历。

/// 文本统计结果
#[derive(Debug, Clone)]
pub struct TextStats {
    /// Unicode 字符数（不是字节数）
    pub char_count: i64,
    /// UTF-8 字节数
    pub byte_count: i64,
    /// 空白分隔的单词数
    pub word_count: i64,
    /// 行数（按 \n 计）
    pub line_count: i64,
    /// 去重后的不同单词数（小写化后比较）
    pub unique_words: i64,
    /// 平均单词长度（字符），无单词时为 0
    pub avg_word_len: f64,
    /// 最长单词，无单词时为空串
    pub longest_word: String,
    /// 出现最多的前 K 个单词（小写化，按次数降序、次数相同按字典序）
    pub top_words: Vec<WordCount>,
    /// 出现最多的前 K 个字符（按次数降序）
    pub top_chars: Vec<CharCount>,
}

/// 词频条目
#[derive(Debug, Clone, PartialEq)]
pub struct WordCount {
    pub word: String,
    pub count: i64,
}

/// 字符频次条目
#[derive(Debug, Clone, PartialEq)]
pub struct CharCount {
    pub ch: String,
    pub count: i64,
}

/// 单词切分规则：仅保留字母与数字，连续字母数字序列为一个单词，统一小写。
fn tokenize(text: &str) -> Vec<String> {
    let mut words = Vec::new();
    let mut current = String::new();
    for ch in text.chars() {
        if ch.is_alphanumeric() {
            current.extend(ch.to_lowercase());
        } else if !current.is_empty() {
            words.push(std::mem::take(&mut current));
        }
    }
    if !current.is_empty() {
        words.push(current);
    }
    words
}

/// 判断忽略大小写、忽略非字母数字字符后是否为回文。
/// "A man, a plan, a canal: Panama" → true；空串与单字符视为回文。
pub fn is_palindrome(text: String) -> bool {
    let normalized: Vec<char> = text
        .chars()
        .filter(|c| c.is_alphanumeric())
        .flat_map(|c| c.to_lowercase())
        .collect();
    let n = normalized.len();
    (0..n / 2).all(|i| normalized[i] == normalized[n - 1 - i])
}

/// 完整文本分析。top_k 控制词频/字频榜单长度（<= 0 时返回空榜单）。
pub fn analyze_text(text: String, top_k: i32) -> TextStats {
    let chars: Vec<char> = text.chars().collect();
    let words = tokenize(&text);

    let mut word_freq: std::collections::HashMap<String, i64> = std::collections::HashMap::new();
    for w in &words {
        *word_freq.entry(w.clone()).or_insert(0) += 1;
    }
    let mut top_words: Vec<WordCount> = word_freq
        .into_iter()
        .map(|(word, count)| WordCount { word, count })
        .collect();
    // 次数降序，次数相同按字典序升序——排序规则确定，Dart 侧展示才稳定
    top_words.sort_by(|a, b| b.count.cmp(&a.count).then_with(|| a.word.cmp(&b.word)));
    top_words.truncate(top_k.max(0) as usize);

    let mut char_freq: std::collections::HashMap<char, i64> = std::collections::HashMap::new();
    for c in &chars {
        if !c.is_whitespace() {
            *char_freq.entry(*c).or_insert(0) += 1;
        }
    }
    let mut top_chars: Vec<CharCount> = char_freq
        .into_iter()
        .map(|(ch, count)| CharCount { ch: ch.to_string(), count })
        .collect();
    top_chars.sort_by(|a, b| b.count.cmp(&a.count).then_with(|| a.ch.cmp(&b.ch)));
    top_chars.truncate(top_k.max(0) as usize);

    let word_count = words.len() as i64;
    let total_word_chars: usize = words.iter().map(|w| w.chars().count()).sum();
    let avg_word_len = if words.is_empty() {
        0.0
    } else {
        total_word_chars as f64 / word_count as f64
    };
    let longest_word = words
        .iter()
        .max_by_key(|w| w.chars().count())
        .cloned()
        .unwrap_or_default();

    TextStats {
        char_count: chars.len() as i64,
        byte_count: text.len() as i64,
        word_count,
        line_count: text.lines().count() as i64,
        unique_words: {
            let mut seen = words.clone();
            seen.sort();
            seen.dedup();
            seen.len() as i64
        },
        avg_word_len,
        longest_word,
        top_words,
        top_chars,
    }
}

// ==================== 测试 ====================
// 预期值来自独立真值：手工计数、经典回文例句。

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn tokenize_splits_on_non_alphanumeric() {
        assert_eq!(tokenize("Hello, World! 你好"), vec!["hello", "world", "你好"]);
        assert_eq!(tokenize("it's a test-2 case"), vec!["it", "s", "a", "test", "2", "case"]);
        assert!(tokenize("  \n\t ").is_empty());
    }

    /// "hello world hello rust hello"：hello×3, world×1, rust×1
    #[test]
    fn analyze_counts_words_and_uniques() {
        let stats = analyze_text("hello world hello rust hello".into(), 10);
        assert_eq!(stats.char_count, 28);
        assert_eq!(stats.word_count, 5);
        assert_eq!(stats.unique_words, 3);
        assert_eq!(stats.line_count, 1);
        assert_eq!(stats.top_words[0], WordCount { word: "hello".into(), count: 3 });
        // 平均词长 = (5+5+5+4+5)/5 = 4.8
        assert!((stats.avg_word_len - 4.8).abs() < 1e-9);
        assert_eq!(stats.longest_word, "hello");
    }

    #[test]
    fn top_k_limits_and_sorts() {
        let text = "b a c a b a c b".to_string();
        // a×3, b×3, c×2 → 同次数按字典序：a 在 b 前
        let stats = analyze_text(text, 2);
        assert_eq!(stats.top_words.len(), 2);
        assert_eq!(stats.top_words[0].word, "a");
        assert_eq!(stats.top_words[0].count, 3);
        assert_eq!(stats.top_words[1].word, "b");
    }

    #[test]
    fn top_k_zero_returns_empty_boards() {
        let stats = analyze_text("some text".into(), 0);
        assert!(stats.top_words.is_empty());
        assert!(stats.top_chars.is_empty());
    }

    /// 中文按字符统计："你好你好吗" → 你×2, 好×2, 吗×1；char_count=5, byte_count=15
    #[test]
    fn unicode_aware_char_and_byte_counts() {
        let stats = analyze_text("你好你好吗".into(), 3);
        assert_eq!(stats.char_count, 5);
        assert_eq!(stats.byte_count, 15); // 每个 CJK 字符 3 字节
        assert_eq!(stats.top_chars[0], CharCount { ch: "你".into(), count: 2 });
        assert_eq!(stats.word_count, 1); // 连续字母数字 = 1 个"单词"
    }

    #[test]
    fn multiline_line_count() {
        let stats = analyze_text("line1\nline2\nline3".into(), 5);
        assert_eq!(stats.line_count, 3);
    }

    /// 经典回文例句集合
    #[test]
    fn palindrome_classic_cases() {
        assert!(is_palindrome("A man, a plan, a canal: Panama".into()));
        assert!(is_palindrome("Was it a car or a cat I saw?".into()));
        assert!(is_palindrome("上海自来水来自海上".into()));
        assert!(is_palindrome("".into()));
        assert!(is_palindrome("a".into()));
        assert!(!is_palindrome("hello".into()));
        assert!(!is_palindrome("rust 与 flutter".into()));
    }
}
