package org.peter.asm.transform;

/**
 * 被增强代码在运行时调用的记录器。
 * <p>
 * 增强后的方法会插入 {@code invokestatic TimingRuntime.record(String, long)}。
 * 用静态入口而不是把 sink 对象塞进字节码，是为了让被增强的类不必知道任何
 * 额外的依赖类型——它只需要一个 {@code (String, long) → void} 的静态方法。
 *
 * <p>sink 是线程级的：同一个类的增强字节码可以被不同线程独立观测，
 * 互不干扰（测试并行跑也安全）。
 */
public final class TimingRuntime {

    private static final ThreadLocal<TimingTransformer.TimingSink> SINK = new ThreadLocal<>();

    private TimingRuntime() {
    }

    /** 为当前线程安装 sink。传 {@code null} 等价于 {@link #clear()}。 */
    public static void install(TimingTransformer.TimingSink sink) {
        if (sink == null) {
            SINK.remove();
        } else {
            SINK.set(sink);
        }
    }

    /** 移除当前线程的 sink。 */
    public static void clear() {
        SINK.remove();
    }

    /** 由被增强的字节码调用。没有安装 sink 时静默丢弃，避免影响业务逻辑。 */
    public static void record(String method, long nanos) {
        TimingTransformer.TimingSink sink = SINK.get();
        if (sink != null) {
            sink.accept(method, nanos);
        }
    }
}
