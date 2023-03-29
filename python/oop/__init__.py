# 类把数据与功能绑定在一起。

# https://docs.python.org/zh-cn/3/tutorial/classes.html

# 作用域和命名空间示例
def scope_test():
    def do_local():
        spam = "local spam"

    def do_nonlocal():
        nonlocal spam
        spam = "nonlocal spam"

    def do_global():
        global spam
        spam = "global spam"

    spam = "test spam"
    do_local()
    print("After local assignment:", spam)
    do_nonlocal()
    print("After nonlocal assignment:", spam)
    do_global()
    print("After global assignment:", spam)


scope_test()
print("In global scope:", spam)


# 类(Class): 用来描述具有相同的属性和方法的对象的集合。它定义了该集合中每个对象所共有的属性和方法。对象是类的实例。
# 数据成员：类变量或者实例变量, 用于处理类及其实例对象的相关的数据。

# 类变量：类变量在整个实例化的对象中是公用的。类变量定义在类中且在函数体之外。类变量通常不作为实例变量使用。
# 实例变量：在类的声明中，属性是用变量来表示的。这种变量就称为实例变量，是在类声明的内部但是在类的其他成员方法之外声明的。

# 方法：类中定义的函数。

# 方法重写：如果从父类继承的方法不能满足子类的需求，可以对其进行改写，这个过程叫方法的覆盖（override），也称为方法的重写。
# 局部变量：定义在方法中的变量，只作用于当前实例的类。
# 继承：即一个派生类（derived class）继承基类（base class）的字段和方法。继承也允许把一个派生类的对象作为一个基类对象对待。
# ->例如，有这样一个设计：一个Dog类型的对象派生自Animal类，这是模拟"是一个（is-a）"关系（例图，Dog是一个Animal）。

# 实例化：创建一个类的实例，类的具体对象。
# 对象：通过类定义的数据结构实例。对象包括两个数据成员（类变量和实例变量）和方法。

# -------------------------- Warming --------------------------------------
# 特殊方法，一般是系统定义名字 ，类似 __init__() 之类的。
# __private_attrs：两个下划线开头，声明该属性为私有
# __private_method：两个下划线开头，声明该方法为私有方法，不能在类的外部调用。

class Employee:
    # 类变量：类变量在整个实例化的对象中是公用的。类变量定义在类中且在函数体之外。类变量通常不作为实例变量使用。 static
    empCount = 0  # class variable shared by all instances

    def __init__(self, name, salary):
        self.name = name
        self.salary = salary
        Employee.empCount += 1

    def displayCount(self):
        print("Total Employee %d" % Employee.empCount)

    def displayEmployee(self):
        print("Name : ", self.name, ", Salary: ", self.salary)


class Parent:
    parentAttr = 100

    def __init__(self):
        print("调用父类构造函数")

    def parentMethod(self):
        print("调用父类方法")

    def setAttr(self, attr):
        Parent.parentAttr = attr

    def getAttr(self):
        print("父类属性 :", Parent.parentAttr)

    def myMethod(self):
        print('调用父类方法')


class Child(Parent):  # 定义子类
    def __init__(self):
        super().__init__()
        print("调用子类构造方法")

    def childMethod(self):
        print('调用子类方法')

    def myMethod(self):
        print('调用子类方法')


# Python同样支持运算符重载，实例如下：
class Vector:
    def __init__(self, a, b):
        self.a = a
        self.b = b

    def __str__(self):
        return 'Vector (%d, %d)' % (self.a, self.b)

    def __add__(self, other):
        return Vector(self.a + other.a, self.b + other.b)


if __name__ == '__main__':
    "创建 Employee 类的第一个对象"
    emp1 = Employee("Zara", 2000)
    "创建 Employee 类的第二个对象"
    emp2 = Employee("Manni", 5000)

    emp1.displayEmployee()
    emp2.displayEmployee()
    print("Total Employee %d" % Employee.empCount)

    # 你可以添加，删除，修改类的属性，如下所示：
    emp1.age = 7  # 添加一个 'age' 属性
    emp1.age = 8  # 修改 'age' 属性
    del emp1.age  # 删除 'age' 属性

    # 你也可以使用以下函数的方式来访问属性：
    setattr(emp1, 'age', 8)  # 添加属性 'age' 值为 8
    hasattr(emp1, 'age')  # 如果存在 'age' 属性返回 True。
    getattr(emp1, 'age')  # 返回 'age' 属性的值
    delattr(emp1, 'age')  # 删除属性 'age'

    # __dict__: 类的属性（包含一个字典，由类的数据属性组成）
    # __doc__: 类的文档字符串
    # __name__: 类名
    # __module__: 类定义所在的模块（类的全名是
    # '__main__.className'，如果类位于一个导入模块mymod中，那么className.__module__
    # 等于
    # mymod）
    # __bases__: 类的所有父类构成元素（包含了一个由所有父类组成的元组）
    print("Employee.__doc__:", Employee.__doc__)
    print("Employee.__name__:", Employee.__name__)
    print("Employee.__module__:", Employee.__module__)
    print("Employee.__bases__:", Employee.__bases__)
    print("Employee.__dict__:", Employee.__dict__)

    c = Child()  # 实例化子类
    c.childMethod()  # 调用子类的方法
    c.parentMethod()  # 调用父类方法
    c.setAttr(200)  # 再次调用父类的方法 - 设置属性值
    c.getAttr()  # 再次调用父类的方法 - 获取属性值
    c.myMethod()  # 子类调用重写方法

    v1 = Vector(2, 10)
    v2 = Vector(5, -2)
    print(v1 + v2)

    sum(i * i for i in range(10))
    # for i in range(10)
    value = (i * i for i in range(10))
    print(sum(value))
