package featuretest;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {
    public static void main(String[] args) {
        //一个私人旅行团包了一个导游（其实也可以没有导游），大家都相互熟悉，知道要等一下家人或者朋友
        //那么此时需要有个计数器，计数值设置为3
        CyclicBarrier cyclicBarrier = new CyclicBarrier(30, () -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName());
            System.out.println("导游：都到齐了！出发！青春没有售价！我们直飞拉萨，带着A,B,C产生的结果出发了！");
        });
        new Thread(() -> {
            try {
                System.out.println("A在群里发，我还要15millis");
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "到了，剩下俩也不知到来了没有调用await()等下吧");
            //有这个await()真方便我也不用关心他们到了没，都到了计数器就可以减为0就可以出发了
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            //阻塞结束后
            System.out.println("哦！都到了！A提着包说到");
        }).start();
        new Thread(() -> {
            try {
                System.out.println("B在群里发，我还要20millis");
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "到了，剩下俩也不知到来了没有调用await()等下吧");
            //有这个await()真方便我也不用关心他们到了没，都到了计数器就可以减为0就可以出发了
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            //阻塞结束后
            System.out.println("哦！都到了！B嘴里塞满了热狗支支吾吾说到");
        }).start();
        new Thread(() -> {
            try {
                System.out.println("C在群里发，我还要30millis");
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "到了，剩下俩也不知到来了没有调用await()等下吧");
            //有这个await()真方便我也不用关心他们到了没，都到了计数器就可以减为0就可以出发了
            try {
                System.out.println(cyclicBarrier.await());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            //阻塞结束后
            System.out.println("害！都到了，得亏你们用await()等我，我堵车了！C气喘吁吁说到");
        }).start();

        new Thread(() -> {
            try {
                System.out.println("D在群里发，我还要30millis");
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "到了，剩下俩也不知到来了没有调用await()等下吧");
            //有这个await()真方便我也不用关心他们到了没，都到了计数器就可以减为0就可以出发了
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            //阻塞结束后
            System.out.println("害！都到了，得亏你们用await()等我，我堵车了！C气喘吁吁说到");
        }).start();


        new Thread(() -> {
            try {
                System.out.println("E在群里发，我还要30millis");
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "到了，剩下俩也不知到来了没有调用await()等下吧");
            //有这个await()真方便我也不用关心他们到了没，都到了计数器就可以减为0就可以出发了
            try {
                System.out.println(cyclicBarrier.await());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            //阻塞结束后
            System.out.println("害！都到了，得亏你们用await()等我，我堵车了！C气喘吁吁说到");
        }).start();
    }
}
