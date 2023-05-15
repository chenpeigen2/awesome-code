package featuretest.threads;

public class Thread_100 {

    static volatile int count = 1;

    //    https://blog.csdn.net/permeability/article/details/117235608
    public static void main(String[] args) throws InterruptedException {
        Object obj1 = new Object();
        Object obj2 = new Object();
        Object obj3 = new Object();


//
        Thread t1 = new Thread(() -> {
            while (true) {
                synchronized (obj1) {
                    try {
                        obj1.wait();
                        if (count == 10001) {
                            synchronized (obj2) {
                                obj2.notify();
                            }
                            break;
                        }

                        System.out.println(count++);
                        synchronized (obj2) {
                            obj2.notify();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, "A");
        Thread t2 = new Thread(() -> {
            while (true) {
                synchronized (obj2) {
                    try {
                        obj2.wait();
                        if (count == 10001) {
                            synchronized (obj3) {
                                obj3.notify();
                            }
                            break;
                        }
                        System.out.println(count++);
                        synchronized (obj3) {
                            obj3.notify();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, "B");
        Thread t3 = new Thread(() -> {
            while (true) {
                synchronized (obj3) {
                    try {
                        obj3.wait();
                        if (count == 10001) {
                            synchronized (obj1) {
                                obj1.notify();
                            }
                            break;
                        }
                        System.out.println(count++);
                        synchronized (obj1) {
                            obj1.notify();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, "C");


        t1.start();
        t2.start();
        t3.start();

        synchronized (obj1) {
            obj1.notify();
        }

//        PrintNumber printNumber = new PrintNumber();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 34; i++) {
//                    printNumber.firstPrint();
//                }
//            }
//        }, "线程一").start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 33; i++) {
//                    printNumber.secondPrint();
//                }
//            }
//        }, "线程二").start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 33; i++) {
//                    printNumber.thirdPrint();
//                }
//            }
//        }, "线程三").start();
    }
}

class PrintNumber {
    private volatile int number = 1;
    private volatile int value = 1;

    void firstPrint() {
        synchronized (this) {
            while (value != 1) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + ": " + number);
            number++;
            value = 2;
            notifyAll();
        }
    }

    void secondPrint() {
        synchronized (this) {
            while (value != 2) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + ": " + number);
            number++;
            value = 3;
            notifyAll();
        }
    }

    void thirdPrint() {
        synchronized (this) {
            while (value != 3) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + ": " + number);
            number++;
            value = 1;
            notifyAll();
        }
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
                    if (value == 100) {
                        System.out.println(value);
                        break;
                    }
                    System.out.println(value++);
                    next.notify();
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
