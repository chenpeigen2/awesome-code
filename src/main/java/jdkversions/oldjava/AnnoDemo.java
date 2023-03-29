package jdkversions.oldjava;


//@Retention - 标识这个注解怎么保存，是只在代码中，还是编入class文件中，或者是在运行时可以通过反射访问。
//@Documented - 标记这些注解是否包含在用户文档中。
//@Target - 标记这个注解应该是哪种 Java 成员。
//@Inherited - 标记这个注解是继承于哪个注解类(默认 注解并没有继承于任何子类)
public @interface AnnoDemo {
    // only public static final
    int a = 123;

    // only public abstract method with returns and can with default values
    String aa();
}
