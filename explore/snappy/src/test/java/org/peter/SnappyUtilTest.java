package org.peter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnappyUtilTest {

    @Test
    void compressRoundTripRestoresOriginalBytes() {
        byte[] original = "Hello snappy-java! Snappy-java is a JNI-based wrapper.".getBytes(StandardCharsets.UTF_8);
        byte[] compressed = SnappyUtil.compress(original);
        assertArrayEquals(original, SnappyUtil.unCompress(compressed));
    }

    @Test
    void compressShrinksRepetitiveData() {
        byte[] original = "abcabcabc".repeat(1000).getBytes(StandardCharsets.UTF_8);
        byte[] compressed = SnappyUtil.compress(original);
        assertTrue(compressed.length < original.length,
                "repetitive %d bytes should compress below %d, got %d"
                        .formatted(original.length, original.length, compressed.length));
    }

    @Test
    void compressReturnsNullForNullOrEmptyInput() {
        assertNull(SnappyUtil.compress(null));
        assertNull(SnappyUtil.compress(new byte[0]));
    }

    @Test
    void unCompressReturnsNullForNullOrEmptyInput() {
        assertNull(SnappyUtil.unCompress(null));
        assertNull(SnappyUtil.unCompress(new byte[0]));
    }

    @Test
    void unCompressReturnsNullForCorruptedData() {
        assertNull(SnappyUtil.unCompress(new byte[]{1, 2, 3, 4, 5}));
    }

    @Test
    void streamRoundTripRestoresOriginalBytes() {
        byte[] original = "stream format round trip".getBytes(StandardCharsets.UTF_8);
        byte[] compressed = SnappyUtil.compressWithHeader(original);
        assertArrayEquals(original, SnappyUtil.unCompressWithHeader(compressed));
    }

    @Test
    void streamRoundTripHandlesLargePayloadAcrossBlocks() {
        // 1 MiB 重复数据，超过 SnappyOutputStream 内部块大小，强制多块读写
        byte[] original = "0123456789abcdef".repeat(64 * 1024).getBytes(StandardCharsets.UTF_8);
        byte[] compressed = SnappyUtil.compressWithHeader(original);
        assertArrayEquals(original, SnappyUtil.unCompressWithHeader(compressed));
    }

    @Test
    void streamCompressReturnsNullForNullOrEmptyInput() {
        assertNull(SnappyUtil.compressWithHeader(null));
        assertNull(SnappyUtil.compressWithHeader(new byte[0]));
    }

    @Test
    void streamUnCompressReturnsNullForNullOrEmptyInput() {
        assertNull(SnappyUtil.unCompressWithHeader(null));
        assertNull(SnappyUtil.unCompressWithHeader(new byte[0]));
    }

    @Test
    void streamUnCompressReturnsNullForCorruptedData() {
        assertNull(SnappyUtil.unCompressWithHeader(new byte[]{1, 2, 3, 4, 5}));
    }

    // ---------- 字符串压缩 ----------

    @Test
    void stringRoundTripRestoresOriginalTextIncludingChinese() {
        String original = "Hello snappy! 你好，斯纳皮！🎵";
        byte[] compressed = SnappyUtil.compressString(original);
        assertEquals(original, SnappyUtil.unCompressToString(compressed));
    }

    @Test
    void compressStringReturnsNullForNullOrEmptyInput() {
        assertNull(SnappyUtil.compressString(null));
        assertNull(SnappyUtil.compressString(""));
    }

    @Test
    void unCompressToStringReturnsNullForNullOrEmptyInput() {
        assertNull(SnappyUtil.unCompressToString(null));
        assertNull(SnappyUtil.unCompressToString(new byte[0]));
    }

    // ---------- 文件压缩 ----------

    @Test
    void fileRoundTripRestoresOriginalContent(@TempDir Path tempDir) throws IOException {
        byte[] original = "0123456789abcdef".repeat(64 * 1024).getBytes(StandardCharsets.UTF_8);
        Path src = tempDir.resolve("src.bin");
        Path compressed = tempDir.resolve("src.bin.snappy");
        Path dst = tempDir.resolve("dst.bin");
        Files.write(src, original);

        assertTrue(SnappyUtil.compressFile(src, compressed));
        assertTrue(Files.size(compressed) < Files.size(src));
        assertTrue(SnappyUtil.unCompressFile(compressed, dst));
        assertArrayEquals(original, Files.readAllBytes(dst));
    }

    @Test
    void fileCompressFailsForMissingSource(@TempDir Path tempDir) {
        Path missing = tempDir.resolve("not-exist.bin");
        Path out = tempDir.resolve("out.snappy");
        assertFalse(SnappyUtil.compressFile(missing, out));
        assertTrue(Files.notExists(out));
    }

    @Test
    void fileUnCompressFailsForCorruptedData(@TempDir Path tempDir) throws IOException {
        Path corrupted = tempDir.resolve("corrupted.snappy");
        Path out = tempDir.resolve("out.bin");
        Files.write(corrupted, new byte[]{1, 2, 3, 4, 5});
        assertFalse(SnappyUtil.unCompressFile(corrupted, out));
        assertTrue(Files.notExists(out));
    }

    // ---------- 压缩率统计 ----------

    @Test
    void compressionRatioUsesOriginalSizeAsTruth() {
        byte[] original = new byte[10_000];
        Arrays.fill(original, (byte) 'a');
        byte[] compressed = SnappyUtil.compress(original);
        assertTrue(SnappyUtil.compressionRatio(compressed, original) > 1.0);
    }
}
