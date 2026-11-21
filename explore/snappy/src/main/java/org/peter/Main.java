package org.peter;

import org.xerial.snappy.BitShuffle;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Snappy-java 演示：块格式 / 流格式 / 文件压缩 / BitShuffle
 */
public class Main {

    public static void main(String[] args) throws IOException {
        demoBasicString();
        demoRepetitiveVsRandom();
        demoBlockVsStream();
        demoFileCompression();
        demoBitShuffle();
    }

    /** 基本字符串压缩：看压缩率 */
    static void demoBasicString() {
        System.out.println("=== 1. 基本字符串压缩（块格式） ===");
        String input = "Hello snappy-java! Snappy-java is a JNI-based wrapper of "
                + "Snappy, a fast compresser/decompresser.";
        byte[] compressed = SnappyUtil.compressString(input);
        String result = SnappyUtil.unCompressToString(compressed);

        System.out.printf("原始: %d 字节%n", input.getBytes(StandardCharsets.UTF_8).length);
        System.out.printf("压缩: %d 字节，压缩比 %.2f%n", compressed.length,
                SnappyUtil.compressionRatio(compressed, input.getBytes(StandardCharsets.UTF_8)));
        System.out.println("还原: " + result);
        System.out.println();
    }

    /** 重复文本 vs 随机数据：可压缩性差异 + 耗时 */
    static void demoRepetitiveVsRandom() {
        System.out.println("=== 2. 重复文本 vs 随机数据（10 MiB） ===");
        byte[] repetitive = "the quick brown fox jumps over the lazy dog. ".repeat(10 * 1024 * 1024 / 45)
                .getBytes(StandardCharsets.UTF_8);
        byte[] random = new byte[repetitive.length];
        ThreadLocalRandom.current().nextBytes(random);

        benchmark("重复文本", repetitive);
        benchmark("随机数据", random);
        System.out.println();
    }

    private static void benchmark(String label, byte[] data) {
        long start = System.nanoTime();
        byte[] compressed = SnappyUtil.compress(data);
        long compressMs = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        byte[] restored = SnappyUtil.unCompress(compressed);
        long uncompressMs = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("%s: %d -> %d 字节（压缩比 %.2f），压缩 %d ms，解压 %d ms，还原一致=%b%n",
                label, data.length, compressed.length,
                SnappyUtil.compressionRatio(compressed, data),
                compressMs, uncompressMs, Arrays.equals(data, restored));
    }

    /** 块格式 vs 流格式：流格式带文件头，可独立解压 */
    static void demoBlockVsStream() {
        System.out.println("=== 3. 块格式 vs 流格式 ===");
        byte[] data = "stream format has a header, block format doesn't.".repeat(100)
                .getBytes(StandardCharsets.UTF_8);

        byte[] block = SnappyUtil.compress(data);
        byte[] stream = SnappyUtil.compressWithHeader(data);

        System.out.printf("原始 %d 字节 | 块格式 %d 字节 | 流格式 %d 字节%n",
                data.length, block.length, stream.length);
        System.out.println("块格式还原一致=" + Arrays.equals(data, SnappyUtil.unCompress(block)));
        System.out.println("流格式还原一致=" + Arrays.equals(data, SnappyUtil.unCompressWithHeader(stream)));
        System.out.println();
    }

    /** 文件压缩：整文件流式压缩/解压到临时目录 */
    static void demoFileCompression() throws IOException {
        System.out.println("=== 4. 文件压缩 ===");
        Path dir = Files.createTempDirectory("snappy-demo");
        Path src = dir.resolve("notes.txt");
        Path compressed = dir.resolve("notes.txt.snappy");
        Path restored = dir.resolve("notes.restored.txt");

        Files.writeString(src, "这是一段会被压缩的日志内容。".repeat(10_000), StandardCharsets.UTF_8);

        boolean ok1 = SnappyUtil.compressFile(src, compressed);
        boolean ok2 = SnappyUtil.unCompressFile(compressed, restored);
        System.out.printf("压缩 %s -> %s (%d -> %d 字节): %b%n",
                src.getFileName(), compressed.getFileName(),
                Files.size(src), Files.size(compressed), ok1);
        System.out.printf("解压还原一致=%b，内容一致=%b%n",
                ok2, Files.readString(restored, StandardCharsets.UTF_8)
                        .equals(Files.readString(src, StandardCharsets.UTF_8)));
        System.out.println("临时目录: " + dir);
        System.out.println();
    }

    /** BitShuffle：压缩前先按位打散整数数组，提升整数序列压缩率 */
    static void demoBitShuffle() throws IOException {
        System.out.println("=== 5. BitShuffle（int 数组先位打散再压缩） ===");
        // 大量取值范围小的整数：位打散后高位字节大量重复，压缩率明显提升
        int[] data = new int[1000];
        Arrays.setAll(data, i -> i % 100);

        byte[] raw = toByteArray(data);
        byte[] compressedRaw = SnappyUtil.compress(raw);

        byte[] shuffled = BitShuffle.shuffle(data);
        byte[] compressedShuffled = SnappyUtil.compress(shuffled);

        byte[] uncompressed = SnappyUtil.unCompress(compressedShuffled);
        int[] result = BitShuffle.unshuffleIntArray(uncompressed);

        System.out.printf("int 数组: %d 个元素（取值 0-99）%n", data.length);
        System.out.printf("直接压缩: %d 字节 | 位打散后压缩: %d 字节%n",
                compressedRaw.length, compressedShuffled.length);
        System.out.printf("还原一致=%b，前 10 个: %s%n",
                Arrays.equals(data, result), Arrays.toString(Arrays.copyOf(result, 10)));
        System.out.println();
    }

    private static byte[] toByteArray(int[] data) {
        byte[] bytes = new byte[data.length * Integer.BYTES];
        for (int i = 0; i < data.length; i++) {
            int v = data[i];
            bytes[i * 4] = (byte) (v >>> 24);
            bytes[i * 4 + 1] = (byte) (v >>> 16);
            bytes[i * 4 + 2] = (byte) (v >>> 8);
            bytes[i * 4 + 3] = (byte) v;
        }
        return bytes;
    }
}
