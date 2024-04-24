package org.peter;

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
    }
}