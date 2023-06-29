package featuretest.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

//ReferenceQueue 中的 poll() 和
// remove() 方法都可以用于从队列中获取引用对象。
// 它们的区别在于，当队列为空时，poll() 方法会立即返回 null，
// 而 remove() 方法会一直阻塞等待，直到有引用对象可用为止。
public class Test1 {
    public static void main(String[] args) throws InterruptedException {
        Object obj = new Object();
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
        WeakReference<Object> weakReference = new WeakReference<>(obj, referenceQueue);

        obj = null; // 让 obj 对象变成不可达

        referenceQueue.remove();

// 等待垃圾回收器回收 weakReference 对象
        while (referenceQueue.poll() == null) {
            System.gc();
        }

    }
}
