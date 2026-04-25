package com.ruoyi.web.controller.common;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// 🌟 注意：Spring Boot 3+ 必须使用 jakarta 包，而不是老的 javax
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 【大屏实时推送】WebSocket 服务端点
 * 作用：维护前端大屏的连接，并提供向大屏主动推送数据的方法
 *
 */
@Component
@ServerEndpoint("/websocket/bcg/screen/{clientId}")
public class BcgScreenWebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(BcgScreenWebSocketServer.class);

    // 🌟 核心：用来存放所有当前在线的大屏客户端连接（使用 ConcurrentHashMap 保证线程安全）
    // Key = clientId (前端生成的唯一标识), Value = 该客户端的 WebSocket Session
    private static final ConcurrentHashMap<String, Session> sessionPool = new ConcurrentHashMap<>();

    /**
     * 前端大屏连接成功时触发
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "clientId") String clientId) {
        sessionPool.put(clientId, session);
        log.info("【大屏 WebSocket】有新大屏建立连接！客户端ID: {}，当前在线大屏数: {}", clientId, sessionPool.size());
    }

    /**
     * 前端大屏断开连接时触发
     */
    @OnClose
    public void onClose(@PathParam(value = "clientId") String clientId) {
        sessionPool.remove(clientId);
        log.info("【大屏 WebSocket】大屏连接断开！客户端ID: {}，当前在线大屏数: {}", clientId, sessionPool.size());
    }

    /**
     * 收到前端大屏发来的消息时触发 (一般用于心跳包保活)
     */
    @OnMessage
    public void onMessage(String message) {
        log.debug("【大屏 WebSocket】收到前端消息: {}", message);
    }

    /**
     * 发生异常时触发
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("【大屏 WebSocket】连接发生错误: ", error);
    }

    // ==============================================================
    // 🌟 提供给 Service 层调用的核心推送方法
    // ==============================================================

    /**
     * 向所有在线的大屏【广播】最新检测数据 (群发)
     * @param messageObj 要推送的任意 Java 对象（传的是 BcgDashboardWsVo）
     */
    public static void broadcastMessage(Object messageObj) {
        if (sessionPool.isEmpty()) {
            return; // 如果当前没有任何人打开大屏网页，直接跳过推送，节省性能
        }

        // 将 Java 对象转化为 JSON 字符串
        String jsonMessage = JSON.toJSONString(messageObj);
        
        for (Session session : sessionPool.values()) {
            if (session != null && session.isOpen()) {
                try {
                    // 异步发送文本消息给前端大屏
                    session.getAsyncRemote().sendText(jsonMessage);
                } catch (Exception e) {
                    log.error("【大屏 WebSocket】向客户端发送消息失败", e);
                }
            }
        }
    }
}