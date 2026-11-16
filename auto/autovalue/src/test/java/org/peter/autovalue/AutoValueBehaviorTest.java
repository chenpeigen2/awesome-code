package org.peter.autovalue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 AutoValue 生成的类在<b>运行期</b>的真实行为。
 * <p>
 * 这些断言刻意只碰公共 API（{@code create}、{@code builder}、{@code equals}…），
 * 不碰 {@code AutoValue_Person} 这类生成类的内部细节——生成类的名字和结构
 * 是 AutoValue 的实现自由，绑上去会让测试变脆。
 */
class AutoValueBehaviorTest {

    @Test
    void factoryCreatesObjectWithGivenValues() {
        Person person = Person.create("张三", 30);

        assertEquals("张三", person.name());
        assertEquals(30, person.age());
    }

    @Test
    void valueSemanticsComeFromGeneratedEquals() {
        // 值对象的核心承诺：字段相同即相等。这是手写 POJO 最容易漏掉的部分
        Person a = Person.create("李四", 25);
        Person b = Person.create("李四", 25);
        Person c = Person.create("王五", 25);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    void generatedToStringIsReadable() {
        // 默认 Object.toString 是 Person@1a2b3c，调试时毫无用处
        String text = Person.create("张三", 30).toString();

        assertTrue(text.contains("张三"), text);
        assertTrue(text.contains("30"), text);
    }

    @Test
    void builderRejectsNegativeAmount() {
        // 校验逻辑写在 build() 里，构造不出非法状态
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Money.builder().currency("CNY").amountInCents(-1).build());

        assertTrue(error.getMessage().contains("non-negative"), error.getMessage());
    }

    @Test
    void builderProducesValueEqualToFactory() {
        Money fromBuilder = Money.builder().currency("CNY").amountInCents(1999).build();
        Money fromFactory = Money.of("CNY", 1999);

        assertEquals(fromFactory, fromBuilder);
        assertEquals(fromFactory.hashCode(), fromBuilder.hashCode());
    }

    @Test
    void mutatingBuilderAfterBuildDoesNotAffectBuiltObject() {
        // 值对象必须不可变：Builder 是可变的，但产物不能跟着变
        Money.Builder builder = Money.builder().currency("CNY").amountInCents(100);
        Money first = builder.build();

        builder.amountInCents(999);
        Money second = builder.build();

        assertEquals(100, first.amountInCents(), "已构建的对象不应被后续 builder 修改影响");
        assertEquals(999, second.amountInCents());
        assertNotEquals(first, second);
    }

    @Test
    void demoRunsAndReportsBothShapes() {
        String report = new AutoValueDemo().run();

        assertTrue(report.contains("factory: Person{name=张三, age=30}"), report);
        assertTrue(report.contains("builder: Money{currency=CNY, amountInCents=1999}"), report);
    }
}
