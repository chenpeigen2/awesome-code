"""
代码执行和编译相关内置函数示例
包括: eval, exec, compile, input, print, open, repr, ascii
"""


def demo_eval() -> None:
    """eval() - 执行字符串表达式"""
    print("=== eval() ===")

    # 基本数学表达式
    print(f"eval('2 + 3 * 4') = {eval('2 + 3 * 4')}")

    # 使用变量
    x = 10
    y = 20
    print(f"eval('x + y') = {eval('x + y')}")

    # 指定命名空间
    expr = "a + b"
    result = eval(expr, {"a": 1, "b": 2})
    print(f"eval('{expr}', {{'a': 1, 'b': 2}}) = {result}")

    # 安全警告：不要 eval 不受信任的输入！
    # 危险示例（已注释）：eval("__import__('os').system('rm -rf /')")


def demo_exec() -> None:
    """exec() - 执行字符串代码"""
    print("\n=== exec() ===")

    # 执行多行代码
    code = """
x = 10
y = 20
result = x + y
print(f"Inside exec: {x} + {y} = {result}")
"""
    exec(code)

    # 使用命名空间
    namespace = {}
    exec("z = 100", namespace)
    print(f"namespace['z'] = {namespace['z']}")

    # 返回 None
    result = exec("print('hello')")
    print(f"exec() returns: {result}")


def demo_compile() -> None:
    """compile() - 编译源代码为代码对象"""
    print("\n=== compile() ===")

    # 编译表达式
    expr = "2 ** 10"
    code_obj = compile(expr, "<string>", "eval")
    print(f"compile('{expr}', '<string>', 'eval') -> {eval(code_obj)}")

    # 编译单条语句
    stmt = "print('Hello from compiled code')"
    code_obj = compile(stmt, "<string>", "single")
    exec(code_obj)

    # 编译模块
    module_code = """
def greet(name):
    return f"Hello, {name}!"

result = greet("World")
"""
    code_obj = compile(module_code, "<string>", "exec")
    namespace = {}
    exec(code_obj, namespace)
    print(f"Module result: {namespace['result']}")

    # 模式: 'exec', 'eval', 'single'


def demo_input() -> None:
    """input() - 获取用户输入"""
    print("\n=== input() ===")

    # 基本用法（注释掉，避免交互）
    # name = input("Enter your name: ")
    # print(f"Hello, {name}!")

    # 带默认值的技巧
    # response = input("Continue? [y/N]: ") or "N"

    print("input() returns a string")
    print("Use int(input()) for numbers, but handle ValueError")


def demo_print() -> None:
    """print() - 打印输出"""
    print("\n=== print() ===")

    # 基本用法
    print("Basic print")

    # 多个参数
    print("Multiple", "arguments", "here")

    # sep 参数
    print("A", "B", "C", sep="-")

    # end 参数
    print("No newline", end=" | ")
    print("Next part")

    # 输出到文件
    import io
    buffer = io.StringIO()
    print("To buffer", file=buffer)
    print(f"Buffer content: {buffer.getvalue()!r}")

    # flush 参数
    print("Immediate flush", flush=True)


def demo_repr_ascii() -> None:
    """repr() 和 ascii() - 字符串表示"""
    print("\n=== repr() / ascii() ===")

    # repr - 返回对象的字符串表示
    print(f"repr('hello') = {repr('hello')}")
    print(f"repr(123) = {repr(123)}")
    print(f"repr([1, 2, 3]) = {repr([1, 2, 3])}")

    # ascii - 只使用 ASCII 字符
    print(f"ascii('hello') = {ascii('hello')}")
    print(f"ascii('中文') = {ascii('中文')}")  # 非ASCII转义
    print(f"ascii('😀') = {ascii('😀')}")  # emoji转义

    # 自定义 repr
    class Point:
        def __init__(self, x: int, y: int):
            self.x = x
            self.y = y

        def __repr__(self):
            return f"Point({self.x}, {self.y})"

    print(f"repr(Point(1, 2)) = {repr(Point(1, 2))}")


def demo_open() -> None:
    """open() - 打开文件"""
    print("\n=== open() ===")

    import tempfile
    import os

    # 创建临时文件演示
    with tempfile.NamedTemporaryFile(mode='w', delete=False, suffix='.txt') as f:
        temp_path = f.name
        f.write("Hello, World!\n这是测试内容。")

    try:
        # 读取文件
        with open(temp_path, 'r', encoding='utf-8') as f:
            content = f.read()
            print(f"File content: {content!r}")

        # 逐行读取
        with open(temp_path, 'r', encoding='utf-8') as f:
            for i, line in enumerate(f, 1):
                print(f"Line {i}: {line!r}")

        # 写入文件
        with open(temp_path, 'a', encoding='utf-8') as f:
            f.write("\n追加的内容")

        # 二进制模式
        with open(temp_path, 'rb') as f:
            binary = f.read(10)
            print(f"Binary (first 10 bytes): {binary}")

    finally:
        os.unlink(temp_path)


if __name__ == "__main__":
    demo_eval()
    demo_exec()
    demo_compile()
    # demo_input()  # 需要交互，跳过
    demo_print()
    demo_repr_ascii()
    demo_open()
