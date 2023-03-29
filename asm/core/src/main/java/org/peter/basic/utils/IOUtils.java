package org.peter.basic.utils;

import com.google.common.io.Files;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class IOUtils {
    /**
     * @param node input-node
     * @param file file to write (maybe null)
     */
    @SneakyThrows
    public static void writeClassNodeToFile(@NonNull ClassNode node, @NonNull File file) {
        ClassWriter cw = new ClassWriter(0);
        node.accept(cw);
        byte[] code = cw.toByteArray();
        // if we not have the parent dir we should create
        Files.createParentDirs(file);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(code);
        }
    }

    @SneakyThrows
    public static ClassReader getClassReader(@NonNull File f) {
        return getClassReader(new FileInputStream(f));
    }

    @SneakyThrows
    public static ClassReader getClassReader(@NonNull InputStream f) {
        return new ClassReader(f);
    }

    @SneakyThrows
    public static ClassReader getClassReader(@NonNull String className) {
        return new ClassReader(className);
    }

}
