# Python 内置函数速查表

> Python 3.12 共有 71 个内置函数

## 函数列表（按字母排序）

| 函数 | 说明 |
|------|------|
| `abs(x)` | 返回绝对值 |
| `aiter(async_iterable)` | 返回异步迭代器 |
| `all(iterable)` | 所有元素都为 True 则返回 True |
| `anext(async_iterator)` | 返回异步迭代器的下一个元素 |
| `any(iterable)` | 任一元素为 True 则返回 True |
| `ascii(object)` | 返回 ASCII 表示 |
| `bin(x)` | 转换为二进制字符串 |
| `bool(x)` | 转换为布尔值 |
| `breakpoint()` | 进入调试器 |
| `bytearray()` | 创建可变字节数组 |
| `bytes()` | 创建不可变字节对象 |
| `callable(object)` | 检查是否可调用 |
| `chr(i)` | Unicode 码点转字符 |
| `classmethod()` | 类方法装饰器 |
| `compile()` | 编译源代码 |
| `complex()` | 创建复数 |
| `delattr()` | 删除属性 |
| `dict()` | 创建字典 |
| `dir()` | 列出属性 |
| `divmod(a, b)` | 返回 (商, 余数) |
| `enumerate()` | 带索引的枚举 |
| `filter()` | 过滤序列 |
| `float()` | 转换为浮点数 |
| `format()` | 格式化字符串 |
| `frozenset()` | 创建不可变集合 |
| `getattr()` | 获取属性 |
| `globals()` | 返回全局变量字典 |
| `hasattr()` | 检查属性是否存在 |
| `hash()` | 返回哈希值 |
| `help()` | 显示帮助 |
| `hex()` | 转换为十六进制 |
| `id()` | 返回对象标识 |
| `input()` | 获取用户输入 |
| `int()` | 转换为整数 |
| `isinstance()` | 检查实例类型 |
| `issubclass()` | 检查子类关系 |
| `iter()` | 返回迭代器 |
| `len()` | 返回长度 |
| `list()` | 创建列表 |
| `locals()` | 返回局部变量字典 |
| `map()` | 映射函数 |
| `max()` | 返回最大值 |
| `memoryview()` | 创建内存视图 |
| `min()` | 返回最小值 |
| `next()` | 返回下一个元素 |
| `object()` | 创建基础对象 |
| `oct()` | 转换为八进制 |
| `open()` | 打开文件 |
| `ord()` | 字符转 Unicode 码点 |
| `pow()` | 幂运算 |
| `print()` | 打印输出 |
| `property()` | 属性装饰器 |
| `range()` | 创建范围序列 |
| `repr()` | 返回字符串表示 |
| `reversed()` | 反向迭代器 |
| `round()` | 四舍五入 |
| `set()` | 创建集合 |
| `setattr()` | 设置属性 |
| `slice()` | 创建切片对象 |
| `sorted()` | 排序 |
| `staticmethod()` | 静态方法装饰器 |
| `str()` | 转换为字符串 |
| `sum()` | 求和 |
| `super()` | 调用父类方法 |
| `tuple()` | 创建元组 |
| `type()` | 获取或创建类型 |
| `vars()` | 返回 __dict__ |
| `zip()` | 并行迭代 |
| `__import__()` | 动态导入模块 |

## 按功能分类

### 类型转换
int, float, bool, str, list, tuple, dict, set, frozenset, complex, bytes, bytearray

### 数学运算
abs, divmod, pow, round, max, min, sum

### 进制转换
bin, oct, hex, ord, chr

### 序列操作
len, range, slice, sorted, reversed, enumerate, zip

### 迭代器
iter, next, aiter, anext, map, filter

### 对象检查
type, isinstance, issubclass, callable, hasattr, getattr, setattr, delattr, dir, vars, id, hash

### 作用域
globals, locals

### 代码执行
compile - 注意：代码执行函数需谨慎使用

### 输入输出
input, print, open, repr, ascii, format

### 面向对象
super, property, staticmethod, classmethod, object

### 其他
help, breakpoint, memoryview, __import__

## 内置常量

- `False` - 布尔假值
- `True` - 布尔真值
- `None` - 空值
- `NotImplemented` - 未实现标志
- `Ellipsis` - 省略号 ...
- `__debug__` - 调试模式标志

## 示例代码位置

```
builtin_funcs/
├── type_conversion.py     # 类型转换函数
├── sequence_iter.py       # 序列和迭代器函数
├── object_introspection.py # 对象检查函数
├── math_numeric.py        # 数学函数
├── code_execution.py      # 代码执行函数
├── advanced_async.py      # 高级和异步函数
└── basis.py               # 原有示例
```

## 参考链接

- [Python 官方文档 - 内置函数](https://docs.python.org/zh-cn/3/library/functions.html)
- [Python 官方文档 - 内置常量](https://docs.python.org/zh-cn/3/library/constants.html)
