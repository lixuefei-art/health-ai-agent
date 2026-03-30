package com.lxf.healthaiagent.app;

import com.lxf.healthaiagent.advisor.MyLoggerAdvisor;
import com.lxf.healthaiagent.chatmemory.FileBasedChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class HealthApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演健康顾问，帮助用户解决健康问题。给出意见和方案。不超过5条建议";

    /**
     * 构造器生成基于内存会话记忆的ChatClient
     * @param dashscopeChatModel 千问大模型
     */
    public HealthApp(ChatModel dashscopeChatModel){
//        InMemoryChatMemory inMemoryChatMemory = new InMemoryChatMemory();
        String fileDir = System.getProperty("user.dir") + "/chat_memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        // 基于文件存储的会话记忆
                        new MessageChatMemoryAdvisor(chatMemory),
                        // 自定义日志拦截器
                        new MyLoggerAdvisor()
                        // 更多自定义advisor
                )
                .build();
    }

    /**
     * 基于内存的对话记忆
     * @param message 用户输入的文本
     * @param chatId 会话id
     * @return 对话结果
     */
    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        return content;
    }

    record HealthReport(String title, List<String> suggestions ) {
    }

    public HealthReport doChatWithReport(String message, String chatId) {
        // 调用doChat方法获取对话结果
        HealthReport healthReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "，并以JSON格式保存到文件")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(HealthReport.class);
        return healthReport;
    }

    /**
     * 基于本地rag的知识检索对话
     */
//    @Resource
//    private VectorStore healthAppVectorStore;

    @Resource
    private Advisor healthAppRagCloudAdvisor;

    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // 应用知识库问答
                .advisors(healthAppRagCloudAdvisor)
                .call()
                .chatResponse();
        assert chatResponse != null;
        String content = chatResponse.getResult().getOutput().getText();
        return content;
    }

    /**
     * 工具调用
     */
    @Resource
    private ToolCallback[] allTools;

    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        return content;
    }




    /**
     * MCP 工具调用
     */
    @Resource
    private ToolCallbackProvider mcpToolCallbackProvider;

    public String doChatWithMcp(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .tools(mcpToolCallbackProvider)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        return content;
    }



}
