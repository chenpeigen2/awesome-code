package lee.adds;

//https://blog.51cto.com/u_15127579/3372662
public class Producer_Consumer {

    static class Cargo {

        static int MAX = 10;
        volatile int count = -1;

        static volatile int produced_cnt = 0;

        volatile boolean produced = false; //indicate should produce one by one


        void produce() {
            count++;
            produced_cnt++;
            produced = true;
        }


        void consume() {
            System.out.println(produced_cnt);
            produced = false;
        }

        boolean isFull() {
            return count >= MAX;
        }

        boolean canProduce() {
            return !isFull() && !produced;
        }

        boolean canConsume() {
            return !isFull() && produced;
        }
    }


    static class Producer extends Thread {

        private final Cargo object;

        public Producer(Cargo object) {
            this.object = object;
        }

        @Override
        public void run() {
            while (!object.isFull()) {
                synchronized (object) {
                    if (!object.canProduce()) {
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        object.produce();
                        object.notifyAll();
                    }

                }
            }
        }
    }

    static class Consumer extends Thread {

        private final Cargo object;

        public Consumer(Cargo object) {
            this.object = object;
        }

        @Override
        public void run() {
            while (!object.isFull()) {
                synchronized (object) {
                    if (!object.canConsume()) {
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        object.consume();
                        object.notifyAll();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Cargo cargo = new Cargo();
        var producer = new Producer(cargo);
        var consumer = new Consumer(cargo);

        producer.start();
        consumer.start();
    }
}

class Main {
    public static void main(String[] args) throws InterruptedException {
        //Scanner in = new Scanner(System.in);
        //int a = in.nextInt();
        Object o1 = new Object();
        Object o2 = new Object();
        Producer producer = new Producer(o1, o2);
        Consumer consumer = new Consumer(o1, o2);

        producer.start();
        consumer.start();

        Thread.sleep(1000);
        synchronized(o1) {
            o1.notify();
        }
    }

    static int cargo = -1;

    static volatile int cnt = 0;


    static class Producer extends Thread {

        private Object o1;
        private Object o2;

        public Producer(Object o1, Object o2) {
            this.o1 = o1;
            this.o2 = o2;
        }

        public void run() {
            while (cnt < 1000) {
                synchronized(o1) {
                    try {
                        o1.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    cargo = 1; // produce the cargo
                    cnt++;
                    synchronized(o2) {
                        o2.notify();
                    }
                }
            }
        }
    }

    static class Consumer extends Thread {
        private Object o1;
        private Object o2;

        public Consumer(Object o1, Object o2) {
            this.o1 = o1;
            this.o2 = o2;
        }

        public void run() {
            while (cnt < 1000) {
                synchronized(o2) {
                    try {
                        System.out.println(cnt);
                        o2.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(cnt);
                    cargo = -1; // means consumed

                    synchronized(o1) {
                        o1.notify();
                    }
                }
            }
        }
    }
}
