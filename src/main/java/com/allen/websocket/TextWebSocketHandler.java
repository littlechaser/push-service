package com.allen.websocket;

import com.alibaba.fastjson.JSON;
import com.allen.core.BizDataException;
import com.allen.core.Constants;
import com.allen.core.ExceptionStackTraceUtils;
import com.allen.core.JSONMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TextWebSocketHandler implements WebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextWebSocketHandler.class);

    private static Map<String, WebSocketSession> webSocketSessionContainer = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        Map<String, Object> attributes = webSocketSession.getAttributes();
        final String username = (String) attributes.get(Constants.USER_NAME);
        if (StringUtils.isBlank(username)) {
            throw new Exception("用户名称不可为空");
        }
        WebSocketSession session = webSocketSessionContainer.get(username);
        if (session != null) {
            webSocketSessionContainer.remove(username);
            session.close();
        }
        webSocketSessionContainer.put(username, webSocketSession);
        LOGGER.info("用户{}建立连接成功", username);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        Map<String, Object> attributes = webSocketSession.getAttributes();
        System.out.println(attributes);
        String message = JSON.toJSONString(webSocketMessage.getPayload());
        TextMessage textMessage = new TextMessage(message);
        webSocketSession.sendMessage(textMessage);
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
//        LOGGER.error(ExceptionStackTraceUtils.getStackTrace(throwable));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        Map<String, Object> attributes = webSocketSession.getAttributes();
        final String username = (String) attributes.get(Constants.USER_NAME);
        webSocketSessionContainer.remove(username);
        webSocketSession.close();
        LOGGER.info("用户{}断开连接", username);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public static <T> void sendMessage(JSONMessage jsonMessage) throws Exception {
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
            Integer all = webSocketSessionContainer.size();
            Integer success = 0;
            Integer failure = 0;
            for (Map.Entry<String, WebSocketSession> item : webSocketSessionContainer.entrySet()) {
                WebSocketSession session = item.getValue();
                if (session.isOpen()) {
                    try {
                        session.sendMessage(textMessage);
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
            final WebSocketSession session = webSocketSessionContainer.get(username);
            if (session == null) {
                LOGGER.info("用户{}未连接", username);
                return;
            }
            if (session.isOpen()) {
                session.sendMessage(textMessage);
                LOGGER.info("发送消息给{}成功", username);
            } else {
                throw new BizDataException("用户" + username + "的连接已断开");
            }
        }
    }

}
