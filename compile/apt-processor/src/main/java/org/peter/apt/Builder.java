package org.peter.apt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个 setter，让 {@link BuilderProcessor} 为它所在的类生成流式 Builder。
 * <p>
 * 只能标在<b>方法</b>上：注解处理器是按元素类型分发的，标在字段上会被显式报错
 * （见 {@code BuilderProcessor} 里的校验），而不是被静默忽略。
 *
 * <p>用法：
 * <pre>{@code
 * public class Person {
 *     private String name;
 *
 *     @Builder
 *     public void setName(String name) { this.name = name; }
 * }
 * }</pre>
 * 编译后会多出一个 {@code PersonBuilder}：
 * <pre>{@code
 * Person p = new PersonBuilder().setName("doge").build();
 * }</pre>
 *
 * <p>{@link RetentionPolicy#SOURCE} 是刻意的：这个注解只在编译期有意义，
 * 没必要写进 .class 文件增加运行期负担。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Builder {
}
