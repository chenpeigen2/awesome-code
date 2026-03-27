"""
对象检查和属性操作内置函数示例
包括: type, isinstance, issubclass, callable, hasattr, getattr, setattr, delattr, dir, vars, id, hash
"""


def demo_type() -> None:
    """type() - 获取对象类型或创建新类型"""
    print("=== type() ===")

    # 获取类型
    print(f"type(123) = {type(123)}")
    print(f"type('hello') = {type('hello')}")
    print(f"type([1, 2, 3]) = {type([1, 2, 3])}")

    # 判断类型
    print(f"type(123) == int: {type(123) == int}")

    # 动态创建类
    Person = type('Person', (), {'name': 'default', 'greet': lambda self: f"Hello, {self.name}"})
    p = Person()
    p.name = 'Alice'
    print(f"Dynamic class: {p.greet()}")


def demo_isinstance_issubclass() -> None:
    """isinstance() 和 issubclass() - 类型检查"""
    print("\n=== isinstance() / issubclass() ===")

    # isinstance - 检查实例
    print(f"isinstance(123, int) = {isinstance(123, int)}")
    print(f"isinstance('hello', str) = {isinstance('hello', str)}")
    print(f"isinstance([1, 2], (list, tuple)) = {isinstance([1, 2], (list, tuple))}")  # 多种类型

    # issubclass - 检查继承关系
    class Animal: pass
    class Dog(Animal): pass

    print(f"issubclass(Dog, Animal) = {issubclass(Dog, Animal)}")
    print(f"issubclass(Dog, object) = {issubclass(Dog, object)}")


def demo_callable() -> None:
    """callable() - 检查对象是否可调用"""
    print("\n=== callable() ===")

    def func(): pass

    class CallableClass:
        def __call__(self):
            return "called"

    print(f"callable(func) = {callable(func)}")
    print(f"callable(print) = {callable(print)}")
    print(f"callable(lambda x: x) = {callable(lambda x: x)}")
    print(f"callable(123) = {callable(123)}")
    print(f"callable(CallableClass()) = {callable(CallableClass())}")


def demo_attribute_ops() -> None:
    """hasattr, getattr, setattr, delattr - 属性操作"""
    print("\n=== 属性操作函数 ===")

    class Person:
        def __init__(self, name: str):
            self.name = name

    p = Person('Alice')

    # hasattr - 检查属性
    print(f"hasattr(p, 'name') = {hasattr(p, 'name')}")
    print(f"hasattr(p, 'age') = {hasattr(p, 'age')}")

    # getattr - 获取属性
    print(f"getattr(p, 'name') = {getattr(p, 'name')}")
    print(f"getattr(p, 'age', 'default') = {getattr(p, 'age', 'default')}")  # 默认值

    # setattr - 设置属性
    setattr(p, 'age', 25)
    print(f"after setattr(p, 'age', 25): p.age = {p.age}")

    # delattr - 删除属性
    delattr(p, 'age')
    print(f"after delattr(p, 'age'): hasattr(p, 'age') = {hasattr(p, 'age')}")


def demo_dir() -> None:
    """dir() - 列出对象的属性和方法"""
    print("\n=== dir() ===")

    # 模块的属性
    import math
    math_attrs = [a for a in dir(math) if not a.startswith('_')][:5]
    print(f"dir(math) (first 5): {math_attrs}")

    # 对象的属性
    class Demo:
        def __init__(self):
            self.x = 1
        def method(self):
            pass

    d = Demo()
    print(f"dir(d): {[a for a in dir(d) if not a.startswith('_')]}")

    # 无参数 - 当前作用域
    # print(f"dir(): {dir()}")


def demo_vars() -> None:
    """vars() - 返回对象的 __dict__"""
    print("\n=== vars() ===")

    class Person:
        def __init__(self, name: str, age: int):
            self.name = name
            self.age = age

    p = Person('Alice', 25)
    print(f"vars(p) = {vars(p)}")

    # 修改
    vars(p)['city'] = 'Beijing'
    print(f"after vars(p)['city'] = 'Beijing': vars(p) = {vars(p)}")


def demo_id() -> None:
    """id() - 返回对象的唯一标识符（内存地址）"""
    print("\n=== id() ===")

    a = [1, 2, 3]
    b = [1, 2, 3]
    c = a

    print(f"id(a) = {id(a)}")
    print(f"id(b) = {id(b)}")
    print(f"id(c) = {id(c)}")
    print(f"a is b: {a is b}")  # 不同对象
    print(f"a is c: {a is c}")  # 同一对象

    # 小整数缓存
    x = 256
    y = 256
    print(f"256 is 256: {x is y}")

    x = 257
    y = 257
    print(f"257 is 257: {x is y}  # 可能不同（超出缓存范围）")


def demo_hash() -> None:
    """hash() - 返回对象的哈希值"""
    print("\n=== hash() ===")

    # 可哈希对象
    print(f"hash(123) = {hash(123)}")
    print(f"hash('hello') = {hash('hello')}")
    print(f"hash((1, 2, 3)) = {hash((1, 2, 3))}")

    # 不可哈希对象
    try:
        hash([1, 2, 3])
    except TypeError as e:
        print(f"hash([1, 2, 3]) error: {e}")

    # 自定义类的哈希
    class Point:
        def __init__(self, x: int, y: int):
            self.x = x
            self.y = y

        def __hash__(self):
            return hash((self.x, self.y))

        def __eq__(self, other):
            return (self.x, self.y) == (other.x, other.y)

    p1 = Point(1, 2)
    p2 = Point(1, 2)
    print(f"hash(Point(1, 2)) = {hash(p1)}")
    print(f"Point(1, 2) == Point(1, 2): {p1 == p2}")


if __name__ == "__main__":
    demo_type()
    demo_isinstance_issubclass()
    demo_callable()
    demo_attribute_ops()
    demo_dir()
    demo_vars()
    demo_id()
    demo_hash()
