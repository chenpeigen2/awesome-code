package anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 用于修饰 类、接口 ...
@Target({ElementType.TYPE})
// 在编译器期可以见，但是不会被编译到 .class 文件里面
@Retention(RetentionPolicy.CLASS)
public @interface PrintHello {
}
