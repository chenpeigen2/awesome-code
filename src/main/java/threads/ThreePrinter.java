package threads;

public class ThreePrinter {

    int n = 1;

    int flag = 1;


    final Object lock = new Object();

    public void first() {
        while (n <= 100) {
            synchronized (lock) {
                while (flag != 1 && n <= 100) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (n < 101) {
                    System.out.println(Thread.currentThread() + "  " + n++);

                } else {
                    break;
                }
                flag = 2;
                lock.notifyAll();
            }
        }
    }

    public void second() {
        while (n <= 100) {
            synchronized (lock) {
                while (flag != 2 && n <= 100) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (n < 101) {
                    System.out.println(Thread.currentThread() + "  " + n++);

                } else {
                    break;
                }
                flag = 3;
                lock.notifyAll();
            }
        }
    }


    public void third() {
        while (n <= 100) {
            synchronized (lock) {
                while (flag != 3 && n <= 100) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (n < 101) {
                    System.out.println(Thread.currentThread() + "  " + n++);
                } else {
                    break;
                }
                flag = 1;
                lock.notifyAll();
            }
        }
    }


    public static void main(String[] args) {
        ThreePrinter threePrinter = new ThreePrinter();
        new Thread(new Runnable() {
            @Override
            public void run() {
                threePrinter.first();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                threePrinter.second();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                threePrinter.third();
            }
        }).start();
    }

}

class Single {
    private Single() {

    }

    private static volatile Single single;

    public static Single getInstance() {
        if (single == null) {
            synchronized (Single.class) {
                if (single ==null) {
                    single = new Single();
                }
            }
        }
        return single;
    }
}
