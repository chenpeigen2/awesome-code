package sample;

import java.util.List;

/**
 * 被 Spoon 解析的示例类。
 * <p>
 * 刻意包含「一个基础类型字段、一个泛型字段、一个普通字段」以及
 * 「一个普通方法 + 一个被 @Deprecated 标记的方法」，
 * 好让测试能验证 Spoon 能读出字段类型、方法签名和注解。
 */
public class Order {

    private String id;
    private List<String> items;
    private long totalCents;

    public long total() {
        return totalCents;
    }

    @Deprecated
    public long legacyTotal() {
        return totalCents;
    }
}
