package org.peter;

import org.xerial.snappy.Snappy;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;

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

}
