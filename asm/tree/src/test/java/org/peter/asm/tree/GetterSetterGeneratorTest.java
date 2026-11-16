package org.peter.asm.tree;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;
import org.peter.asm.tree.fixture.Bytecode;
import org.peter.asm.tree.fixture.Person;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 给 POJO 自动加 getter/setter —— Tree API 最典型的用法。
 * <p>
 * 断言落在「改造后的类能通过反射被正常使用」这个行为上，
 * 而不是检查指令序列长什么样。
 */
class GetterSetterGeneratorTest {

    private final byte[] enhanced = GetterSetterGenerator.enhance(Bytecode.of(Person.class));
    private final Class<?> type = Bytecode.load(enhanced, "org.peter.asm.tree.fixture.Person");

    @Test
    void generatesGetterForEveryNonStaticField() throws Exception {
        List<String> getters = Arrays.stream(type.getDeclaredMethods())
                .filter(m -> m.getParameterCount() == 0 && m.getName().startsWith("get"))
                .map(Method::getName)
                .sorted()
                .toList();

        assertEquals(
                List.of("getActive", "getAge", "getId", "getName", "getScore"),
                getters);
    }

    @Test
    void generatesSetterForEveryNonStaticField() throws Exception {
        List<String> setters = Arrays.stream(type.getDeclaredMethods())
                .filter(m -> m.getParameterCount() == 1 && m.getName().startsWith("set"))
                .map(Method::getName)
                .sorted()
                .toList();

        assertEquals(
                List.of("setActive", "setAge", "setId", "setName", "setScore"),
                setters);
    }

    @Test
    void setterThenGetterRoundTripsEveryType() throws Exception {
        Object person = Bytecode.newInstance(enhanced, "org.peter.asm.tree.fixture.Person");

        Bytecode.invoke(person, "setName", "Ada");
        Bytecode.invoke(person, "setAge", 36);
        Bytecode.invoke(person, "setActive", true);
        Bytecode.invoke(person, "setScore", 9.5d);
        Bytecode.invoke(person, "setId", 42L);

        assertEquals("Ada", Bytecode.invoke(person, "getName"));
        assertEquals(36, Bytecode.invoke(person, "getAge"));
        assertEquals(true, Bytecode.invoke(person, "getActive"));
        assertEquals(9.5d, Bytecode.invoke(person, "getScore"));
        assertEquals(42L, Bytecode.invoke(person, "getId"));
    }

    @Test
    void getterAndSetterHaveCorrectReturnAndParameterTypes() throws Exception {
        assertEquals(String.class, type.getDeclaredMethod("getName").getReturnType());
        assertEquals(int.class, type.getDeclaredMethod("getAge").getReturnType());
        assertEquals(boolean.class, type.getDeclaredMethod("getActive").getReturnType());
        assertEquals(double.class, type.getDeclaredMethod("getScore").getReturnType());
        assertEquals(long.class, type.getDeclaredMethod("getId").getReturnType());

        assertEquals(void.class, type.getDeclaredMethod("setAge", int.class).getReturnType());
    }

    @Test
    void gettersAndSettersArePublic() throws Exception {
        assertTrue(java.lang.reflect.Modifier.isPublic(
                type.getDeclaredMethod("getName").getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(
                type.getDeclaredMethod("setName", String.class).getModifiers()));
    }

    @Test
    void doesNotDuplicateExistingAccessors() {
        byte[] once = GetterSetterGenerator.enhance(Bytecode.of(Person.class));
        byte[] twice = GetterSetterGenerator.enhance(once);

        ClassNode node = ClassTree.read(twice);
        long getNameCount = node.methods.stream()
                .filter(m -> m.name.equals("getName") && m.desc.equals("()Ljava/lang/String;"))
                .count();

        assertEquals(1, getNameCount, "重复 enhance 不应叠加方法");
    }

    @Test
    void skipsStaticFields() {
        ClassNode node = ClassTree.read(Bytecode.of(Person.class));
        ClassTree.addField(node, org.objectweb.asm.Opcodes.ACC_PRIVATE
                | org.objectweb.asm.Opcodes.ACC_STATIC, "shared", "I");

        byte[] enhancedWithStatic = GetterSetterGenerator.enhance(ClassTree.write(node));
        ClassNode reread = ClassTree.read(enhancedWithStatic);

        assertFalse(reread.methods.stream().anyMatch(m -> m.name.equals("getShared")));
    }

    @Test
    void enhancedClassStillKeepsOriginalMembers() {
        ClassNode node = ClassTree.read(enhanced);

        assertEquals(5, node.fields.size());
        assertTrue(node.methods.stream().anyMatch(m -> m.name.equals("toString")));
        assertTrue(node.methods.stream()
                .anyMatch(m -> m.name.equals("<init>") && m.desc.equals("(Ljava/lang/String;I)V")));
    }
}
