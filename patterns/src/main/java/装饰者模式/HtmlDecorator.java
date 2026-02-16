package 装饰者模式;

public class HtmlDecorator extends MessageDecorator {
    public HtmlDecorator(Message message) {
        super(message);
    }

    @Override
    public void send(String content) {
        // 1. 增强内容
        String htmlContent = "<html><body>" + content + "</body></html>";
        System.out.println("[HTML包装器] 已添加HTML标签");
        // 2. 调用父类或直接调用 wrappedMessage，将增强后的内容传下去
        super.send(htmlContent);
    }
}
