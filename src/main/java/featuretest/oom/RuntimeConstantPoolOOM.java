package featuretest.oom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YangFan on 2016/10/31 下午3:01.
 * <p/>
 * <p>
 * VM参数-XX:PermSize=10M -XX:MaxPermSize=10M
 * <p>
 * 对于JDK 1.6 HotSpot而言，方法区=永久代，这里看到OutOfMemoryError的区域是“PermGen space”，即永久代，那其实也就是方法区溢出了
 * <p>
 * JDK7这个例子会一直循环，因为JDK 7里String.intern生成的String不再是在perm gen分配,而是在Java Heap中分配
 * JDK8移除了永久代（Permanent Generation ），替换成了元空间（Metaspace）内存分配模型
 * 设置虚拟机参数-XX:MaxMetaspaceSize=1m，可出现OutOfMemoryError: Metaspace 溢出
 */
public class RuntimeConstantPoolOOM {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        int i = 0;
        while (true)
            list.add(String.valueOf(i++).intern());
    }
}
