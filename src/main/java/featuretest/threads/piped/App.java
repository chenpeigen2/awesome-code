package featuretest.threads.piped;

import java.io.PipedReader;
import java.io.PipedWriter;

public class App {

    public static void testPipedReaderWriter() {
        /**
         * 管道流通信核心是,Writer和Reader公用一块缓冲区,缓冲区在Reader中申请,
         * 由Writer调用和它绑定的Reader的Receive方法进行写.
         *
         * 线程间通过管道流通信的步骤为
         * 1 建立输入输出流
         * 2 绑定输入输出流
         * 3 Writer写
         * 4 Reader读
         */
        PipedReader reader = new PipedReader();
        PipedWriter writer = new PipedWriter();
        Producer producer = new Producer(writer);
        Consumer consumer = new Consumer(reader);

        try {
            writer.connect(reader);
            producer.start();
            consumer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        testPipedReaderWriter();
    }
}
