package featuretest.threads.piped;

import java.io.PipedWriter;

public class Producer extends Thread {
    //输出流
    private PipedWriter writer = new PipedWriter();

    public Producer(PipedWriter writer) {
        this.writer = writer;
    }

    @Override
    public void run() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Hello World!");
            writer.write(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
