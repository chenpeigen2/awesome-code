package org.peter.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class Channel {
    public static void main(String[] args) throws IOException {
        // 创建文件输出字节流
        FileInputStream fos = new FileInputStream("data.txt");
        //得到文件通道
        FileChannel fc = fos.getChannel();


//        fc.position(fc.size());
//
//
//        //往通道写入ByteBuffer
//        fc.write(ByteBuffer.wrap("abcdefgdadfasf ".getBytes()));


        var str = read(fc, Charset.defaultCharset());

        System.out.println(str);

        //关闭流
        fos.close();
    }

    public static String read(FileChannel fileChannel, Charset charset) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder stringBuilder = new StringBuilder();
        fileChannel.read(charset.encode("UTE-8"));
        while (fileChannel.read(buffer) != -1) {
            buffer.flip();
            stringBuilder.append(charset.decode(buffer));
            buffer.clear();
        }

        return stringBuilder.toString();
    }

}
