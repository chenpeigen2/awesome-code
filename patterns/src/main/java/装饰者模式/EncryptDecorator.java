package 装饰者模式;

// 具体装饰者 B：对消息进行简单的“加密”（倒序）
public class EncryptDecorator extends MessageDecorator {
    public EncryptDecorator(Message message) {
        super(message);
    }

    @Override
    public void send(String content) {
        // 1. 修改内容（加密逻辑）
        String encrypted = new StringBuilder(content).reverse().toString();
        System.out.println("[加密包装器] 消息已倒序加密");
        // 2. 传递给下一层
        super.send(encrypted);
    }
}
