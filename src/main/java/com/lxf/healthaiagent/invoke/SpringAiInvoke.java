package com.lxf.healthaiagent.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SpringAiInvoke implements CommandLineRunner {


    @Resource
    private ChatModel chatModel;

    @Override
    public void run(String... args) throws Exception {
        String result = chatModel.call("你是谁");
        System.out.println(result);
    }
}
