package org.peter.asm.basics;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HexFormat;

/**
 * 把字节码「翻译回」人类可读的形式。
 * <p>
 * 两个视角：
 * <ul>
 *   <li>{@link #disassemble} —— 类似 {@code javap -c}，看指令与常量池引用；</li>
 *   <li>{@link #hexDump} —— 看原始字节，验证魔数与结构。</li>
 * </ul>
 */
public final class Disassembler {

    private Disassembler() {
    }

    /**
     * 反汇编成 ASM Textifier 的文本形式。
     *
     * @param classBytes class 文件字节
     */
    public static String disassemble(byte[] classBytes) {
        StringWriter out = new StringWriter();
        // TraceClassVisitor 把访问者调用重新打印成文本；visitor 传 null 表示只打印不转发
        new ClassReader(classBytes).accept(
                new TraceClassVisitor(null, new Textifier(), new PrintWriter(out)),
                ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return out.toString();
    }

    /**
     * 十六进制 dump，每行 {@code bytesPerLine} 个字节，大写、单空格分隔。
     *
     * @param classBytes   class 文件字节
     * @param bytesPerLine 每行字节数，必须为正
     */
    public static String hexDump(byte[] classBytes, int bytesPerLine) {
        if (bytesPerLine <= 0) {
            throw new IllegalArgumentException("bytesPerLine must be positive: " + bytesPerLine);
        }
        HexFormat hex = HexFormat.of().withUpperCase().withDelimiter(" ");
        StringBuilder out = new StringBuilder();
        for (int offset = 0; offset < classBytes.length; offset += bytesPerLine) {
            int end = Math.min(offset + bytesPerLine, classBytes.length);
            if (offset > 0) {
                out.append('\n');
            }
            out.append(hex.formatHex(classBytes, offset, end));
        }
        return out.toString();
    }
}
