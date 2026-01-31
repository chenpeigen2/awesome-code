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

    public void fourth() {
        while (n <= 100) {
            synchronized (lock) {
                while (flag != 4 && n <= 100) {
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

    private void fifth() {
        while (n <= 100) {
            synchronized (lock) {
                while (flag != 5 && n <= 100) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (n <= 101) {
                    System.out.println(n++);
                } else {
                    break;
                }
                flag = 1;
                lock.notifyAll();
            }
        }
    }

    private void six() {
        while (n <= 101) {
            synchronized (lock) {
                while (flag != 6 && n <= 101) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (flag <= 101) {
                    System.out.println(n++);
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
                if (single == null) {
                    single = new Single();
                }
            }
        }
        return single;
    }
}

class Single2 {
    private Single2() {

    }

    private volatile Single2 instance;

    public Single2 getInstance() {
        if (instance == null) {
            synchronized (Single2.class) {
                if (instance == null) {
                    instance = new Single2();
                }
            }
        }
        return instance;
    }
}
