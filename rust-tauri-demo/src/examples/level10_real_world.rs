//! Level 10: 实际应用示例
//!
//! 学习目标:
//! - 构建一个简单的 HTTP 客户端
//! - 实现缓存系统
//! - 创建状态机

use serde::{Deserialize, Serialize};
use std::collections::HashMap;
use std::sync::{Arc, RwLock};
use std::time::{Duration, Instant};

// ==================== API 客户端示例 ====================

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ApiResponse<T> {
    pub success: bool,
    pub data: Option<T>,
    pub error: Option<String>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Post {
    pub id: u64,
    pub title: String,
    pub body: String,
    pub user_id: u64,
}

/// API 客户端演示
#[tauri::command]
pub fn demo_api_client() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "HTTP 客户端".to_string(),
        "Rust 中常用的 HTTP 客户端:\n\
         • reqwest: 功能全面，异步\n\
         • ureq: 简单，同步\n\
         • surf: 异步，中间件支持".to_string(),
    );

    result.insert(
        "reqwest 示例".to_string(),
        r#"// Cargo.toml: reqwest = { version = "0.11", features = ["json"] }
// tokio = { version = "1", features = ["full"] }

use reqwest::Client;
use serde::Deserialize;

#[derive(Deserialize)]
struct Post {
    id: u64,
    title: String,
    body: String,
}

async fn fetch_posts() -> Result<Vec<Post>, reqwest::Error> {
    let client = Client::new();

    // GET 请求
    let posts = client
        .get("https://jsonplaceholder.typicode.com/posts")
        .header("User-Agent", "MyApp/1.0")
        .send()
        .await?
        .json::<Vec<Post>>()
        .await?;

    Ok(posts)
}

// POST 请求
async fn create_post(title: &str, body: &str) -> Result<Post, reqwest::Error> {
    let client = Client::new();

    let new_post = serde_json::json!({
        "title": title,
        "body": body,
        "userId": 1
    });

    let post = client
        .post("https://jsonplaceholder.typicode.com/posts")
        .json(&new_post)
        .send()
        .await?
        .json::<Post>()
        .await?;

    Ok(post)
}"#.to_string(),
    );

    result.insert(
        "错误处理".to_string(),
        r#"use reqwest::StatusCode;

async fn fetch_with_retry(url: &str) -> Result<String, Box<dyn std::error::Error>> {
    let client = Client::new();
    let max_retries = 3;

    for attempt in 1..=max_retries {
        let response = client.get(url).send().await?;

        match response.status() {
            StatusCode::OK => {
                return Ok(response.text().await?);
            }
            StatusCode::NOT_FOUND => {
                return Err("资源未找到".into());
            }
            status if status.is_server_error() => {
                if attempt == max_retries {
                    return Err(format!("服务器错误: {}", status).into());
                }
                tokio::time::sleep(Duration::from_secs(1)).await;
            }
            _ => {
                return Err(format!("HTTP 错误: {}", status).into());
            }
        }
    }

    Err("超过最大重试次数".into())
}"#.to_string(),
    );

    result.insert(
        "超时和拦截器".to_string(),
        r#"use reqwest::Client;
use std::time::Duration;

let client = Client::builder()
    .timeout(Duration::from_secs(10))
    .connect_timeout(Duration::from_secs(5))
    .user_agent("MyApp/1.0")
    // 添加默认 header
    .default_headers({
        let mut headers = reqwest::header::HeaderMap::new();
        headers.insert("Accept", "application/json".parse().unwrap());
        headers
    })
    .build()?"#.to_string(),
    );

    result
}

// ==================== 缓存系统示例 ====================

/// 缓存条目
#[derive(Debug, Clone, Serialize)]
pub struct CacheEntry<T> {
    pub value: T,
    pub created_at: u64,
    pub expires_at: u64,
}

/// 简单内存缓存
#[derive(Debug)]
pub struct MemoryCache<T> {
    store: RwLock<HashMap<String, CacheEntry<T>>>,
    default_ttl: Duration,
}

impl<T: Clone + Serialize + for<'de> Deserialize<'de>> MemoryCache<T> {
    pub fn new(default_ttl: Duration) -> Self {
        Self {
            store: RwLock::new(HashMap::new()),
            default_ttl,
        }
    }

    pub fn get(&self, key: &str) -> Option<T> {
        let store = self.store.read().unwrap();
        store.get(key).and_then(|entry| {
            let now = Instant::now().elapsed().as_secs();
            if now < entry.expires_at {
                Some(entry.value.clone())
            } else {
                None
            }
        })
    }

    pub fn set(&self, key: String, value: T, ttl: Option<Duration>) {
        let now = Instant::now().elapsed().as_secs();
        let ttl = ttl.unwrap_or(self.default_ttl);
        let entry = CacheEntry {
            value,
            created_at: now,
            expires_at: now + ttl.as_secs(),
        };
        let mut store = self.store.write().unwrap();
        store.insert(key, entry);
    }

    pub fn remove(&self, key: &str) {
        let mut store = self.store.write().unwrap();
        store.remove(key);
    }

    pub fn clear(&self) {
        let mut store = self.store.write().unwrap();
        store.clear();
    }
}

/// 缓存演示
#[tauri::command]
pub fn demo_cache() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "缓存策略".to_string(),
        "• 内存缓存: 快速，进程内\n\
         • Redis: 分布式，持久化\n\
         • moka: 高性能内存缓存\n\
         • cacache: 文件系统缓存".to_string(),
    );

    result.insert(
        "简单内存缓存实现".to_string(),
        r#"use std::collections::HashMap;
use std::sync::RwLock;
use std::time::{Duration, Instant};

#[derive(Clone)]
struct CacheEntry<T> {
    value: T,
    expires_at: Instant,
}

struct MemoryCache<T> {
    store: RwLock<HashMap<String, CacheEntry<T>>>,
}

impl<T: Clone> MemoryCache<T> {
    fn get(&self, key: &str) -> Option<T> {
        let store = self.store.read().unwrap();
        store.get(key).and_then(|entry| {
            if entry.expires_at > Instant::now() {
                Some(entry.value.clone())
            } else {
                None
            }
        })
    }

    fn set(&self, key: String, value: T, ttl: Duration) {
        let entry = CacheEntry {
            value,
            expires_at: Instant::now() + ttl,
        };
        self.store.write().unwrap().insert(key, entry);
    }
}"#.to_string(),
    );

    result.insert(
        "使用 moka 缓存库".to_string(),
        r#"// Cargo.toml: moka = { version = "0.11", features = ["sync"] }

use moka::sync::Cache;

let cache: Cache<String, String> = Cache::builder()
    .max_capacity(100)           // 最大条目数
    .time_to_live(Duration::from_secs(60))  // TTL
    .build();

// 插入
cache.insert("key".to_string(), "value".to_string());

// 获取
if let Some(value) = cache.get(&"key".to_string()) {
    println!("{}", value);
}

// 或使用 get_with 自动加载
let value = cache.get_with("key".to_string(), || {
    // 缓存未命中时执行
    fetch_from_database()
});"#.to_string(),
    );

    result.insert(
        "缓存装饰器模式".to_string(),
        r#"// 为数据访问层添加缓存
trait UserRepository {
    fn get_user(&self, id: u64) -> Option<User>;
}

struct CachedUserRepository {
    inner: Box<dyn UserRepository>,
    cache: MemoryCache<User>,
}

impl UserRepository for CachedUserRepository {
    fn get_user(&self, id: u64) -> Option<User> {
        let key = format!("user:{}", id);

        // 先查缓存
        if let Some(user) = self.cache.get(&key) {
            return Some(user);
        }

        // 缓存未命中，查询数据库
        if let Some(user) = self.inner.get_user(id) {
            self.cache.set(key, user.clone(), Duration::from_secs(300));
            return Some(user);
        }

        None
    }
}"#.to_string(),
    );

    result.insert(
        "缓存失效策略".to_string(),
        "• TTL (Time To Live): 固定过期时间\n\
         • LRU (Least Recently Used): 最近最少使用\n\
         • LFU (Least Frequently Used): 最少使用频率\n\
         • Write-through: 写入同时更新缓存\n\
         • Write-behind: 异步更新缓存\n\
         • Cache-aside: 应用代码管理缓存".to_string(),
    );

    result
}

// ==================== 状态机示例 ====================

/// 订单状态
#[derive(Debug, Clone, Copy, PartialEq, Eq, Serialize, Deserialize)]
pub enum OrderState {
    Created,
    Paid,
    Shipped,
    Delivered,
    Cancelled,
}

/// 状态转换事件
#[derive(Debug, Clone, Serialize, Deserialize)]
pub enum OrderEvent {
    Pay,
    Ship,
    Deliver,
    Cancel,
}

/// 状态机演示
#[tauri::command]
pub fn demo_state_machine() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "什么是状态机？".to_string(),
        "状态机定义了对象可能的状态，以及触发状态转换的事件。\n\
         状态机确保只能发生有效的状态转换。".to_string(),
    );

    result.insert(
        "枚举实现状态机".to_string(),
        r#"#[derive(Debug, Clone, Copy, PartialEq)]
enum OrderState {
    Created,
    Paid,
    Shipped,
    Delivered,
    Cancelled,
}

enum OrderEvent {
    Pay,
    Ship,
    Deliver,
    Cancel,
}

impl OrderState {
    fn transition(self, event: OrderEvent) -> Result<OrderState, String> {
        match (self, event) {
            (OrderState::Created, OrderEvent::Pay) => Ok(OrderState::Paid),
            (OrderState::Created, OrderEvent::Cancel) => Ok(OrderState::Cancelled),
            (OrderState::Paid, OrderEvent::Ship) => Ok(OrderState::Shipped),
            (OrderState::Paid, OrderEvent::Cancel) => Ok(OrderState::Cancelled),
            (OrderState::Shipped, OrderEvent::Deliver) => Ok(OrderState::Delivered),
            _ => Err(format!("无效转换: {:?} -> {:?}", self, event)),
        }
    }
}"#.to_string(),
    );

    result.insert(
        "使用状态机库".to_string(),
        r#"// Cargo.toml: sm = "0.2"

use sm::{sm, Transition};

sm! {
    TurnStile {
        InitialStates { Locked }

        Locked {
            Push => Locked,
            Coin => Unlocked,
        }

        Unlocked {
            Push => Locked,
            Coin => Unlocked,
        }
    }
}

let mut turnstile = TurnStile::new();
turnstile = turnstile.transition(Coin);
println!("{:?}", turnstile.state());  // Unlocked"#.to_string(),
    );

    result.insert(
        "类型级状态机".to_string(),
        r#"// 使用泛型在编译时保证状态正确性
struct Order<State> {
    id: u64,
    _state: PhantomData<State>,
}

// 状态标记类型
struct Created;
struct Paid;
struct Shipped;

// 只有 Created 状态可以支付
impl Order<Created> {
    fn new(id: u64) -> Self {
        Order { id, _state: PhantomData }
    }

    fn pay(self) -> Order<Paid> {
        Order { id: self.id, _state: PhantomData }
    }
}

// 只有 Paid 状态可以发货
impl Order<Paid> {
    fn ship(self) -> Order<Shipped> {
        Order { id: self.id, _state: PhantomData }
    }
}

// 编译器确保：
let order = Order::<Created>::new(1);
let order = order.pay();     // OK
// order.pay();              // 编译错误！Paid 状态没有 pay 方法
let order = order.ship();    // OK"#.to_string(),
    );

    result.insert(
        "状态图示例".to_string(),
        r#"订单状态流转:

┌─────────┐
│ Created │
└────┬────┘
     │
  ┌──┴──┐
  │ Pay │        ┌───────────┐
  ▼     │        │ Cancelled │
┌────┐  └───────►└───────────┘
│Paid│
└─┬──┘
  │ Ship
  ▼
┌───────┐
│Shipped│
└───┬───┘
    │ Deliver
    ▼
┌──────────┐
│ Delivered│ (终态)
└──────────┘"#.to_string(),
    );

    result.insert(
        "状态机最佳实践".to_string(),
        "• 使用枚举定义有限状态\n\
         • 明确定义允许的转换\n\
         • 考虑使用类型系统保证安全\n\
         • 复杂状态考虑使用状态机库\n\
         • 记录状态变更日志\n\
         • 处理无效转换的边界情况".to_string(),
    );

    result
}
