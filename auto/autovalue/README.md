# auto/autovalue

[AutoValue](https://github.com/google/auto/tree/main/value) 是 Google 的注解处理器：
你只写抽象类 + 一个静态工厂方法，编译期生成 `equals` / `hashCode` / `toString` /
构造器 / Builder 的完整实现。

## 跑起来

```bash
./gradlew :auto:autovalue:demo    # 打印生成的类与行为
./gradlew :auto:autovalue:test    # 7 个测试
```

## 这个模块以前为什么跑不起来

两个 bug 叠在一起，导致模块从未成功编译过：

1. **工厂方法被注释掉了** —— AutoValue 靠 `static Xxx create(...)` 这个抽象工厂方法
   确定要生成什么，注释掉之后 `AutoValue_Person` 根本不会生成。
2. **`annotationProcessor` 配错了 artifact** —— 原来写的是
   `annotationProcessor(libs.auto.value.annotations)`，而 `auto-value-annotations` 里
   **只有注解本身，没有处理器**。正确的处理器 artifact 是 `auto-value`。

第 2 点尤其隐蔽：Gradle 不报错，处理器只是「什么都没做」，然后你在代码里
`new AutoValue_Person()` 就找不到符号。

```kotlin
compileOnly(libs.auto.value.annotations)   // @AutoValue 注解
annotationProcessor(libs.auto.value)        // 真正干活的处理器
```

## 两个例子

**`Person`** —— 最小可用形态，只有工厂方法：

```java
@AutoValue
public abstract class Person {
    public abstract String name();
    public abstract int age();

    public static Person create(String name, int age) {
        return new AutoValue_Person(name, age);
    }
}
```

**`Money`** —— 带 Builder + 校验。AutoValue 生成的 `build()` 可以覆写，在里面做不变量检查：

```java
@AutoValue
public abstract class Money {
    public abstract long amountInCents();

    public static Builder builder() { return new AutoValue_Money.Builder(); }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder amountInCents(long cents);
        public abstract Money build();

        // 校验放在 build()：所有 setter 调用完之后才检查，不会出现「半成品」状态
    }
}
```

负数金额在 `build()` 里被拒绝，抛 `IllegalStateException`——测试断言了这一点。

## 生成的代码在哪

```bash
find auto/autovalue/build/generated -name "AutoValue_*.java"
```

值得读一遍：生成的是普通 Java 源码，没有黑魔法。

## 测试

`AutoValueBehaviorTest` 7 个用例，全部断言**可观测行为**：
工厂方法返回值、`equals`/`hashCode` 契约、`toString` 内容、Builder 校验拒绝非法值、
Builder 与工厂方法结果相等、Builder 可复用不互相污染、demo 输出。

不测「生成器内部怎么拼字符串」——那是实现细节，AutoValue 升级就会变。
