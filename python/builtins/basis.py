class A(object):
    def __init__(self, num):
        self.num = num
        self.start_num = -1

    def __iter__(self):
        """
        @summary: 迭代器，生成迭代对象时调用，返回值必须是对象自己,然后for可以循环调用next方法
        """
        print("__iter__")
        return self

    def __next__(self):
        """
        @summary: 每一次for循环都调用该方法（必须存在）
        """
        self.start_num += 1
        if self.start_num >= self.num:
            raise StopIteration()
        return self.start_num


if __name__ == '__main__':
    # 内置函数range()常用于遍历数字序列，该函数可以生成算术级数：

    # class range(stop)
    # class range(start, stop[, step])
    print(type(range(5)))
    r1 = range(5)
    print(r1.__len__())
    print(len(r1))
    print(r1.start, r1.stop, r1.step)
    print('----')
    for i in range(5):
        print(i)

    # for … in … 这个语句其实做了两件事。
    # 第一件事是获得一个可迭代器，即调用了__iter__()函数。 第二件事是循环的过程，循环调用__next__()函数。
    for i in A(10):
        print(i)

    # repr()函数将对象转化为供解释器读取的形式。
    # https: // www.runoob.com / python / python - func - repr.html
    # 参数：object 返回值：返回一个对象的 string 格式。
    s = 'RUNOOB'
    s1 = "RUNOOB"
    print(repr(s))
    print(repr(s1))

    dict = {'a': 'b', 'c': 'd', 'e': 'f'}
    dict = repr(dict)
    print(dict)

    # reversed函数返回一个反转的迭代器。
    # https: // www.runoob.com / python3 / python3 - func - reversed.html
    # 参数 seq -- 要转换的序列，可以是 tuple, string, list 或 range。
    seqString = "abcdefg"
    print(type(reversed(seqString)))
    for x in reversed(seqString):
        print(x)
    # 字符串
    seqString = 'Runoob'
    print(list(reversed(seqString)))

    # 元组
    seqTuple = ('R', 'u', 'n', 'o', 'o', 'b')
    print(list(reversed(seqTuple)))

    # range
    seqRange = range(5, 9)
    print(list(reversed(seqRange)))

    # 列表
    seqList = [1, 2, 4, 3, 5]
    print(list(reversed(seqList)))
    # 返回number舍入到小数点后ndigits位精度的值。 如果ndigits被省略或为None，则返回最接近输入值的整数。
    v = round(2.676, 2)
    print(v)

    """
    enumerate(iterable, start=0)
    返回一个枚举对象。iterable 必须是一个序列，或 iterator，或其他支持迭代的对象。
    enumerate() 函数用于将一个可遍历的数据对象(如列表、元组或字符串)组合为一个索引序列，同时列出数据和数据下标，一般用在 for 循环当中。
    """
    seasons = ['Spring', 'Summer', 'Fall', 'Winter']
    print(list(enumerate(seasons)))
    print(list(enumerate(seasons, start=1)))  # 下标从 1 开始
    seq = ['one', 'two', 'three']
    for i, element in enumerate(seq):
        print(i, element)

    """
    eval(expression[, globals[, locals]])
    eval() 函数用来执行一个字符串表达式，并返回表达式的值。
    """
    x = 7
    print(eval('3 * x'))
    print("#################")

    """
    exec()
    """
    exec("print('hello world exec')")

    """
    pow(base, exp[, mod])
    返回 base 的 exp 次幂；如果 mod 存在，则返回 base 的 exp 次幂对 mod 
    取余（比 pow(base, exp) % mod 更高效）。 两参数形式 pow(base, exp) 等价于乘方运算符: base**exp。
    """
    print(pow(10, 2, mod=7))
    print(pow(38, -1, mod=97))

    """
    abs(x)
    返回一个数的绝对值。 参数可以是整数、浮点数或任何实现了 __abs__() 的对象。 如果参数是一个复数，则返回它的模。
    """
    print(abs(-1.2))

    """
    max(iterable, *[, key, default])¶
    max(arg1, arg2, *args[, key])
    返回可迭代对象中最大的元素，或者返回两个及以上实参中最大的。
    
    min(iterable, *[, key, default])
    min(arg1, arg2, *args[, key])
    返回可迭代对象中最小的元素，或者返回两个及以上实参中最小的。
    
    如果只提供了一个位置参数，它必须是非空 iterable，返回可迭代对象中最大的元素；如果提供了两个及以上的位置参数，则返回最大的位置参数。
    """
    print(max(1, 2, 3, 4))
    print(min(1, 2, 3, 4))

    """
    sum(iterable, /, start=0)
    从 start 开始自左向右对 iterable 的项求和并返回总计值。 iterable 的项通常为数字，而 start 值则不允许为字符串。
    
    对某些用例来说，存在 sum() 的更好替代。 
    拼接字符串序列的更好更快方式是调用 ''.join(sequence)。 
    要以扩展精度对浮点值求和，请参阅 math.fsum()。 要拼接一系列可迭代对象，请考虑使用 itertools.chain()。
    """
    print(sum([1, 2, 3, 4, 5], start=12))
    print("////")

    """"
    返回一个不带特征的新对象。object 是所有类的基类。它带有所有 Python 类实例均通用的方法。本函数不接受任何参数。
    注解 由于 object 没有 __dict__，因此无法将任意属性赋给 object 的实例。
    """
    obj = object()
    # obj.x = 23 error

    """
    bin(x)
    将整数转变为以“0b”前缀的二进制字符串。结果是一个合法的 Python 表达式。
    """
    print(bin(5))

    """
    oct(x)
    将一个整数转变为一个前缀为“0o”的八进制字符串。结果是一个合法的 Python 表达式。
    如果 x 不是 Python 的 int 对象，那它需要定义 __index__() 方法返回一个整数。一些例子：
    """
    oct(8)

    """
    hex()
    将整数转换为以“0x”为前缀的小写十六进制字符串。如果 x 不是 Python int 对象，则必须定义返回整数的 __index__() 方法。一些例子：
    """
    hex(19)

    """
    ord(c)
    对表示单个 Unicode 字符的字符串，返回代表它 Unicode 码点的整数。例如 ord('a') 返回整数 97，
     ord('€') （欧元符号）返回 8364 。这是 chr() 的逆函数。
     https://home.unicode.org/
    """

    """
    chr()
    返回 Unicode 码位为整数 i 的字符的字符串格式。
    例如，chr(97) 返回字符串 'a'，chr(8364) 返回字符串 '€'。这是 ord() 的逆函数。
    https://util.unicode.org/UnicodeJsps/character.jsp
    """
    print(hex(ord('回')))
    print(chr(0x56de))

    """
    len(s)
    返回对象的长度（元素个数）。
    实参可以是序列（如 string、bytes、tuple、list 或 range 等）或集合（如 dictionary、set 或 frozen set 等）。
    """
    print(len(range(10)))

    """
    class list([iterable])
    虽然被称为函数，list 实际上是一种可变序列类型，详情请参阅 列表 和 序列类型 --- list, tuple, range。
    """
    print(list(range(10)))

    """
    locals()
    更新并返回表示当前本地符号表的字典。 
    locals() 函数会以字典类型返回当前位置的全部局部变量。
    在函数代码块但不是类代码块中调用 locals() 时将返回自由变量。 请注意在模块层级上，locals() 和 globals() 是同一个字典。
    """
    print(locals())

    """
    globals() 函数会以字典类型返回当前位置的全部全局变量。
    """
    print(globals())

    """
    vars() 函数返回对象object的属性和属性值的字典对象。
    """
    print(vars())

    """
    hasattr()
    getattr()
    setattr()
    delattr()
    """
    x = A(10)
    setattr(x, 'foobar', 123)
    print(getattr(x, 'foobar'))
    print(hasattr(x, 'foobar'))
    delattr(x, 'foobar')

    """
    iter(object[, sentinel])
    Return an iterator object. 
    
    next(iterator[, default])
    Retrieve the next item from the iterator by calling its __next__() method.
    """
    lst = [1, 2, 3]
    for i in iter(lst):
        print(i)

    it = iter([1, 2, 3, 4, 5])
    print(type([1, 2, 3, 4, 5]))
    print(type(it))

    while True:
        try:
            x = it.__next__()
            x = next(it)
            print(x)
        except StopIteration:
            break

    """
    hash(object)
    """
    print(hash(it))
    print(it.__hash__())

    """
    help([object])
    启动内置的帮助系统（此函数主要在交互式中使用）。
    """
    help(())

    """
    id(object)
    返回对象的“标识值”。该值是一个整数，在此对象的生命周期中保证是唯一且恒定的。两个生命期不重叠的对象可能具有相同的 id() 值。
    id(object), 返回该对象的内存地址
    """
    print(id(()))

    """
    Convert a number or string to an xxx
    int()
    bool()
    float()
    """
    print(int('12'))

    """
    https://docs.python.org/zh-cn/3/library/stdtypes.html#typememoryview
    class memoryview(object)
    warming !
    """
    v = memoryview(b'abcefg')
    print("-----------")
    print(v[1], " ", v[-1], " ", v[1:4])
    print("-----------")

    """
    class bytes([source[, encoding[, errors]]])
    返回一个新的“bytes”对象，这是一个不可变序列，包含范围为 0 <= x < 256 的整数。
    """
    print(bytes(v[1:4]))

    """
    class bytearray([source[, encoding[, errors]]])
    返回一个新的 bytes 数组。 bytearray 类是一个可变序列
    另见 二进制序列类型 --- bytes, bytearray, memoryview 和 bytearray 对象。
    """
    print(bytearray(b'runoob'))

    """
    all() 函数用于判断给定的可迭代参数 iterable 中的所有元素是否都为 TRUE，如果是返回 True，否则返回 False。
    元素除了是 0、空、None、False 外都算 True。
    any() 函数用于判断给定的可迭代参数 iterable 是否全部为 False，则返回 False，如果有一个为 True，则返回 True。
    """
    print(all(['a', 'b', 'c', 'd']))  # True
    print(all(['a', 'b', '', 'd']))  # False
    print(all([0, 1, 2, 3]))  # False
    print(any(['a', 'b', '', 'd']))  # 列表list，存在一个为空的元素
    print(any([0, '', False]))  # 列表list,元素全为0,'',false

    """
    @staticmethod¶ 将方法转换为静态方法。
    @classmethod¶ 把一个方法封装成类方法。
    
    静态方法对象
    静态方法对象提供了一种胜过上文所述将函数对象转换为方法对象的方式。 
    静态方法对象是对任意其他对象的包装器，通常用来包装用户自定义的方法对象。 当从类或类实例获取一个静态方法对象时，实际返回的是经过包装的对象，它不会被进一步转换。 静态方法对象也是可调用对象。 静态方法对象可通过内置的 staticmethod() 构造器来创建。

    类方法对象
    类方法对象和静态方法一样是对其他对象的封装，会改变从类或类实例获取该对象的方式。
    类方法对象在此类获取操作中的行为已在上文 "用户定义方法" 一节中描述。类方法对象可通过内置的 classmethod() 构造器来创建。
    https://docs.python.org/zh-cn/3/reference/datamodel.html#types
    https://www.bilibili.com/read/cv902958/
    
    https://blog.csdn.net/alaitian/article/details/82505487
    """


    class cal:
        cal_name = '计算器'

        def __init__(self, x, y):
            self.x = x
            self.y = y

        @property  # 在cal_add函数前加上@property，使得该函数可直接调用，封装起来
        def cal_add(self):
            return self.x + self.y

            # 在cal_info函数前加上@classmethon，则该函数变为类方法，

        @classmethod  # 该函数只能访问到类的数据属性，不能获取实例的数据属性
        def cal_info(cls):  # python自动传入位置参数cls就是类本身
            print('这是一个%s' % cls.cal_name)  # cls.cal_name调用类自己的数据属性

        @staticmethod
        def cal_info_static():
            print('这是一个%s' % cal.cal_name)  # cls.cal_name调用类自己的数据属性


    cal.cal_info()  # >>> '这是一个计算器'
    cal.cal_info_static()  # >>> '这是一个计算器'


    class Fuck:
        sex = 'NTH'  # 这就是非绑定的属性

        @staticmethod
        def sta():
            return Fuck.sex

        @classmethod
        def cla(cls):
            return cls.sex  # @classmethod里面必须要传入一个参数，这个参数代表就是当前的类


    class Fuck_everybody(Fuck):  # 因为Fuck_everybody继承了父类Fuck，所以Fuck_everybody可以调用父类的sta()方法与cla()方法
        pass


    print(Fuck_everybody.sta())
    print(Fuck_everybody.cla())
    # 然后我突然不爽，把Fuck删掉了
    del Fuck
    # 那么，你再试试
    # print(Fuck_everybody.sta())
    print(Fuck_everybody.cla())  # 类方法还是可以照样执行的
