"""
高级和异步相关内置函数示例
包括: super, property, staticmethod, classmethod, aiter, anext, breakpoint, __import__
"""


def demo_super() -> None:
    """super() - 调用父类方法"""
    print("=== super() ===")

    class Animal:
        def __init__(self, name: str):
            self.name = name

        def speak(self) -> str:
            return f"{self.name} makes a sound"

    class Dog(Animal):
        def __init__(self, name: str, breed: str):
            super().__init__(name)  # 调用父类 __init__
            self.breed = breed

        def speak(self) -> str:
            base = super().speak()  # 调用父类方法
            return f"{base}, and barks!"

    dog = Dog("Buddy", "Golden Retriever")
    print(dog.speak())

    # 多重继承
    class A:
        def method(self):
            print("A.method")

    class B(A):
        def method(self):
            print("B.method")
            super().method()

    class C(A):
        def method(self):
            print("C.method")
            super().method()

    class D(B, C):
        def method(self):
            print("D.method")
            super().method()

    print("\nMRO:", D.__mro__)
    d = D()
    d.method()


def demo_decorators() -> None:
    """@property, @staticmethod, @classmethod 装饰器"""
    print("\n=== 装饰器函数 ===")

    class Circle:
        pi = 3.14159  # 类属性

        def __init__(self, radius: float):
            self._radius = radius

        @property
        def radius(self) -> float:
            """获取半径"""
            return self._radius

        @radius.setter
        def radius(self, value: float):
            """设置半径"""
            if value < 0:
                raise ValueError("Radius cannot be negative")
            self._radius = value

        @radius.deleter
        def radius(self):
            """删除半径"""
            del self._radius

        @property
        def area(self) -> float:
            """计算面积（只读属性）"""
            return self.pi * self._radius ** 2

        @classmethod
        def from_diameter(cls, diameter: float):
            """从直径创建圆（类方法）"""
            return cls(diameter / 2)

        @staticmethod
        def get_pi() -> float:
            """获取 π 值（静态方法）"""
            return 3.14159

    c = Circle(5)
    print(f"Radius: {c.radius}")
    print(f"Area: {c.area}")

    c.radius = 10
    print(f"New radius: {c.radius}")

    # 类方法创建实例
    c2 = Circle.from_diameter(10)
    print(f"From diameter: radius = {c2.radius}")

    # 静态方法
    print(f"π = {Circle.get_pi()}")


def demo_property_func() -> None:
    """property() 函数的函数形式"""
    print("\n=== property() 函数形式 ===")

    class Temperature:
        def __init__(self, celsius: float = 0):
            self._celsius = celsius

        def get_celsius(self):
            return self._celsius

        def set_celsius(self, value):
            if value < -273.15:
                raise ValueError("Below absolute zero!")
            self._celsius = value

        def del_celsius(self):
            del self._celsius

        celsius = property(get_celsius, set_celsius, del_celsius, "Temperature in Celsius")

    t = Temperature(25)
    print(f"Celsius: {t.celsius}")
    t.celsius = 30
    print(f"New celsius: {t.celsius}")


def demo_breakpoint() -> None:
    """breakpoint() - 调试断点"""
    print("\n=== breakpoint() ===")

    print("breakpoint() 会暂停程序进入调试器")
    print("在调试器中可以使用:")
    print("  n - 执行下一行")
    print("  c - 继续执行")
    print("  p variable - 打印变量")
    print("  q - 退出")

    # 取消注释以测试:
    # x = 10
    # breakpoint()  # 程序会在这里暂停
    # print(f"x = {x}")


def demo_import() -> None:
    """__import__() - 动态导入模块"""
    print("\n=== __import__() ===")

    # 基本用法
    math = __import__('math')
    print(f"__import__('math').sqrt(16) = {math.sqrt(16)}")

    # 导入子模块
    # os_path = __import__('os.path', fromlist=['path'])
    # print(f"os.path.join: {os_path.join}")

    # 通常使用 importlib 替代
    import importlib
    json = importlib.import_module('json')
    print(f"importlib.import_module('json').dumps: {json.dumps}")


async def demo_async_iter() -> None:
    """aiter() 和 anext() - 异步迭代器"""
    print("\n=== aiter() / anext() ===")

    class AsyncCounter:
        def __init__(self, count: int):
            self.count = count
            self.current = 0

        def __aiter__(self):
            return self

        async def __anext__(self):
            if self.current >= self.count:
                raise StopAsyncIteration
            self.current += 1
            return self.current

    # 使用 aiter 和 anext
    async_counter = AsyncCounter(3)
    async_iter = aiter(async_counter)

    print("Using aiter/anext:")
    try:
        while True:
            value = await anext(async_iter)
            print(f"  Got: {value}")
    except StopAsyncIteration:
        print("  Done!")

    # 使用 async for
    print("\nUsing async for:")
    async for i in AsyncCounter(3):
        print(f"  Got: {i}")


def demo_memoryview() -> None:
    """memoryview() - 内存视图"""
    print("\n=== memoryview() ===")

    # 创建字节对象的内存视图
    data = b'Hello, World!'
    mv = memoryview(data)

    print(f"Original: {data}")
    print(f"memoryview: {mv}")
    print(f"mv[0]: {mv[0]} (byte value)")
    print(f"mv[0:5]: {mv[0:5]}")
    print(f"mv[0:5].tobytes(): {mv[0:5].tobytes()}")

    # 可变内存视图
    mutable = bytearray(b'hello')
    mv = memoryview(mutable)
    print(f"\nBefore: {mutable}")
    mv[0] = 72  # 'H'
    print(f"After mv[0] = 72: {mutable}")

    # 高效操作（不复制数据）
    large_data = bytes(range(256))
    mv = memoryview(large_data)
    print(f"\nSlice without copy: {mv[100:110].tobytes()}")


if __name__ == "__main__":
    demo_super()
    demo_decorators()
    demo_property_func()
    demo_breakpoint()
    demo_import()
    demo_memoryview()

    # 异步示例需要单独运行
    print("\n--- Running async demo ---")
    import asyncio
    asyncio.run(demo_async_iter())
