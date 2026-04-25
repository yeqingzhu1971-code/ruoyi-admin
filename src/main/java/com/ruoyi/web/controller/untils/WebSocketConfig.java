package com.ruoyi.web.controller.untils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 【大屏实时推送】WebSocket 核心配置类
 * 作用：开启并扫描所有带有 @ServerEndpoint 注解的 WebSocket 服务端类
 */
@Configuration
public class WebSocketConfig {

    /**
     * 注入 ServerEndpointExporter
     * 注意：因为我们用的是 Spring Boot 内嵌的 Tomcat，所以必须手动注入此 Bean。
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}