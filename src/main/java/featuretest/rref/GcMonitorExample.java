package featuretest.rref;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

import static java.lang.System.out;

class Video {
    private byte[] data;

    public Video() {
        this.data = new byte[1024 * 1024 * 30]; //30MB
        out.println("Allocated " + this.data.length + " bytes memory");
    }
}

public class GcMonitorExample {

    public static void main(String[] args) throws InterruptedException {
        final ReferenceQueue<Video> referenceQueue = new ReferenceQueue<Video>();

        // 模拟加载3个视频到内存中
        final Video video1 = new Video();
        final PhantomReference<Video> videoRef1 = new PhantomReference<Video>(video1, referenceQueue);

        Video video2 = new Video();
        final PhantomReference<Video> videoRef2 = new PhantomReference<Video>(video2, referenceQueue);

        Video video3 = new Video();
        final PhantomReference<Video> videoRef3 = new PhantomReference<Video>(video3, referenceQueue);

        // 在另外的线程中监控 GC 情况
        new Thread() {
            @Override
            public void run() {

                for (int i = 0; i < 10; i++) {
                    try {
                        System.gc();
                        Thread.sleep(1000);

                        PhantomReference<Video> ref = (PhantomReference<Video>) referenceQueue.poll();
                        if (ref == videoRef1) {
                            out.println("video 1 has been GC");
                        } else if (ref == videoRef2) {
                            out.println("video 2 has been GC");
                        } else if (ref == videoRef3) {
                            out.println("video 3 has been GC");
                        }
                        out.println();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();

        // 释放 video2 的强引用
        video2 = null;
        video3 = null;

        while (true) {
            // 保持程序运行，否则 JVM 关闭，所有 video 实例都会被回收
            Thread.sleep(1000);
        }
    }

}