package com.lxf.healthaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
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

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");

        // 测试文件操作：保存用户档案
        testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = app.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
        // 测试图片搜索 MCP
        String chatId = UUID.randomUUID().toString();
//        String message = "请帮我找到合肥市蜀山区振兴路附近2公里内的三家网吧";
        String message = "请帮我找到三张猫咪的图片";
        String answer =  app.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }


}