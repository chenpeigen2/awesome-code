package org.peter;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
}
