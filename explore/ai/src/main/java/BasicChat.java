import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatThinking;

import java.util.Arrays;

void main() {
    ZhipuAiClient client = ZhipuAiClient.builder()
            .build();

    // 创建聊天完成请求
    ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
            .model("glm-5")
            .messages(Arrays.asList(
                    ChatMessage.builder()
                            .role(ChatMessageRole.USER.value())
                            .content("作为一名营销专家，请为我的产品创作一个吸引人的口号")
                            .build(),
                    ChatMessage.builder()
                            .role(ChatMessageRole.ASSISTANT.value())
                            .content("当然，要创作一个吸引人的口号，请告诉我一些关于您产品的信息")
                            .build(),
                    ChatMessage.builder()
                            .role(ChatMessageRole.USER.value())
                            .content("智谱AI开放平台")
                            .build()
            ))
            .thinking(ChatThinking.builder().type("enabled").build())
            .maxTokens(65536)
            .temperature(1.0f)
            .build();


    ChatCompletionResponse response = client.chat().createChatCompletion(request);

    if (response.isSuccess()) {
        Object reply = response.getData().getChoices().get(0).getMessage();
        System.out.println("AI 回复: " + reply);
    } else {
        System.err.println("错误: " + response.getMsg());
    }


}