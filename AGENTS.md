# AGENTS.md

This file provides guidelines and commands for AI agents working in this repository.

## Repository Overview

Multi-language exploration repository containing:
- **Java/Kotlin**: Algorithm practice, Spring Boot, framework explorations
- **Go Projects**: Fyne GUI apps and algorithm implementations
- **Python**: Data science, Flask, learning exercises
- **TypeScript/Bun**: Modern TypeScript projects using Bun runtime
- **Other**: C++, Groovy, Rust, Lua, Zig experiments

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
```

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
# Install
pip install -e .

# Run
python path/to/script.py

# Tests
pytest
pytest path/to/test_file.py::test_function
pytest -v
```

### TypeScript/Bun

```bash
# Install/Run
bun install
bun run index.ts
bun test
```

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
- Mock external dependencies

### Python
- pytest, parametrized tests
- Mock external dependencies

**Example:**
```python
def test_example():
    assert 1 + 1 == 2

@pytest.mark.parametrize("input,expected", [(1, 2), (2, 4)])
def test_double(input, expected):
    assert input * 2 == expected
```

### TypeScript
- Jest or Bun test runner
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

## Common Gotchas

- Go: `go mod tidy` after adding dependencies
- Python: Virtual environments recommended
- TypeScript: May need `@types/*` packages
- Java/Kotlin: Multi-module - use `./gradlew :module:task`
- LeetCode Java projects: Chinese class names for concepts
- Fyne projects: May need GUI development setup
- This is a learning repo - code quality varies

When in doubt, check existing code for patterns in each language directory.
