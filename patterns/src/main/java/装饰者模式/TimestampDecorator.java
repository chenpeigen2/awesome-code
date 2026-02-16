package 装饰者模式;

// 具体装饰者 C：在消息前加上时间戳
public class TimestampDecorator extends MessageDecorator {
    public TimestampDecorator(Message message) {
        super(message);
    }

    @Override
    public void send(String content) {
        // 1. 修改内容
        String timestampedContent = "[" + System.currentTimeMillis() + "] " + content;
        System.out.println("[时间戳包装器] 已添加时间戳");
        // 2. 传递给下一层
        super.send(timestampedContent);
    }
}
