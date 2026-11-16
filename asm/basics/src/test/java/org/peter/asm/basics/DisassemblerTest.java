package org.peter.asm.basics;

import org.junit.jupiter.api.Test;
import org.peter.asm.basics.fixture.ClassBytes;
import org.peter.asm.basics.fixture.Greeter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 反汇编 / 十六进制 dump 的规格。
 * <p>
 * 预期值来自 JVM 规范：class 文件必须以魔数 {@code CAFEBABE} 开头。
 */
class DisassemblerTest {

    @Test
    void disassemblesToReadableText() {
        String text = Disassembler.disassemble(ClassBytes.of(Greeter.class));

        assertTrue(text.contains("org/peter/asm/basics/fixture/Greeter"), text);
        assertTrue(text.contains("greet"), text);
        assertTrue(text.contains("Ljava/lang/String;"), text);
        // Textifier 会渲染出方法体指令
        assertTrue(text.contains("INVOKEDYNAMIC") || text.contains("INVOKEVIRTUAL"), text);
    }

    @Test
    void hexDumpStartsWithTheClassFileMagic() {
        String hex = Disassembler.hexDump(ClassBytes.of(Greeter.class), 16);

        // JVM 规范：class 文件以魔数 0xCAFEBABE 开头
        assertTrue(hex.startsWith("CA FE BA BE "), hex);
        assertFalse(hex.contains("  "), "每个字节之间只有一个空格: " + hex);
    }

    @Test
    void hexDumpHonoursBytesPerLine() {
        String[] lines = Disassembler.hexDump(ClassBytes.of(Greeter.class), 8).split("\n");

        assertTrue(lines[0].startsWith("CA FE BA BE 00 00 00 "), lines[0]);
        // 每行 8 个字节 → 8 个 token
        assertEquals(8, lines[0].split(" ").length, lines[0]);
        assertEquals(8, lines[1].split(" ").length, lines[1]);
    }
}
