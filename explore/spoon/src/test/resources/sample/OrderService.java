package sample;

/**
 * 第二个被解析的示例类，用来验证 Spoon 能处理多个文件、多个类。
 */
public class OrderService {

    public long total(Order order) {
        return order.total();
    }

    public Order find(String id) {
        Order order = new Order();
        return order;
    }
}
