# Okio 示例代码和工具类

本项目提供了 Okio 库的全面示例代码和实用工具类，帮助开发者快速理解和使用 Okio 进行高效的 I/O 操作。

## 目录结构

```
src/main/kotlin/org/peter/okio/
├── demo/                          # 示例代码
│   ├── BufferByteStringDemo.kt    # Buffer和ByteString核心演示
│   ├── SourceSinkDemo.kt          # Source和Sink流操作演示
│   ├── FileSystemDemo.kt          # FileSystem和Path文件操作演示
│   ├── AdvancedFeaturesDemo.kt    # 高级功能演示（Gzip、加密、超时等）
│   └── EncodingDemo.kt            # 编码转换演示
└── util/                          # 工具类
    ├── FileUtil.kt                # 文件操作工具类
    ├── ConvertUtil.kt             # 数据转换工具类
    └── StreamUtil.kt              # 流处理工具类
```

## Okio 核心概念

### 1. Buffer - 高效缓冲区

Buffer 是 Okio 的核心组件，它是一个可变的字节序列，支持高效的读写操作。

```kotlin
val buffer = Buffer()

// 写入数据
buffer.writeUtf8("Hello, Okio!")
buffer.writeInt(12345)
buffer.writeLong(Long.MAX_VALUE)

// 读取数据
val text = buffer.readUtf8()
val number = buffer.readInt()
val bigNumber = buffer.readLong()
```

**主要特点：**
- 内部使用分段链表结构，避免频繁的内存拷贝
- 支持多种数据类型的读写（UTF-8、Int、Long、Short、Double、Float等）
- 提供快照功能，可以无损地查看缓冲区内容

### 2. ByteString - 不可变字节序列

ByteString 是一个不可变的字节序列，类似于 String，但用于二进制数据。

```kotlin
// 创建 ByteString
val fromUtf8 = ByteString.encodeUtf8("Hello")
val fromHex = ByteString.decodeHex("48656C6C6F")
val fromBase64 = ByteString.decodeBase64("SGVsbG8=")

// 哈希计算
val md5 = fromUtf8.md5()
val sha256 = fromUtf8.sha256()
val hmac = fromUtf8.hmacSha256(ByteString.encodeUtf8("secret-key"))

// 编码转换
val hex = fromUtf8.hex()
val base64 = fromUtf8.base64()
```

**主要特点：**
- 不可变性保证线程安全
- 内置哈希计算功能（MD5、SHA-1、SHA-256、SHA-512）
- 支持 HMAC 消息认证码
- 支持 Base64 和十六进制编码

### 3. Source 和 Sink - 流式 I/O

Source 是输入流，Sink 是输出流，它们是 Okio 的基础 I/O 抽象。

```kotlin
// Source 读取
val source = file.source().buffer()
val content = source.readUtf8()
source.close()

// Sink 写入
val sink = file.sink().buffer()
sink.writeUtf8("Hello, World!")
sink.close()

// 使用 use 自动关闭资源
file.source().buffer().use { source ->
    println(source.readUtf8())
}
```

**主要特点：**
- 统一的 API 设计，简化 I/O 操作
- 自动处理缓冲，提高性能
- 支持超时控制
- 与 Java I/O 流无缝互操作

### 4. FileSystem 和 Path - 文件系统操作

Okio 提供了现代的文件系统 API，支持跨平台文件操作。

```kotlin
val fileSystem = FileSystem.SYSTEM

// 创建目录
fileSystem.createDirectories(Path("path/to/dir"))

// 写入文件
val path = Path("example.txt")
fileSystem.sink(path).buffer().use { sink ->
    sink.writeUtf8("Hello, FileSystem!")
}

// 读取文件
fileSystem.source(path).buffer().use { source ->
    println(source.readUtf8())
}

// 文件操作
fileSystem.copy(source, target)
fileSystem.move(source, target)
fileSystem.delete(path)

// 列出目录内容
val files = fileSystem.list(directory)
```

**主要特点：**
- 类型安全的路径操作
- 支持符号链接
- 提供文件元数据访问
- 跨平台兼容

## 工具类使用指南

### FileUtil - 文件操作工具

```kotlin
// 文本文件操作
FileUtil.writeText(path, "Hello, World!")
val content = FileUtil.readText(path)
FileUtil.appendText(path, "\nNew line")

// 行操作
val lines = FileUtil.readLines(path)
FileUtil.writeLines(path, listOf("Line 1", "Line 2"))

// 二进制文件操作
FileUtil.writeBytes(path, byteArrayOf(0x00, 0x01, 0x02))
val bytes = FileUtil.readBytes(path)

// 文件管理
FileUtil.copy(source, target)
FileUtil.move(source, target)
FileUtil.delete(path)
FileUtil.deleteRecursively(directory)

// 文件信息
val exists = FileUtil.exists(path)
val isFile = FileUtil.isFile(path)
val isDir = FileUtil.isDirectory(path)
val size = FileUtil.size(path)
val extension = FileUtil.getExtension(path)
```

### ConvertUtil - 数据转换工具

```kotlin
// 编码转换
val hex = ConvertUtil.bytesToHex(bytes)
val bytes = ConvertUtil.hexToBytes(hex)
val base64 = ConvertUtil.bytesToBase64(bytes)
val bytes = ConvertUtil.base64ToBytes(base64)

// 字符串编码
val hex = ConvertUtil.stringToHex("Hello")
val text = ConvertUtil.hexToString(hex)

// 哈希计算
val md5 = ConvertUtil.md5("password")
val sha256 = ConvertUtil.sha256("data")
val hmac = ConvertUtil.hmacSha256("data", "secret-key")

// 数值转换
val intBytes = ConvertUtil.intToBytes(12345)
val intValue = ConvertUtil.bytesToInt(intBytes)
val longBytes = ConvertUtil.longToBytes(Long.MAX_VALUE)
val longValue = ConvertUtil.bytesToLong(longBytes)

// 文件大小格式化
val formatted = ConvertUtil.formatFileSize(1536)  // "1.50 KB"
val bytes = ConvertUtil.parseFileSize("1.5MB")    // 1572864
```

### StreamUtil - 流处理工具

```kotlin
// 流读取
val bytes = StreamUtil.readAllBytes(source)
val text = StreamUtil.readAllString(source)

// 流复制
val bytesCopied = StreamUtil.copy(source, sink)

// 带进度的复制
StreamUtil.copyWithProgress(source, sink) { bytesTransferred ->
    println("已传输: $bytesTransferred 字节")
}

// Gzip 压缩
val compressed = StreamUtil.compress(data)
val decompressed = StreamUtil.decompress(compressed)

// 流式哈希
val hash = StreamUtil.hashStream(source, "SHA-256")
val hmac = StreamUtil.hashStreamWithKey(source, "HMAC-SHA256", key)

// 限制读取字节数
val limitedSource = StreamUtil.limitSource(source, 1024)

// 统计读写字节数
val countingSink = StreamUtil.countingSink(sink)
val countingSource = StreamUtil.countingSource(source)
```

## 高级功能

### Gzip 压缩

```kotlin
// 压缩数据
val buffer = Buffer()
val gzipSink = GzipSink(buffer)
gzipSink.buffer().use { sink ->
    sink.writeUtf8("Large text content...")
}
val compressed = buffer.readByteArray()

// 解压数据
val gzipSource = GzipSource(Buffer().write(compressed))
val decompressed = gzipSource.buffer().use { it.readUtf8() }
```

### 哈希和加密

```kotlin
val data = ByteString.encodeUtf8("Sensitive data")

// 哈希计算
val md5 = data.md5()
val sha256 = data.sha256()
val sha512 = data.sha512()

// HMAC 消息认证
val key = ByteString.encodeUtf8("secret-key")
val hmac = data.hmacSha256(key)

// 流式哈希
val hashingSink = HashingSink.sha256(sink)
val hashingSource = HashingSource.sha256(source)
```

### 超时控制

```kotlin
// 设置读取超时
val source = socket.source().buffer()
source.timeout.timeout(5000, TimeUnit.MILLISECONDS)

// 设置写入超时
val sink = socket.sink().buffer()
sink.timeout.timeout(10000, TimeUnit.MILLISECONDS)
```

### Forwarding 类扩展

```kotlin
// 自定义 Sink
class LoggingSink(delegate: Sink) : ForwardingSink(delegate) {
    override fun write(source: Buffer, byteCount: Long) {
        println("Writing $byteCount bytes")
        super.write(source, byteCount)
    }
}

// 自定义 Source
class LoggingSource(delegate: Source) : ForwardingSource(delegate) {
    override fun read(sink: Buffer, byteCount: Long): Long {
        val bytesRead = super.read(sink, byteCount)
        println("Read $bytesRead bytes")
        return bytesRead
    }
}
```

## 最佳实践

### 1. 使用 use 函数自动关闭资源

```kotlin
// 推荐
file.source().buffer().use { source ->
    println(source.readUtf8())
}

// 不推荐
val source = file.source().buffer()
println(source.readUtf8())
source.close()  // 容易忘记关闭
```

### 2. 使用缓冲提高性能

```kotlin
// 推荐：使用 buffer() 创建缓冲流
val bufferedSource = source.buffer()
val bufferedSink = sink.buffer()

// 不推荐：直接使用原始流
val rawSource = source  // 每次读取可能只读取很少字节
```

### 3. 合理使用 ByteString 和 Buffer

```kotlin
// 不可变数据使用 ByteString
val hash = ByteString.encodeUtf8(data).sha256()

// 可变数据使用 Buffer
val buffer = Buffer()
buffer.writeUtf8(part1)
buffer.writeUtf8(part2)
```

### 4. 异常处理

```kotlin
try {
    FileUtil.writeText(path, content)
} catch (e: IOException) {
    println("写入文件失败: ${e.message}")
}
```

## 运行示例

```bash
# 构建项目
./gradlew build

# 运行测试
./gradlew test

# 运行示例
./gradlew run --args="BufferByteStringDemo"
```

## 依赖配置

```kotlin
dependencies {
    implementation("com.squareup.okio:okio:3.7.0")
}
```

## 参考资料

- [Okio 官方文档](https://square.github.io/okio/)
- [Okio GitHub 仓库](https://github.com/square/okio)
- [Okio API 参考](https://square.github.io/okio/3.x/okio/okio/)
