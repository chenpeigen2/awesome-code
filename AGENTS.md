# AGENTS.md

This file provides guidelines and commands for AI agents working in this repository.

## Repository Overview

Multi-language exploration repository containing:

- **Java/Kotlin**: Algorithm practice, Spring Boot, Vert.x, gRPC, framework explorations
- **Go Projects**: Fyne GUI apps and algorithm implementations
- **Python**: Data science, PyTorch, PyQt GUI apps, learning exercises
- **TypeScript/Bun**: Modern TypeScript projects using Bun runtime
- **Electron Apps**: Desktop applications (music-player, electron-music-player)
- **Mobile**: Android native, Flutter, React Native, Capacitor
- **Other**: C++, Groovy, Rust, Lua, Zig, Erlang experiments

## Project Structure

```
awesome-code/
├── kotlin/              # Kotlin basics and coroutines examples
├── python/              # Python learning (PyTorch, pandas, matplotlib)
├── go/                  # Go applications
├── rust/                # Rust experiments
├── zig/                 # Zig hello-world
├── lua/                 # Lua scripts
├── erl/                 # Erlang experiments
├── c++/                 # C++ references
├── flutter/             # Flutter mobile apps
├── rn/                  # React Native apps
├── js/                  # JavaScript experiments
├── music-player/        # Electron + React music player
├── javafx-music-player/ # JavaFX music player
├── py-imageviewer/      # PyQt6 image viewer
├── android/             # Android native projects
├── vertx/               # Vert.x projects (gRPC, web, config)
├── grpc/                # gRPC examples
├── dagger/              # Dagger DI examples
├── database/            # MongoDB, PostgreSQL, CouchDB
├── explore/             # Framework explorations
│   ├── ai/              # AI SDK experiments
│   ├── bun/             # Bun runtime experiments
│   ├── disruptor/       # LMAX Disruptor
│   ├── Mutiny/          # SmallRye Mutiny
│   ├── calcite-tutorial/# Apache Calcite
│   └── ...              # More frameworks
└── patterns/            # Design patterns
```

## Build Commands

### Java/Kotlin (Gradle)

```bash
# Build all projects
./gradlew build

# Run tests
./gradlew test

# Run specific test class
./gradlew test --tests <ClassName>

# Run specific test method
./gradlew test --tests <ClassName>.<MethodName>

# Clean build artifacts
./gradlew clean

# Build specific subproject
./gradlew :<subproject>:build

# Examples
./gradlew :grpc:build
./gradlew :vertx:vertx-grpc:test
./gradlew :explore:disruptor:build
```

**Note:** Java 25 and Kotlin 2.3.0 are used. Some modules are excluded due to compatibility:
- `kotlin:coroutines-examples` - Kotlin syntax errors
- `explore:spring-shell` - JDK 25 compatibility issues

### Go Projects

```bash
# Build/Run
go build -o appname ./path/to/main.go
go run ./path/to/main.go

# Tests
go test ./...
go test -run TestFunctionName ./path/to/package
go test -bench=. ./path/to/package

# Dependencies
go mod tidy
go get <package-name>
```

### Python Projects

```bash
# Install (using uv recommended)
uv pip install -e .
# Or traditional
pip install -e .

# Run
python path/to/script.py

# Tests
pytest
pytest path/to/test_file.py::test_function
pytest -v

# Linting (for py-imageviewer)
ruff check .
mypy .
```

### TypeScript/Bun

```bash
# Install/Run
bun install
bun run index.ts
bun test
```

### Electron Apps (music-player)

```bash
# Development
npm run dev                    # Web only
npm run electron:dev           # Full Electron app

# Platform-specific start
npm run start:linux
npm run start:mac
npm run start:win              # PowerShell
npm run start:win-cmd          # CMD

# Build
npm run build                  # Frontend only
npm run build:electron         # Electron main process
npm run electron:build         # Full desktop app

# Tests
npm run test
npm run test:watch
npm run test:coverage
```

## Dependencies Management

### Gradle Version Catalog

This project uses Gradle Version Catalog (`gradle/libs.versions.toml`) for centralized dependency management:

```kotlin
// In build.gradle.kts
dependencies {
    implementation(libs.guava)
    implementation(libs.okhttp)
    testImplementation(libs.junit.jupiter)
}

// Using bundles
implementation(libs.bundles.asm.all)
implementation(libs.bundles.grpc.all)
```

### Key Versions

| Technology | Version |
|------------|---------|
| Java | 25 |
| Kotlin | 2.3.0 |
| Gradle | 9.3.1 |
| JUnit | 5.14.2 |
| Guava | 33.5.0-jre |
| OkHttp | 5.3.0 |
| Vert.x | 4.5.24 |
| gRPC | 1.78.0 |
| Dagger | 2.59 |

## Code Style Guidelines

### Go

- `go fmt` formatting, package names lowercase
- Exported: PascalCase, unexported: camelCase
- Always check errors, use descriptive messages
- Interface names: -er suffix (Reader, Writer)

**Example:**
```go
result, err := someFunction()
if err != nil {
    return fmt.Errorf("failed: %w", err)
}
```

### Java

- Classes: PascalCase (e.g., `BinaryTree`, `TreeNode`)
- Methods: camelCase (e.g., `inorderTraversal`)
- Variables: camelCase, concise
- LeetCode comments include problem URLs
- Chinese class names for algorithmic concepts (e.g., `二叉树`)

**Example:**
```java
// https://leetcode.cn/problems/...
public List<Integer> inorderTraversal(TreeNode root) {
    List<Integer> ans = new ArrayList<>();
    traverse(root, ans);
    return ans;
}
```

### Kotlin

- Use idiomatic Kotlin, expression bodies
- `val` over `var`, string templates, coroutines
- Package naming: lowercase with dots (`org.peter.flow`)

**Example:**
```kotlin
fun main(): Unit = runBlocking {
    launch(Dispatchers.IO) { println("Hello") }
}
```

### Python

- snake_case functions, PascalCase classes
- Use f-strings, type hints where appropriate
- Ruff for linting (line-length: 100)

**Example:**
```python
def greet(name: str) -> str:
    return f'Hi, {name}'
```

### TypeScript

- Strict mode, camelCase functions, PascalCase classes
- Prefer `const` over `let`, arrow functions

**Example:**
```typescript
const server = Bun.serve({
    port: 3000,
    fetch(req) { return new Response("Hello!"); }
});
```

## Testing Guidelines

### Go
- Table-driven tests, testify for assertions
- Benchmark with `testing.B`

### Java/Kotlin
- JUnit 5, descriptive test names
- Mock external dependencies with Mockito

### Python
- pytest, parametrized tests
- pytest-qt for PyQt apps
- pytest-asyncio for async tests

**Example:**
```python
def test_example():
    assert 1 + 1 == 2

@pytest.mark.parametrize("input,expected", [(1, 2), (2, 4)])
def test_double(input, expected):
    assert input * 2 == expected
```

### TypeScript/Electron
- Vitest + Testing Library
- Unit test functions, integration tests

## Error Handling

### Go
- Check errors, `fmt.Errorf` with `%w` for wrapping

### Java
- Exceptions for errors, custom exception classes
- `finally` blocks for cleanup

### Kotlin
- Result types over exceptions where appropriate
- Safe call `?.` for nullable handling

### Python
- Exceptions, custom exception classes, `finally` blocks

## Notable Projects

### music-player (Electron + React)
A desktop music player with:
- Local music file playback (mp3, flac, wav, aac, ogg, ape)
- Online music search and download
- Lyrics window (always on top)
- Zustand state management with persistence
- See `music-player/AGENTS.md` for details

### py-imageviewer (PyQt6)
A feature-rich image viewer with:
- Local and online image browsing
- PyQt6 GUI
- Async support with aiohttp
- See `py-imageviewer/README.md` for details

### vertx/
Vert.x ecosystem projects:
- `vertx-grpc`: gRPC server/client
- `vertx-web`: Web applications
- `vertx-config`: Configuration management
- `service-discovery`: Service discovery pattern

### explore/
Framework and library explorations:
- `disruptor`: LMAX Disruptor patterns
- `Mutiny`: Reactive programming
- `calcite-tutorial`: Apache Calcite SQL
- `jsprit`: Vehicle routing
- `Kryo`: Serialization
- `snappy`: Compression

## Common Gotchas

- Go: `go mod tidy` after adding dependencies
- Python: Virtual environments recommended (`uv venv` or `python -m venv`)
- TypeScript: May need `@types/*` packages
- Java/Kotlin: Multi-module - use `./gradlew :module:task`
- LeetCode Java projects: Chinese class names for concepts
- Fyne projects: May need GUI development setup
- Electron: Need to build both main and renderer processes
- Gradle: Use version catalog (`libs.xxx`) instead of hardcoded versions
- This is a learning repo - code quality varies

## IDE Configuration

- Gradle uses Aliyun and Tencent mirrors for faster downloads in China
- Kotlin compiler uses `-Xjsr305=strict` for null-safety
- Java release target: 25

When in doubt, check existing code for patterns in each language directory.