package 装饰者模式.demo;

import 装饰者模式.*;

public class DecoratorDemo {
    public static void main(String[] args) {
        String msg = "Hello World";

        System.out.println("=== 场景 1: 发送普通消息 ===");
        Message simple = new BaseMessage();
        simple.send(msg);

        System.out.println("\n=== 场景 2: 发送 HTML 消息 ===");
        // 类似于 Java IO: new BufferedInputStream(new FileInputStream(...))
        Message htmlMsg = new HtmlDecorator(new BaseMessage());
        htmlMsg.send(msg);

        System.out.println("\n=== 场景 3: 发送加密的 HTML 消息 (类似 Java IO 的层层包装) ===");
        // 装饰顺序：先加密，再包装HTML，最后发送
        // 注意观察输出顺序，理解“洋葱模型”
        Message encryptedHtml = new HtmlDecorator(new EncryptDecorator(new BaseMessage()));
        encryptedHtml.send(msg);

        System.out.println("\n=== 场景 4: 发送带时间戳的加密消息 ===");
        Message complexMsg = new TimestampDecorator(new EncryptDecorator(new BaseMessage()));
        complexMsg.send(msg);
    }
}

