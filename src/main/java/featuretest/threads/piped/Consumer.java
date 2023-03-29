package featuretest.threads.piped;

import java.io.PipedReader;

public class Consumer extends Thread {
    //输入流
    private PipedReader reader = new PipedReader();

    public Consumer(PipedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        try {
            char[] cbuf = new char[20];
            reader.read(cbuf, 0, cbuf.length);
            System.out.println("管道流中的数据为: " + new String(cbuf));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
