package featuretest.virtualthread;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class VirtualThreadExample {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting virtual threads example...");

        // 方法1: 使用 Executors.newVirtualThreadPerTaskExecutor()
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 10_000).forEach(i -> {
                executor.submit(() -> {
                    Thread.sleep(Duration.ofMillis(100));
                    System.out.println("Virtual thread " + i + " executed by: " + Thread.currentThread());
                    return i;
                });
            });
        } // executor.close() 会自动调用，等待所有任务完成

        System.out.println("First example completed.");

        // 方法2: 直接使用 Thread.startVirtualThread()
        Thread virtualThread = Thread.startVirtualThread(() -> {
            System.out.println("Direct virtual thread: " + Thread.currentThread());
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Direct virtual thread completed");
        });

        virtualThread.join(); // 等待虚拟线程完成

        System.out.println("Second example completed.");

        // 方法3: 使用 Thread.ofVirtual().start()
        Thread.Builder virtualThreadBuilder = Thread.ofVirtual().name("custom-virtual-thread-", 0);
        Thread[] virtualThreads = new Thread[5];

        for (int i = 0; i < virtualThreads.length; i++) {
            virtualThreads[i] = virtualThreadBuilder.start(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + " started");
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println(threadName + " finished");
            });
        }

        // 等待所有虚拟线程完成
        for (Thread vt : virtualThreads) {
            vt.join();
        }

        System.out.println("All virtual threads completed.");
    }
}
