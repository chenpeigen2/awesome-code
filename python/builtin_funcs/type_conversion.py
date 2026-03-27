"""
类型转换内置函数示例
包括: int, float, bool, str, list, tuple, dict, set, frozenset, complex, bytes, bytearray
"""

from typing import Any


def demo_int() -> None:
    """int() - 将字符串或数字转换为整数"""
    print("=== int() ===")

    # 字符串转整数
    print(f"int('123') = {int('123')}")
    print(f"int('1010', 2) = {int('1010', 2)}")  # 二进制
    print(f"int('FF', 16) = {int('FF', 16)}")  # 十六进制

    # 浮点数转整数（截断）
    print(f"int(3.7) = {int(3.7)}")
    print(f"int(-3.7) = {int(-3.7)}")


def demo_float() -> None:
    """float() - 将字符串或数字转换为浮点数"""
    print("\n=== float() ===")
    print(f"float('3.14') = {float('3.14')}")
    print(f"float('inf') = {float('inf')}")
    print(f"float('-inf') = {float('-inf')}")
    print(f"float('nan') = {float('nan')}")
    print(f"float(42) = {float(42)}")


def demo_bool() -> None:
    """bool() - 将值转换为布尔值"""
    print("\n=== bool() ===")
    # False 的情况
    false_values: list[Any] = [None, False, 0, 0.0, '', (), [], {}, set(), range(0)]
    for v in false_values:
        print(f"bool({v!r}) = {bool(v)}")

    # True 的情况
    print(f"bool(1) = {bool(1)}")
    print(f"bool('hello') = {bool('hello')}")
    print(f"bool([0]) = {bool([0])}")


def demo_str() -> None:
    """str() - 将对象转换为字符串"""
    print("\n=== str() ===")
    print(f"str(123) = {str(123)!r}")
    print(f"str(3.14) = {str(3.14)!r}")
    print(f"str([1, 2, 3]) = {str([1, 2, 3])!r}")
    print(f"str({'a': 1}) = {str({'a': 1})!r}")

    # 指定编码
    print(f"str(b'hello', 'utf-8') = {str(b'hello', 'utf-8')!r}")


def demo_list() -> None:
    """list() - 将可迭代对象转换为列表"""
    print("\n=== list() ===")
    print(f"list('hello') = {list('hello')}")
    print(f"list(range(5)) = {list(range(5))}")
    print(f"list((1, 2, 3)) = {list((1, 2, 3))}")
    print(f"list({1, 2, 3}) = {list({1, 2, 3})}")


def demo_tuple() -> None:
    """tuple() - 将可迭代对象转换为元组"""
    print("\n=== tuple() ===")
    print(f"tuple('hello') = {tuple('hello')}")
    print(f"tuple([1, 2, 3]) = {tuple([1, 2, 3])}")
    print(f"tuple(range(3)) = {tuple(range(3))}")


def demo_dict() -> None:
    """dict() - 创建字典"""
    print("\n=== dict() ===")
    # 空字典
    print(f"dict() = {dict()}")

    # 从键值对
    print(f"dict(a=1, b=2) = {dict(a=1, b=2)}")

    # 从可迭代对象
    print(f"dict([('a', 1), ('b', 2)]) = {dict([('a', 1), ('b', 2)])}")

    # 字典推导式
    print(f"dict((i, i**2) for i in range(5)) = {dict((i, i**2) for i in range(5))}")


def demo_set_frozenset() -> None:
    """set() 和 frozenset() - 创建集合"""
    print("\n=== set() / frozenset() ===")
    print(f"set([1, 2, 2, 3]) = {set([1, 2, 2, 3])}")
    print(f"set('hello') = {set('hello')}")

    # frozenset 是不可变的
    fs = frozenset([1, 2, 3])
    print(f"frozenset([1, 2, 3]) = {fs}")
    print(f"type(fs) = {type(fs)}")


def demo_complex() -> None:
    """complex() - 创建复数"""
    print("\n=== complex() ===")
    print(f"complex(1, 2) = {complex(1, 2)}")
    print(f"complex('1+2j') = {complex('1+2j')}")
    print(f"complex(3) = {complex(3)}")

    c = complex(3, 4)
    print(f"c.real = {c.real}, c.imag = {c.imag}")


def demo_bytes_bytearray() -> None:
    """bytes() 和 bytearray() - 字节序列"""
    print("\n=== bytes() / bytearray() ===")

    # bytes - 不可变
    print(f"bytes('hello', 'utf-8') = {bytes('hello', 'utf-8')}")
    print(f"bytes([72, 101, 108, 108, 111]) = {bytes([72, 101, 108, 108, 111])}")
    print(f"b'hello' = {b'hello'}")

    # bytearray - 可变
    ba = bytearray(b'hello')
    print(f"bytearray(b'hello') = {ba}")
    ba[0] = 72  # 修改
    print(f"after ba[0] = 72: {ba}")


if __name__ == "__main__":
    demo_int()
    demo_float()
    demo_bool()
    demo_str()
    demo_list()
    demo_tuple()
    demo_dict()
    demo_set_frozenset()
    demo_complex()
    demo_bytes_bytearray()
