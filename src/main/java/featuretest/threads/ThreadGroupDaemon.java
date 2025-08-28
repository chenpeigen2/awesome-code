package featuretest.threads;

import java.util.concurrent.TimeUnit;

public class ThreadGroupDaemon {

    public static void main(String[] args) throws InterruptedException {
        ThreadGroup group1 = new ThreadGroup("group1");
        Thread thread1 = new Thread(group1, () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "group1-thread1");
        thread1.start();

        ThreadGroup group2 = new ThreadGroup("group2");
        Thread thread2 = new Thread(group2, () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "group2-thread2");
        thread2.start();

        // 等待所有线程结束
        thread1.join();
        thread2.join();
    }
}