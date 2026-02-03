package iflow;

import cn.iflow.sdk.core.IFlowClient;
import cn.iflow.sdk.types.messages.*;
import reactor.core.publisher.Flux;

public class BasicExample {
    public static void main(String[] args) throws Exception {
        // SDK 自动处理：
        // 1. 检测 iFlow 是否已安装
        // 2. 启动 iFlow 进程（如果未运行）
        // 3. 查找可用端口并建立连接
        // 4. 退出时自动清理资源
        try (IFlowClient client = IFlowClient.create()) {
            client.connect().block();
            client.sendMessage("你好，iFlow！").block();

            // 使用 blockLast() 等待流完成，避免资源过早关闭
            client.receiveMessages()
                    .doOnNext(message -> {
                        if (message instanceof AssistantMessage) {
                            AssistantMessage assistantMsg = (AssistantMessage) message;
                            System.out.print(assistantMsg.getChunk().getText());
                        } else if (message instanceof TaskFinishMessage) {
                            System.out.println("\n对话完成");
                        }
                    })
                    .doOnError(error -> error.printStackTrace())
                    .doOnComplete(() -> System.out.println("流结束"))
                    .blockLast(); // 阻塞直到流完成
        }
    }
}
