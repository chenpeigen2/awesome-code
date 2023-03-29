# usage of property
# https://www.runoob.com/python/python-func-property.html
class C:
    def __init__(self):
        self._x = None

    def getx(self):
        return self._x

    def setx(self, value):
        self._x = value

    def delx(self):
        del self._x

    # 如果 c 是 C 的实例化, c.x 将触发 getter,c.x = value 将触发 setter ， del c.x 触发 deleter。
    x = property(getx, setx, delx, "I'm the 'x' property.")


class D:
    def __init__(self):
        self._x = 12

    @property
    def x(self):
        """I'm the 'x' property."""
        return self._x

    @x.setter
    def x(self, value):
        self._x = value

    @x.deleter
    def x(self):
        del self._x


if __name__ == '__main__':
    d = D()
    del d.x
    d.x = 23
    print(d.x)
    print()

    # zip
    # 在多个迭代器上并行迭代，从每个迭代器返回一个数据项组成元组 (tuple)。
    v = zip(range(3), ['fee', 'fi', 'fo', 'fum'])
    # 与默认行为不同的是，它会检查可迭代对象的长度是否相同，如果不相同则触发ValueError 。
    # v = zip(range(3), ['fee', 'fi', 'fo', 'fum'], strict=False)
    # https: // docs.python.org / zh - cn / 3 / library / functions.html
    print(type(v))
    print(v)

    for item in v:
        print(item, type(item))

    # tuple
    # iterable - - 要转换为元组的可迭代序列。
    list1 = ['Google', 'Taobao', 'Runoob', 'Baidu']
    tuple1 = tuple(list1)
    print(tuple1)

    # appendix
    # 使用一对圆括号来表示空元组: ()
    # 使用一个后缀的逗号来表示单元组: a, 或(a, )
    # 使用以逗号分隔的多个项: a, b, c or (a, b, c)
    # 使用内置的 tuple(): tuple() 或tuple(iterable)


# Python不定长参数(*args、**kwargs含义)


def print_numbers(*args):
    print(type(args))  # tuple
    for n in args:
        print(type(n))  # int


l = [1, 2, 3, 4]
print_numbers(*l)  # *l，等价于 print_numbers(1, 2, 3, 4)
print_numbers(l)  # 将 l 作为一个整体传入，这样函数接受到的其实只有一个参数，且参数类型为 list


def register(name, email, **kwargs):
    print(type(kwargs))
    print('name:%s, age:%s, others:%s', (name, email, kwargs))


register("demon", "1@1.com")  # name:%s, age:%s, others:%s ('demon', '1@1.com', {})
register("demon", "1@1.com", addr="shanghai")  # name:%s, age:%s, others:%s ('demon', '1@1.com', {'addr': 'shanghai'})


def register(name, email, **kwargs):
    print('name:%s, age:%s, others:%s', (name, email, kwargs))


d = {"addr": "shanghai"}
register("demon", "1@1.com", **d)

d = {"name": "yrr", "email": "1@1.com", "addr": "shanghai"}
register(**d)

d = {"email": "yrr", "name": "1@1.com", "addr": "shanghai"}
print()
register(**d)
