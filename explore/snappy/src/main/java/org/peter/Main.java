package org.peter;

import org.xerial.snappy.BitShuffle;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        String input = "Hello snappy-java! Snappy-java is a JNI-based wrapper of "
                + "Snappy, a fast compresser/decompresser.";
        byte[] compressed = SnappyUtil.compress(input.getBytes(StandardCharsets.UTF_8));
        byte[] uncompressed = SnappyUtil.unCompress(compressed);
        String result = new String(uncompressed, StandardCharsets.UTF_8);

        System.out.println(compressed.length);
        System.out.println(uncompressed.length);
        System.out.println(result);

        try {
            bitShuffle();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void bitShuffle() throws IOException {

        int[] data = new int[]{1, 3, 34, 43, 34};
        byte[] shuffledByteArray = BitShuffle.shuffle(data);
        byte[] compressed = Snappy.compress(shuffledByteArray);
        byte[] uncompressed = Snappy.uncompress(compressed);
        int[] result = BitShuffle.unshuffleIntArray(uncompressed);
        System.out.println(result);
    }
}