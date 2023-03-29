package featuretest.ios;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteBufferApp {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        System.out.println(buffer);

        buffer.put((byte) 1);
        buffer.put((byte) 2);
        buffer.put((byte) 3);

        System.out.println(buffer);
        buffer.flip(); // switch to read
        System.out.println(buffer);

        buffer.get();
        buffer.get();
        System.out.println(buffer);

//        buffer.position(0);

        System.out.println(buffer);

        var bb = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
        var cc = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);

        cc.put((byte) 5);
        System.out.println();
    }
}
