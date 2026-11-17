package org.peter.gson;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Gson 核心行为的可观测断言。
 * <p>
 * 这些类原来都在默认包里，既不能被 import，也没法从测试里引用 ——
 * 迁到 {@code org.peter.gson} 之后才第一次变得可测。
 */
class GsonBehaviorTest {

    private final Gson gson = new Gson();

    @Test
    void serializesPojoToJson() {
        Person person = new Person("张三", 30, "13800000000", "z@example.com");

        String json = gson.toJson(person);

        assertTrue(json.contains("\"name\":\"张三\""), json);
        assertTrue(json.contains("\"age\":30"), json);
    }

    @Test
    void deserializesJsonBackToPojo() {
        Person person = gson.fromJson(
                "{\"name\":\"李四\",\"age\":25,\"email\":\"l@example.com\",\"phone\":\"139\"}",
                Person.class);

        assertEquals("李四", person.getName());
        assertEquals(25, person.getAge());
    }

    @Test
    void roundTripPreservesValues() {
        Person original = new Person("王五", 40, "137", "w@example.com");

        Person restored = gson.fromJson(gson.toJson(original), Person.class);

        assertEquals(original.getName(), restored.getName());
        assertEquals(original.getAge(), restored.getAge());
        assertEquals(original.getEmail(), restored.getEmail());
    }

    @Test
    void instanceCreatorIsActuallyUsed() {
        // 关键断言：不只验证「解析出了对象」，还要验证 InstanceCreator 真的被调用了。
        // 原来的示例把 registerTypeAdapter 注释掉了，对象照样能解析出来，
        // 但完全没走 InstanceCreator —— 只看结果根本发现不了。
        InstanceCreatorExample example = new InstanceCreatorExample();

        InstanceCreatorExample.User user = example.parse(
                "{\"name\":\"张三\",\"age\":25,\"email\":\"z@example.com\"}");

        assertEquals("张三", user.getName());
        assertEquals(25, user.getAge());
        assertTrue(example.creatorInvocations() > 0,
                "InstanceCreator 未被调用，说明注册没生效");
    }

    @Test
    void withoutInstanceCreatorNoArgConstructorIsStillNotUsed() {
        // 对照实验：不注册 InstanceCreator 时，Gson 也能反序列化（靠 Unsafe 分配），
        // 说明「能解析出来」并不能证明 InstanceCreator 生效 —— 必须数调用次数。
        InstanceCreatorExample.User direct = new Gson().fromJson(
                "{\"name\":\"A\",\"age\":1,\"email\":\"a@example.com\"}",
                InstanceCreatorExample.User.class);

        assertEquals("A", direct.getName());
    }

    @Test
    void unknownFieldsAreSilentlyIgnoredByDefault() {
        // Gson 默认对 JSON 里多出来的字段静默忽略。这既是方便也是坑：
        // 字段名拼错时不会报错，只会得到一个 null。
        // （注意 Strictness.STRICT 只管 JSON 语法严格性，不管未知字段，
        //   别指望它能帮你抓拼写错误。）
        Person person = gson.fromJson("{\"name\":\"A\",\"nmae\":\"typo\"}", Person.class);

        assertEquals("A", person.getName());
        assertEquals(null, person.getEmail());
    }

    @Test
    void strictnessRejectsMalformedJsonSyntax() {
        // Strictness 真正管的是语法：未加引号的字符串在 STRICT 下必须报错
        var strict = new com.google.gson.GsonBuilder()
                .setStrictness(com.google.gson.Strictness.STRICT)
                .create();

        org.junit.jupiter.api.Assertions.assertThrows(
                com.google.gson.JsonSyntaxException.class,
                () -> strict.fromJson("{name:A}", Person.class));
    }

    @Test
    void eachDeserializationCreatesANewInstance() {
        Person first = gson.fromJson("{\"name\":\"A\"}", Person.class);
        Person second = gson.fromJson("{\"name\":\"B\"}", Person.class);

        assertNotSame(first, second);
        assertEquals("A", first.getName());
        assertEquals("B", second.getName());
    }

    @Test
    void demoReportsInstanceCreatorUsage() {
        String report = new InstanceCreatorExample().run();

        assertTrue(report.contains("creator invoked: 1 time(s)"), report);
        assertTrue(report.contains("张三"), report);
    }
}
