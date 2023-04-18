package jdkversions.jdk21;

public class Main {
    public static void main(String[] args) {
//        // 记录使用线程池开始时间
//        long threadPoolStart = System.currentTimeMillis();
//        // 记录使用线程池时所有线程
//        List<String> threadPoolNameList;
//        // 线程池执行是累加，验证运行是否正确
//        AtomicInteger threadPoolCompute = new AtomicInteger();
//        try (var executor = Executors.newFixedThreadPool(16)) {
//            // 启动100个线程，每个线程sleep一秒。此处启动太多会导致长时间等待
//            IntStream.range(0, 100).forEach(i -> executor.submit(() -> {
//                try {
//                    Thread.sleep(Duration.ofSeconds(1));
//                    threadPoolCompute.incrementAndGet();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }));
//            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
//            threadPoolNameList = Arrays.stream(threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds()))
//                    .map(ThreadInfo::getThreadName)
//                    .collect(Collectors.toList());
//        }
//        long threadPoolEnd = System.currentTimeMillis();
//
//        // 记录使用虚拟线程开始时间
//        long virtualThreadStart = System.currentTimeMillis();
//        // 记录使用虚拟线程时所有线程
//        List<String> virtualThreadNameList;
//        // 虚拟线程执行是累加，验证运行是否正确
//        AtomicInteger virtualThreadCompute = new AtomicInteger();
//        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
//            IntStream.range(0, 10_000).forEach(i -> executor.submit(() -> {
//                try {
//                    Thread.sleep(Duration.ofSeconds(1));
//                    virtualThreadCompute.incrementAndGet();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }));
//            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
//            virtualThreadNameList = Arrays.stream(threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds()))
//                    .map(ThreadInfo::getThreadName)
//                    .collect(Collectors.toList());
//        }
//
//        System.out.println(String.format("threadPool: thread count = %s, cost time = %ss, compute = %s",
//                threadPoolNameList.size(),
//                (threadPoolEnd - threadPoolStart) / 1000,
//                threadPoolCompute.get()));
//        threadPoolNameList.forEach(System.out::println);
//        System.out.println(String.format("virtualThread: thread count = %s, cost time = %ss, compute = %s",
//                virtualThreadNameList.size(),
//                (System.currentTimeMillis() - virtualThreadStart) / 1000,
//                virtualThreadCompute.get()));
//        virtualThreadNameList.forEach(System.out::println);
    }
}
