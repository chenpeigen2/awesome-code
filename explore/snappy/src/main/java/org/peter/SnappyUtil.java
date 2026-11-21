package org.peter;

import org.xerial.snappy.Snappy;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SnappyUtil {

    public static byte[] compress(byte bytes[]) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return Snappy.compress(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] unCompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return Snappy.uncompress(bytes);
        } catch (Exception e) {
            return null;
        }
    }


    public static byte[] compressWithHeader(byte bytes[]) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            SnappyOutputStream sos = new SnappyOutputStream(os);
            int count;
            byte temp[] = new byte[4096];
            try {
                while ((count = is.read(temp)) != -1) {
                    sos.write(temp, 0, count);
                }
                sos.flush();
                return os.toByteArray();
            } finally {
                sos.close();
                is.close();
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] unCompressWithHeader(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream byteAos = null;
        ByteArrayInputStream byteArrayIn = null;
        SnappyInputStream gzipIn = null;
        try {
            byteArrayIn = new ByteArrayInputStream(bytes);
            gzipIn = new SnappyInputStream(byteArrayIn);
            byteAos = new ByteArrayOutputStream();
            byte[] b = new byte[4096];
            int temp = -1;
            while ((temp = gzipIn.read(b)) > 0) {
                byteAos.write(b, 0, temp);
            }
        } catch (Exception e) {
            return null;
        } finally {
            closeStream(byteAos);
            closeStream(gzipIn);
            closeStream(byteArrayIn);
        }
        return byteAos.toByteArray();
    }

    private static void closeStream(Closeable oStream) {
        if (null != oStream) {
            try {
                oStream.close();
            } catch (IOException e) {
                oStream = null;//赋值为null,等待垃圾回收
                e.printStackTrace();
            }
        }
    }

    // ---------- 字符串便捷方法 ----------

    /**
     * 压缩 UTF-8 字符串，null 或空串返回 null
     */
    public static byte[] compressString(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        return compress(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 解压为 UTF-8 字符串，入参为 null 或空数组时返回 null
     */
    public static String unCompressToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        byte[] uncompressed = unCompress(bytes);
        if (uncompressed == null) {
            return null;
        }
        return new String(uncompressed, StandardCharsets.UTF_8);
    }

    // ---------- 文件压缩（流格式，带 snappy 头） ----------

    /**
     * 将 src 文件压缩为流格式写入 dst，成功返回 true
     */
    public static boolean compressFile(Path src, Path dst) {
        if (src == null || dst == null || Files.notExists(src)) {
            return false;
        }
        try (InputStream in = Files.newInputStream(src);
             OutputStream out = new SnappyOutputStream(Files.newOutputStream(dst))) {
            in.transferTo(out);
            out.flush();
            return true;
        } catch (Exception e) {
            deleteQuietly(dst);
            return false;
        }
    }

    /**
     * 将流格式的压缩文件 src 解压写入 dst，成功返回 true
     */
    public static boolean unCompressFile(Path src, Path dst) {
        if (src == null || dst == null || Files.notExists(src)) {
            return false;
        }
        try (InputStream in = new SnappyInputStream(Files.newInputStream(src));
             OutputStream out = Files.newOutputStream(dst)) {
            in.transferTo(out);
            return true;
        } catch (Exception e) {
            deleteQuietly(dst);
            return false;
        }
    }

    // ---------- 统计 ----------

    /**
     * 压缩比 = 原始字节数 / 压缩后字节数，大于 1 表示确实变小了
     */
    public static double compressionRatio(byte[] compressed, byte[] original) {
        if (compressed == null || original == null || compressed.length == 0) {
            return 0;
        }
        return (double) original.length / compressed.length;
    }

    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // 清理失败不影响返回值语义
        }
    }

}
