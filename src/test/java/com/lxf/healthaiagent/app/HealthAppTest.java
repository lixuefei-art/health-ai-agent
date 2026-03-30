package com.lxf.healthaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HealthAppTest {

    @Resource
    private HealthApp app;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        String result = app.doChat("我感冒了，怎么办？", chatId);
        assertNotNull(result);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        HealthApp.HealthReport healthReport = app.doChatWithReport("我感冒了，怎么办？", chatId);
        assertNotNull(healthReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String result = app.doChatWithRag("经常头晕是怎么回事", chatId);
        assertNotNull(result);
    }
}