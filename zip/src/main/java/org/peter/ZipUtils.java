package org.peter;

import java.io.*;
import java.util.zip.*;

public class ZipUtils {

    /**
     * 压缩入口
     */
    public static void zip(File srcFile, File zipFile) throws IOException {
        if (!srcFile.exists()) {
            throw new FileNotFoundException(srcFile.getAbsolutePath());
        }

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipInternal(srcFile, srcFile.getName(), zos);
        }
    }

    /**
     * 递归压缩
     */
    private static void zipInternal(File file, String entryName, ZipOutputStream zos) throws IOException {
        if (file.isDirectory()) {
            // 必须在名字后加 "/" 才能标记为文件夹
            if (!entryName.endsWith("/")) {
                entryName += "/";
            }

            zos.putNextEntry(new ZipEntry(entryName));
            zos.closeEntry();

            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    zipInternal(child, entryName + child.getName(), zos);
                }
            }
        } else {
            // 文件
            zos.putNextEntry(new ZipEntry(entryName));
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
            }
            zos.closeEntry();
        }
    }
}

