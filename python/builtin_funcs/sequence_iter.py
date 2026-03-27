"""
序列和迭代器相关内置函数示例
包括: sorted, reversed, enumerate, zip, map, filter, slice, range, len, sum, max, min
"""

from typing import Any


def demo_sorted() -> None:
    """sorted() - 返回排序后的列表"""
    print("=== sorted() ===")

    # 基本排序
    print(f"sorted([3, 1, 4, 1, 5]) = {sorted([3, 1, 4, 1, 5])}")

    # 降序
    print(f"sorted([3, 1, 4, 1, 5], reverse=True) = {sorted([3, 1, 4, 1, 5], reverse=True)}")

    # 使用 key 函数
    words = ['apple', 'pie', 'a', 'longer']
    print(f"sorted by length: {sorted(words, key=len)}")

    # 按多个条件排序
    students = [('Alice', 85), ('Bob', 92), ('Charlie', 85)]
    print(f"sorted by score then name: {sorted(students, key=lambda x: (-x[1], x[0]))}")


def demo_reversed() -> None:
    """reversed() - 返回反向迭代器"""
    print("\n=== reversed() ===")
    print(f"list(reversed([1, 2, 3])) = {list(reversed([1, 2, 3]))}")
    print(f"list(reversed('hello')) = {list(reversed('hello'))}")
    print(f"list(reversed(range(5))) = {list(reversed(range(5)))}")


def demo_enumerate() -> None:
    """enumerate() - 返回带索引的枚举对象"""
    print("\n=== enumerate() ===")
    fruits = ['apple', 'banana', 'cherry']

    # 默认从 0 开始
    print("默认索引:")
    for i, fruit in enumerate(fruits):
        print(f"  {i}: {fruit}")

    # 指定起始索引
    print("\n从 1 开始:")
    for i, fruit in enumerate(fruits, start=1):
        print(f"  {i}: {fruit}")


def demo_zip() -> None:
    """zip() - 并行迭代多个序列"""
    print("\n=== zip() ===")

    names = ['Alice', 'Bob', 'Charlie']
    ages = [25, 30, 35]
    cities = ['Beijing', 'Shanghai', 'Guangzhou']

    # 基本用法
    print("zip(names, ages):")
    for name, age in zip(names, ages):
        print(f"  {name}: {age}")

    # 多个序列
    print("\nzip(names, ages, cities):")
    for name, age, city in zip(names, ages, cities):
        print(f"  {name}, {age}, {city}")

    # 不同长度 - 以最短的为准
    short = [1, 2]
    long = [10, 20, 30, 40]
    print(f"\nzip(short, long): {list(zip(short, long))}")

    # 使用 strict 模式（Python 3.10+）
    try:
        list(zip(short, long, strict=True))
    except ValueError as e:
        print(f"strict mode error: {e}")


def demo_map() -> None:
    """map() - 对序列每个元素应用函数"""
    print("\n=== map() ===")

    # 平方
    numbers = [1, 2, 3, 4, 5]
    squared = map(lambda x: x**2, numbers)
    print(f"map(x**2, {numbers}) = {list(squared)}")

    # 字符串转大写
    words = ['hello', 'world']
    upper = map(str.upper, words)
    print(f"map(upper, {words}) = {list(upper)}")

    # 多个序列
    a = [1, 2, 3]
    b = [10, 20, 30]
    summed = map(lambda x, y: x + y, a, b)
    print(f"map(x+y, {a}, {b}) = {list(summed)}")


def demo_filter() -> None:
    """filter() - 过滤序列元素"""
    print("\n=== filter() ===")

    # 过滤偶数
    numbers = range(10)
    evens = filter(lambda x: x % 2 == 0, numbers)
    print(f"filter(even, range(10)) = {list(evens)}")

    # 过滤非空字符串
    strings = ['', 'hello', '', 'world', None, 'python']
    non_empty = filter(None, strings)  # None 表示过滤掉 falsy 值
    print(f"filter(None, {strings}) = {list(non_empty)}")


def demo_slice() -> None:
    """slice() - 创建切片对象"""
    print("\n=== slice() ===")

    # slice(stop)
    s = slice(3)
    print(f"'hello'[slice(3)] = {'hello'[s]}")

    # slice(start, stop)
    s = slice(1, 4)
    print(f"'hello'[slice(1, 4)] = {'hello'[s]}")

    # slice(start, stop, step)
    s = slice(0, 10, 2)
    print(f"list(range(10))[slice(0, 10, 2)] = {list(range(10))[s]}")

    # 用于多维数组（如 numpy）
    import numpy as np
    arr = np.array([[1, 2, 3], [4, 5, 6], [7, 8, 9]])
    print(f"numpy array[slice(0, 2), slice(0, 2)]:\n{arr[slice(0, 2), slice(0, 2)]}")


def demo_aggregate() -> None:
    """len, sum, max, min - 聚合函数"""
    print("\n=== 聚合函数 ===")

    numbers = [1, 2, 3, 4, 5]

    # len
    print(f"len({numbers}) = {len(numbers)}")
    print(f"len('hello') = {len('hello')}")

    # sum
    print(f"sum({numbers}) = {sum(numbers)}")
    print(f"sum({numbers}, start=100) = {sum(numbers, start=100)}")

    # max / min
    print(f"max({numbers}) = {max(numbers)}")
    print(f"min({numbers}) = {min(numbers)}")

    # 带 key 函数
    words = ['apple', 'pie', 'a', 'longer']
    print(f"max by length: {max(words, key=len)}")
    print(f"min by length: {min(words, key=len)}")

    # 多个参数
    print(f"max(1, 5, 2, 3) = {max(1, 5, 2, 3)}")
    print(f"min(1, 5, 2, 3) = {min(1, 5, 2, 3)}")


def demo_all_any() -> None:
    """all() 和 any() - 逻辑判断"""
    print("\n=== all() / any() ===")

    # all - 全部为 True
    print(f"all([True, True, True]) = {all([True, True, True])}")
    print(f"all([True, False, True]) = {all([True, False, True])}")
    print(f"all([1, 2, 3]) = {all([1, 2, 3])}")
    print(f"all([]) = {all([])}  # 空序列返回 True")

    # any - 任一为 True
    print(f"any([False, False, True]) = {any([False, False, True])}")
    print(f"any([False, False, False]) = {any([False, False, False])}")
    print(f"any([0, '', None]) = {any([0, '', None])}")
    print(f"any([]) = {any([])}  # 空序列返回 False")


if __name__ == "__main__":
    demo_sorted()
    demo_reversed()
    demo_enumerate()
    demo_zip()
    demo_map()
    demo_filter()
    demo_slice()
    demo_aggregate()
    demo_all_any()
