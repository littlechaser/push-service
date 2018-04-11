package com.allen.websocket;

import com.alibaba.fastjson.JSON;
import com.allen.core.BizDataException;
import com.allen.core.Constants;
import com.allen.core.ExceptionStackTraceUtils;
import com.allen.core.JSONMessage;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TextWebSocketHandler implements WebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextWebSocketHandler.class);

    @Getter
    private static Map<String, Session> sessionContainer = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        Map<String, Object> attributes = webSocketSession.getAttributes();
        final String username = (String) attributes.get(Constants.USER_NAME);
        if (StringUtils.isBlank(username)) {
            throw new Exception("用户名称不可为空");
        }
        Session session = sessionContainer.get(username);
        if (session != null) {
            sessionContainer.remove(username);
            session.close();
        }
        Session newSession = new Session(username, webSocketSession);
        sessionContainer.put(username, newSession);
        LOGGER.info("用户{}建立连接成功", username);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        Map<String, Object> attributes = webSocketSession.getAttributes();
        final String username = (String) attributes.get(Constants.USER_NAME);
        if (webSocketMessage instanceof PongMessage) {
            LOGGER.info("收到用户{}的心跳检测响应包", username);
            Session session = sessionContainer.get(username);
            if (session != null && session.isOpen()) {
                session.setClientLastReplyTime(new Date());
            } else {
                forceEvictSession(username);
            }
            return;
        }
        if (webSocketMessage instanceof PingMessage) {
            Session session = sessionContainer.get(username);
            if (session != null && session.isOpen()) {
                ByteBuffer byteBuffer = ByteBuffer.wrap("success".getBytes("UTF-8"));
                PongMessage pongMessage = new PongMessage(byteBuffer);
                webSocketSession.sendMessage(pongMessage);
                session.setServerLastReplyTime(new Date());
            } else {
                forceEvictSession(username);
            }
            return;
        }
        String message = JSON.toJSONString(webSocketMessage.getPayload());
        LOGGER.info("接收到{}发来的消息：{}", username, message);
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
//        LOGGER.error(ExceptionStackTraceUtils.getStackTrace(throwable));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        Map<String, Object> attributes = webSocketSession.getAttributes();
        final String username = (String) attributes.get(Constants.USER_NAME);
        forceEvictSession(username);
        LOGGER.info("用户{}断开连接", username);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public static void sendMessage(JSONMessage jsonMessage) throws Exception {
        String username = jsonMessage.getUsername();
        if (StringUtils.isBlank(username)) {
            throw new BizDataException("发送失败，发送目标不能为空");
        }
        if (jsonMessage == null || StringUtils.isBlank(JSON.toJSONString(jsonMessage))) {
            throw new BizDataException("发送失败，发送内容不能为空");
        }
        String msgId = jsonMessage.getMsgId();
        if (StringUtils.isBlank(msgId)) {
            throw new BizDataException("发送失败，消息格式有误");
        }
        TextMessage textMessage = new TextMessage(JSON.toJSONString(jsonMessage));
        if (Constants.ALL_USER.equals(username)) {
            Integer all = sessionContainer.size();
            Integer success = 0;
            Integer failure = 0;
            for (Map.Entry<String, Session> item : sessionContainer.entrySet()) {
                Session session = item.getValue();
                WebSocketSession webSocketSession = session.getWebSocketSession();
                if (webSocketSession.isOpen()) {
                    try {
                        webSocketSession.sendMessage(textMessage);
                        LOGGER.info("发送消息给{}成功", item.getKey());
                        success++;
                    } catch (Exception e) {
                        failure++;
                        LOGGER.error("发送给{}失败，{}", item.getKey(), ExceptionStackTraceUtils.getStackTrace(e));
                    }
                } else {
                    failure++;
                    LOGGER.error("发送给{}失败，{}", item.getKey(), "连接接已断开");
                }
            }
            LOGGER.info("总共应发送{}，成功{}，失败{}", all, success, failure);
        } else {
            final Session session = sessionContainer.get(username);
            if (session == null) {
                LOGGER.info("用户{}未连接", username);
                return;
            }
            WebSocketSession webSocketSession = session.getWebSocketSession();
            if (webSocketSession == null) {
                LOGGER.info("用户{}未连接", username);
                return;
            }
            if (webSocketSession.isOpen()) {
                webSocketSession.sendMessage(textMessage);
                LOGGER.info("发送消息给{}成功", username);
            } else {
                throw new BizDataException("用户" + username + "的连接已断开");
            }
        }
    }

    /**
     * 软逐出
     *
     * @param username
     */
    private static void softEvictSession(String username) {
        Session session = sessionContainer.get(username);
        if (session != null) {
            if (session.isOpen()) {
                long time = System.currentTimeMillis() - session.getClientLastReplyTime().getTime();
                if (time > 30 * 60 * 1000L) {
                    forceEvictSession(username);
                    LOGGER.info("已逐出用户{}的超时连接", username);
                }
            } else {
                forceEvictSession(username);
            }
        }
    }

    /**
     * 强制逐出
     *
     * @param username
     */
    private static void forceEvictSession(String username) {
        Session session = sessionContainer.get(username);
        if (session != null) {
            sessionContainer.remove(username);
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            LOGGER.info("已强制逐出用户{}的连接", username);
        }
    }

    public static void heartBeat() throws Exception {
        System.out.println(Thread.currentThread().getName());
        for (Map.Entry<String, Session> item : sessionContainer.entrySet()) {
            Session session = item.getValue();
            if (session != null) {
                softEvictSession(session.getUsername());
            }
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap("heartbeat".getBytes("UTF-8"));
        PingMessage pingMessage = new PingMessage(byteBuffer);
        for (Map.Entry<String, Session> item : sessionContainer.entrySet()) {
            Session session = item.getValue();
            if (session != null && session.isOpen()) {
                WebSocketSession webSocketSession = session.getWebSocketSession();
                webSocketSession.sendMessage(pingMessage);
            } else {
                forceEvictSession(session == null ? "" : session.getUsername());
            }
        }

    }

    @Getter
    public class Session implements Closeable {

        private String username;

        @Setter
        private Date clientLastReplyTime;

        @Setter
        private Date serverLastReplyTime;

        private WebSocketSession webSocketSession;

        public Session(String username, WebSocketSession webSocketSession) {
            if (StringUtils.isBlank(username)) {
                throw new IllegalArgumentException("连接用户不能为空");
            }
            if (webSocketSession == null || !webSocketSession.isOpen()) {
                throw new IllegalArgumentException("websocket连接为空或连接已关闭");
            }
            this.username = username;
            this.clientLastReplyTime = new Date();
            this.serverLastReplyTime = new Date();
            this.webSocketSession = webSocketSession;
        }

        @Override
        public void close() throws IOException {
            username = null;
            clientLastReplyTime = null;
            serverLastReplyTime = null;
            if (webSocketSession != null) {
                webSocketSession.close();
            }
        }

        public boolean isOpen() {
            return webSocketSession.isOpen();
        }

    }
}
