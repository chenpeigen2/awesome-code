package 装饰者模式;

// 抽象装饰者：持有对一个 Message 对象的引用，并将请求转发给它
public abstract class MessageDecorator implements Message {
    protected Message wrappedMessage;

    public MessageDecorator(Message message) {
        this.wrappedMessage = message;
    }

    @Override
    public void send(String content) {
        // 默认行为是直接调用被包装对象的 send 方法
        // 具体的装饰者会在调用前后做手脚
        wrappedMessage.send(content);
    }
}


