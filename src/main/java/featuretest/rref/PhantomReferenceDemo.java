package featuretest.rref;


import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author pilaf
 * @description
 * @date 2023-04-05 22:00
 **/
public class PhantomReferenceDemo {

    private static class MyClass {
        private Date birthTime;

        public MyClass() {
            birthTime = new Date();
        }
        // 不能重写finalize方法，否则MyResourceFinalizer就不会被放到引用队列中
        // 原因见：https://stackoverflow.com/questions/48167735/why-the-reference-dont-put-into-reference-queue-when-finalize-method-overrided
//        @Override
//        public void finalize() throws Throwable{
//            System.out.println("finalize invoked..");
//        }
    }

    private static class MyResourceFinalizer extends PhantomReference<MyClass> {
        // 模拟要释放的内存地址
        private String toReleaseAddress = null;

        public MyResourceFinalizer(MyClass referent, ReferenceQueue<MyClass> referenceQueue) {
            super(referent, referenceQueue);
            toReleaseAddress = String.valueOf(referent.hashCode());
        }

        public void releaseResource() {
            System.out.println("释放内存地址：" + toReleaseAddress);
        }
    }


    public static void main(String[] args) throws Exception {
        ReferenceQueue<MyClass> queue = new ReferenceQueue<>();

        List<MyClass> myClassList = new ArrayList<>();
        List<MyResourceFinalizer> myResourceFinalizers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MyClass myClass = new MyClass();
            myClassList.add(myClass);
            // 虚引用需要和引用队列一起使用，这样再垃圾回收完虚引用的对象后，它的虚引用会被放到队列中
            myResourceFinalizers.add(new MyResourceFinalizer(myClass, queue));
        }

        // 启动另一个线程来检查是否有虚引用被回收了
        new Thread(() -> {
            while (true) {
                MyResourceFinalizer ref = (MyResourceFinalizer) queue.poll();
                if (ref != null) {
                    System.out.println("虚引用被回收：" + ref);
                    ref.releaseResource();
                    ref.clear();
                }
            }
        }).start();

        // 稍微睡眠一下，确保前面的线程启动了
        TimeUnit.SECONDS.sleep(2);
        // help gc
        myClassList = null;
        // 暗示JVM进行垃圾回收
        System.gc();


        for (MyResourceFinalizer myResourceFinalizer : myResourceFinalizers) {
            // 输出true，才表示引用进了队列了
            System.out.println(myResourceFinalizer + " isEnqueued:" + myResourceFinalizer.isEnqueued());
        }
    }
}


