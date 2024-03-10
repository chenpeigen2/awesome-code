package featuretest;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

public class StopwatchDemo {
    public void test1() throws Exception {
        String orderNo = "12345678";

        System.out.println("订单 [{" + orderNo + "}] 开始处理");
        Stopwatch stopwatch = Stopwatch.createStarted();

        TimeUnit.SECONDS.sleep(1);  // 1秒处理时间

        System.out.println("订单 [{" + orderNo + "}] 处理完成，耗时 [{" + stopwatch.stop() + "}]");
    }

    public void test2() throws Exception {
        // 创建stopwatch并开始计时
        Stopwatch stopwatch = Stopwatch.createStarted();
        Thread.sleep(1980);
        // 以秒打印从计时开始至现在的所用时间，向下取整
        System.out.println(stopwatch.elapsed(TimeUnit.SECONDS)); // 1
        // 停止计时
        stopwatch.stop();
        System.out.println(stopwatch.elapsed(TimeUnit.SECONDS)); // 1

        // 再次计时
        stopwatch.start();
        Thread.sleep(100);
        System.out.println(stopwatch.elapsed(TimeUnit.SECONDS)); // 2
        // 重置并开始
        stopwatch.reset().start();
        Thread.sleep(1030);

        // 检查是否运行
        System.out.println(stopwatch.isRunning()); // true
        long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS); // 1034
        System.out.println(millis);
        // 打印
        System.out.println(stopwatch.toString()); // 1.034 s
    }


    public static void main(String[] args) throws InterruptedException {
        // 创建自动start的计时器
        Stopwatch watch = Stopwatch.createStarted();
        Thread.sleep(1000L);
        long time = watch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("代码执行时长：" + time);
        watch.reset();
        watch.start();
        Thread.sleep(500L);
        time = watch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("代码执行时长：" + time);

        System.out.println(watch);
    }
}

