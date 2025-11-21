package org.peter;

import java.io.*;
import java.util.zip.*;

public class UnzipUtils {

    public static void unzip(File zipFile, File destDir) throws IOException {
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                File newFile = safeCreateFile(destDir, entry.getName());

                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    // 确保父目录存在
                    newFile.getParentFile().mkdirs();

                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    /**
     * 安全检查，避免 Zip Slip（解压覆盖系统文件的攻击）
     * entryName 不能跳出目标目录
     */
    private static File safeCreateFile(File destDir, String entryName) throws IOException {
        File destFile = new File(destDir, entryName);
        String destPath = destDir.getCanonicalPath();
        String filePath = destFile.getCanonicalPath();

        if (!filePath.startsWith(destPath + File.separator)) {
            throw new IOException("ZIP entry is outside target dir: " + entryName);
        }
        return destFile;
    }
}
