// abstract final default sealed non-sealed

class , interface , @interface , enum , record

The java 4 knights
final or not ?
static or not ?
permissions ?
types [attr method with abstract or not]

class 有 sealed abstract final修饰的特殊class

接口的属性 默认 是public static final 且只能是 public static final
接口的方法 除了 默认 静态 私有 方法 之外 可以写函数体 ，其它的就是public abstract
内部接口默认是static 修饰的

注解是在接口的前提下 限制加强 属性和接口一样
方法差不多限制的只有一种类型 (带return的 public abstract)


枚举类可以实现接口但是无法继承类，枚举类就是一个final类+private构造函数


现在的是有本地的class 也有 内部的类

// 5 4 3 3 1(归于在方法里面去执行)
// 3 field method block code area




java.lang
java.lang.annotation
java.lang.constant
java.lang.module
java.lang.ref
java.lang.reflect