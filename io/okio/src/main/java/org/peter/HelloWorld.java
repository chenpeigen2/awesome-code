package org.peter;

import lombok.SneakyThrows;
import okio.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class HelloWorld {
    public static void main(String[] args) {
        System.out.println(1);

        File f = new File("./LICENSE");


        try {
            System.out.println("-------------------");
            readLines(f);
            System.out.println("-------------------");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File f1 = new File("./a");
        compress(f1);
    }

    public static void readLines(File file) throws IOException {
        Source fileSource = Okio.source(file);
        BufferedSource bufferedSource = Okio.buffer(fileSource);
        for (String line; (line = bufferedSource.readUtf8Line()) != null; ) {
            System.out.println(line);
        }
        bufferedSource.close();
    }

    public void writeEnv(File file) throws IOException {
        Sink fileSink = Okio.sink(file);
        BufferedSink bufferedSink = Okio.buffer(fileSink);
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            bufferedSink.writeUtf8(entry.getKey());
            bufferedSink.writeUtf8("=");
            bufferedSink.writeUtf8(entry.getValue());
            bufferedSink.writeUtf8("\n");
        }
        bufferedSink.close();
    }

    public void writeEnv(File file, String... args) throws IOException {
        try (BufferedSink sink = Okio.buffer(Okio.sink(file))) {
            sink.writeUtf8("啊啊啊")
                    .writeUtf8("=")
                    .writeUtf8("aaa")
                    .writeUtf8("\n");
        }
    }

    @SneakyThrows
    public static void compress(File file) {
        GzipSink gzipSink = new GzipSink(Okio.sink(file));
        BufferedSink bufferedSink = Okio.buffer(gzipSink);
        bufferedSink.writeUtf8("this is zip file");
        bufferedSink.flush();
        bufferedSink.close();
    }

    @SneakyThrows
    public void uncompress(File file) {
        //读取zip
        GzipSource gzipSource = new GzipSource(Okio.source(file));
        BufferedSource bufferedSource = Okio.buffer(gzipSource);
        String s = bufferedSource.readUtf8();
        bufferedSource.close();
    }

}
