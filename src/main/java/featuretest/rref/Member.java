package featuretest.rref;


import java.lang.ref.Cleaner;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;

class Member implements Runnable {
    public Member() {
        System.out.println("【构造】在一个雷电交加的日子里，林强诞生了");
    }

    @Override
    public void run() {    //执行清除的时候执行的是此操作
        System.out.println("【回收】最终还是要死的");
    }
}

class MemberCleaning implements AutoCloseable {    //实现清除的处理
    private static final Cleaner cleaner = Cleaner.create();//创建一个清除处理
    private Member member;
    private Cleaner.Cleanable cleanable;

    public MemberCleaning() {
        this.member = new Member();    //创建新对象
        this.cleanable = this.cleaner.register(this, this.member);//注册使用的对象
    }

    @Override
    public void close() throws Exception {
        this.cleanable.clean();//启动多线程
    }
}

class Cleaner类 {
    public static void main(String[] args) throws Exception {
        MemberCleaning mc = new MemberCleaning();
        mc = null;

        System.gc();
        System.gc();
        System.gc();
//        mc.close();
//        try (MemberCleaning mc = new MemberCleaning()) {
//            //中间可以执行一些相关的代码
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        public static void main(String[] args) {
        // 创建一个Cleaner对象，只能使用静态工厂方法来创建
//        Cleaner cl = Cleaner.create();
//
//
//        while (true) {
//            String s1 = new String("oioiji");
//            // 通过register方法，为需要做善后工作的s1对象添加清理函数
//            cl.register(s1, () -> {
//                System.out.println("clean one object");
//                System.out.println(s1);
//            });
//
//        }

    }
}


class App {

    private static ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
    private static List<PhantomReference> references = new ArrayList<>();
    private static List<DataObj> dataObjs = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 32; ++i) {
            DataObj obj = new DataObj();
            PhantomReference<Object> phantomReference = new PhantomReference<>(obj, referenceQueue);
            references.add(phantomReference);
            dataObjs.add(obj);
        }

        // 设置为null 让相关对象编程虚可达 被GC特殊对待一下
        dataObjs = null;

        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        Thread.sleep(5000L);
        // 直接在主线程看
        Reference<?> reference = null;
        while ((reference = referenceQueue.poll()) != null) {
            System.out.println(reference);
            reference.clear();
        }

        System.out.println();
    }


}

class DataObj {
}