package com.mess.mes_backend.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监听地址: ws://localhost:8080/ws/{userId}
 */
@ServerEndpoint("/ws/{userId}")
@Component
@Slf4j
public class WebSocketServer {

    // 静态变量，用来记录当前在线连接数。
    // ConcurrentHashMap: 线程安全的Map，用来存每个客户端对应的 WebSocketServer 对象
    private static final ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private String userId;

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        sessionMap.put(userId, session);
        log.info("用户上线: {}, 当前在线人数: {}", userId, sessionMap.size());
    }

    @OnClose
    public void onClose() {
        sessionMap.remove(this.userId);
        log.info("用户下线: {}", this.userId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送 (Unicast: 点对点)
     */
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送自定义消息给指定用户
     */
    public static void sendInfo(String message, String userId) {
        Session session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 广播消息 (Broadcast: 通知所有人)
     * 场景：任务解锁了，通知大家抢单！
     */
    public static void sendToAll(String message) {
        for (String key : sessionMap.keySet()) {
            try {
                sessionMap.get(key).getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
