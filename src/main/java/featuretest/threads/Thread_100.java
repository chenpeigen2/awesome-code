package featuretest.threads;

public class Thread_100 {

    //    https://blog.csdn.net/permeability/article/details/117235608
    public static void main(String[] args) throws InterruptedException {
        Object A = new Object();
        Object B = new Object();
        Object C = new Object();

        Thread t1 = new Thread(new Print(A, B), "A");
        Thread t2 = new Thread(new Print(B, C), "B");
        Thread t3 = new Thread(new Print(C, A), "C");

        //Thread.sleep(1)为了让打印的时候依次执行
        t1.start();
        Thread.sleep(1);
        t2.start();
        Thread.sleep(1);
        t3.start();
    }
}


class Print implements Runnable {

    volatile static int value;

    private final Object self;
    private final Object next;

    public Print(Object self, Object next) throws InterruptedException {
        this.self = self;
        this.next = next;
    }

    public void run() {
        while (true) {
            synchronized (self) {
                synchronized (next) {
                    System.out.println(value++);
                    next.notify();
                    if (value == 100) {
                        break;
                    }
                }
                try {
                    self.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
