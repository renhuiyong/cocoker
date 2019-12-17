package com.cocoker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019-10-05 22:32
 * @Version: 1.0
 */
@Component
@ServerEndpoint("/webSocket")
@Slf4j
public class WebSocket {

    private Session session;

    private static CopyOnWriteArraySet<WebSocket> webSocketsSet = new CopyOnWriteArraySet<>();


//    private static ApplicationContext applicationContext;
//
//    public static void setApplicationContext(ApplicationContext applicationContext) {
//        WebSocket.applicationContext = applicationContext;
//    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketsSet.add(this);
        log.info("[websocket消息]，有新的连接，总数：{}", webSocketsSet.size());
    }

    @OnClose
    public void onClose() {
        webSocketsSet.remove(this);
        log.info("[websocket消息]，连接断开，总数：{}", webSocketsSet.size());
    }

    @OnMessage
    public void onMessage(String msg) {
        log.info("[websocket消息]，收到客户端发来消息：{}", msg);
    }

    public void sendMessage(String msg) {
        for (WebSocket webSocket : webSocketsSet) {
            log.info("[websocket消息]，广播消息：message = {}", msg);
            try {
                webSocket.session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
