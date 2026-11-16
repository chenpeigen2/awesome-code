package org.peter.asm.tree;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;
import org.peter.asm.tree.fixture.Bytecode;
import org.peter.asm.tree.fixture.Person;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tree API 的基本规格：把字节码读成可编辑的树，再写回去结构不变。
 */
class ClassTreeTest {

    @Test
    void readsClassIntoEditableTree() {
        ClassNode node = ClassTree.read(Bytecode.of(Person.class));

        assertEquals("org/peter/asm/tree/fixture/Person", node.name);
        assertEquals("java/lang/Object", node.superName);
        assertEquals(5, node.fields.size());
        assertEquals(List.of("name", "age", "active", "score", "id"),
                node.fields.stream().map(f -> f.name).toList());
    }

    @Test
    void findsFieldsAndMethodsByName() {
        ClassNode node = ClassTree.read(Bytecode.of(Person.class));

        assertNotNull(ClassTree.field(node, "age"));
        assertEquals("I", ClassTree.field(node, "age").desc);
        assertEquals("D", ClassTree.field(node, "score").desc);
        assertEquals("J", ClassTree.field(node, "id").desc);
        assertEquals("Z", ClassTree.field(node, "active").desc);

        assertNotNull(ClassTree.method(node, "toString", "()Ljava/lang/String;"));
        assertNotNull(ClassTree.method(node, "<init>", "(Ljava/lang/String;I)V"));
    }

    @Test
    void roundTripPreservesStructure() {
        ClassNode original = ClassTree.read(Bytecode.of(Person.class));
        byte[] rewritten = ClassTree.write(original);
        ClassNode reread = ClassTree.read(rewritten);

        assertEquals(original.name, reread.name);
        assertEquals(original.superName, reread.superName);
        assertEquals(original.fields.size(), reread.fields.size());
        assertEquals(original.methods.size(), reread.methods.size());
        assertEquals(
                original.methods.stream().map(m -> m.name + m.desc).toList(),
                reread.methods.stream().map(m -> m.name + m.desc).toList());
    }

    @Test
    void roundTrippedClassStillBehavesTheSame() throws Exception {
        byte[] rewritten = ClassTree.write(ClassTree.read(Bytecode.of(Person.class)));

        Object person = Bytecode.newInstance(rewritten, "org.peter.asm.tree.fixture.Person");

        assertEquals("Person{name=null, age=0}", person.toString());
    }

    @Test
    void writeReturnsDistinctArray() {
        ClassNode node = ClassTree.read(Bytecode.of(Person.class));
        byte[] first = ClassTree.write(node);
        byte[] second = ClassTree.write(node);

        assertSame(first.getClass(), second.getClass());
        assertEquals(first.length, second.length);
    }
}
