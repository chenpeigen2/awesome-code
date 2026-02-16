package 装饰者模式;

// 具体组件：发送最原始的文本消息
public class BaseMessage implements Message {
    @Override
    public void send(String content) {
        System.out.println("正在发送消息: " + content);
    }
}

