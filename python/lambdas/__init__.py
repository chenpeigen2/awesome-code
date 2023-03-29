""""
　　lambda 定义了一个匿名函数

　　lambda 并不会带来程序运行效率的提高，只会使代码更简洁。

　　如果可以使用for...in...if来完成的，坚决不用lambda。

　　如果使用lambda，lambda内不要包含循环，如果有，我宁愿定义函数来完成，使代码获得可重用性和更好的可读性。

　　总结：lambda 是为了减少单行函数的定义而存在的。

"""

# https://docs.python.org/zh-cn/3/tutorial/controlflow.html#lambda-expressions

if __name__ == '__main__':
    # basis
    g = lambda x: x + 1
    print(g(1))
    foo = [2, 18, 9, 22, 17, 24, 8, 12, 27]
    f1 = filter(lambda x: x % 3 == 0, foo)
    print(map(lambda x: x * 2 + 10, foo))

    pairs = [(1, 'one'), (2, 'two'), (3, 'three'), (4, 'four')]
    pairs.sort(key=lambda pair: pair[1])
    print(pairs)

    start = property(lambda self: object(), lambda self, v: None, lambda self: None)  # default
    fuck = lambda a, b: a + b
    v = fuck(1, 2)

    print(v)
