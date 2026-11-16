package org.peter.apt.demo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证注解处理器在「真实 Gradle 构建」里生效。
 * <p>
 * 与 {@code BuilderProcessorTest} 的区别：那个测试用 {@code javax.tools} 手动跑处理器，
 * 验证处理器本身的逻辑；这里验证的是 <b>Gradle 配置接对了</b>——
 * {@code annotationProcessor(project(":compile:apt-processor"))} 真的挂上了，
 * 所以 {@code PersonBuilder} 能被生成并且能被源码引用。
 */
class AptDemoTest {

    @Test
    void builderGeneratedByAnnotationProcessorIsAvailable() {
        // 能写出这行代码就说明处理器生效了：PersonBuilder 在源码里不存在
        Person person = new PersonBuilder().setName("李四").setAge(25).build();

        assertNotNull(person);
        assertEquals("李四", person.getName());
        assertEquals(25, person.getAge());
    }

    @Test
    void builderSupportsFluentChaining() {
        Person person = new PersonBuilder()
                .setName("王五")
                .setAge(40)
                .setEmail("wangwu@example.com")
                .build();

        assertEquals("Person{name=王五, age=40, email=wangwu@example.com}", person.toString());
    }

    @Test
    void demoReportsTheBuiltObject() {
        String report = new AptDemo().run();

        assertTrue(report.contains("built: Person{name=张三, age=30, email=zhangsan@example.com}"),
                report);
        assertTrue(report.contains("chaining works: true"), report);
    }
}
