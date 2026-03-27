"""
数学和数字相关内置函数示例
包括: abs, divmod, pow, round, complex, hex, oct, bin, ord, chr
"""


def demo_abs() -> None:
    """abs() - 返回绝对值"""
    print("=== abs() ===")

    # 整数
    print(f"abs(-5) = {abs(-5)}")
    print(f"abs(5) = {abs(5)}")

    # 浮点数
    print(f"abs(-3.14) = {abs(-3.14)}")

    # 复数（返回模）
    print(f"abs(3+4j) = {abs(3+4j)}  # sqrt(3^2 + 4^2) = 5")


def demo_divmod() -> None:
    """divmod() - 同时返回商和余数"""
    print("\n=== divmod() ===")

    # 整数
    print(f"divmod(17, 5) = {divmod(17, 5)}  # 17 = 5*3 + 2")

    # 负数
    print(f"divmod(-17, 5) = {divmod(-17, 5)}")
    print(f"divmod(17, -5) = {divmod(17, -5)}")

    # 浮点数
    print(f"divmod(17.5, 5) = {divmod(17.5, 5)}")

    # 应用：时间转换
    total_seconds = 3725
    minutes, seconds = divmod(total_seconds, 60)
    hours, minutes = divmod(minutes, 60)
    print(f"{total_seconds} seconds = {hours}h {minutes}m {seconds}s")


def demo_pow() -> None:
    """pow() - 幂运算"""
    print("\n=== pow() ===")

    # 基本幂运算
    print(f"pow(2, 10) = {pow(2, 10)}  # 2^10 = 1024")
    print(f"pow(2, -1) = {pow(2, -1)}  # 2^(-1) = 0.5")

    # 三参数形式：模运算
    print(f"pow(2, 10, 100) = {pow(2, 10, 100)}  # (2^10) % 100 = 24")

    # 模逆运算
    print(f"pow(38, -1, 97) = {pow(38, -1, 97)}  # 38 * 51 ≡ 1 (mod 97)")

    # 对比 **
    print(f"2 ** 10 = {2 ** 10}")


def demo_round() -> None:
    """round() - 四舍五入"""
    print("\n=== round() ===")

    # 基本用法
    print(f"round(3.14159) = {round(3.14159)}")
    print(f"round(3.14159, 2) = {round(3.14159, 2)}")
    print(f"round(3.14159, 4) = {round(3.14159, 4)}")

    # 负数位数
    print(f"round(12345, -2) = {round(12345, -2)}  # 四舍五入到百位")

    # 银行家舍入法（round half to even）
    print(f"round(2.5) = {round(2.5)}  # 偶数")
    print(f"round(3.5) = {round(3.5)}  # 偶数")
    print(f"round(1.5) = {round(1.5)}  # 偶数")


def demo_numeric_bases() -> None:
    """bin, oct, hex - 进制转换"""
    print("\n=== 进制转换 ===")

    n = 255

    # 二进制
    print(f"bin({n}) = {bin(n)}")
    print(f"format({n}, 'b') = {format(n, 'b')}  # 无前缀")

    # 八进制
    print(f"oct({n}) = {oct(n)}")
    print(f"format({n}, 'o') = {format(n, 'o')}  # 无前缀")

    # 十六进制
    print(f"hex({n}) = {hex(n)}")
    print(f"format({n}, 'x') = {format(n, 'x')}  # 小写无前缀")
    print(f"format({n}, 'X') = {format(n, 'X')}  # 大写无前缀")

    # 负数
    print(f"bin(-5) = {bin(-5)}")


def demo_chr_ord() -> None:
    """chr() 和 ord() - 字符与 Unicode 码点转换"""
    print("\n=== chr() / ord() ===")

    # ord - 字符转码点
    print(f"ord('A') = {ord('A')}")
    print(f"ord('中') = {ord('中')}")
    print(f"ord('😀') = {ord('😀')}")

    # chr - 码点转字符
    print(f"chr(65) = {chr(65)!r}")
    print(f"chr(20013) = {chr(20013)!r}")
    print(f"chr(0x1F600) = {chr(0x1F600)!r}")

    # 互相转换
    for char in 'ABC中文':
        print(f"'{char}' -> ord: {ord(char)} -> chr: {chr(ord(char))!r}")


def demo_format() -> None:
    """format() - 格式化字符串"""
    print("\n=== format() ===")

    # 数字格式化
    print(f"format(3.14159, '.2f') = {format(3.14159, '.2f')}")
    print(f"format(12345, ',') = {format(12345, ',')}")  # 千位分隔符
    print(f"format(12345, ',.2f') = {format(12345, ',.2f')}")

    # 进制
    print(f"format(255, 'b') = {format(255, 'b')}  # 二进制")
    print(f"format(255, 'x') = {format(255, 'x')}  # 十六进制")
    print(f"format(255, '#x') = {format(255, '#x')}  # 带前缀")

    # 百分比
    print(f"format(0.856, '.2%') = {format(0.856, '.2%')}")

    # 科学计数法
    print(f"format(123456789, 'e') = {format(123456789, 'e')}")
    print(f"format(123456789, '.2e') = {format(123456789, '.2e')}")

    # 对齐
    print(f"format('hello', '>10') = {format('hello', '>10')!r}  # 右对齐")
    print(f"format('hello', '<10') = {format('hello', '<10')!r}  # 左对齐")
    print(f"format('hello', '^10') = {format('hello', '^10')!r}  # 居中")


if __name__ == "__main__":
    demo_abs()
    demo_divmod()
    demo_pow()
    demo_round()
    demo_numeric_bases()
    demo_chr_ord()
    demo_format()
