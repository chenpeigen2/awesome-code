package javalib.base;

import java.nio.file.FileSystems;
import java.nio.file.spi.FileSystemProvider;

public class FileSpi {
    public static void main(String[] args) {
        FileSystemProvider fileSystemProvider = FileSystems.getDefault().provider();
    }
}
