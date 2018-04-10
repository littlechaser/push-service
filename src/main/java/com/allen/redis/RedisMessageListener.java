package com.allen.redis;

import com.allen.core.JSONMessage;
import com.allen.core.SpringContextHolder;
import com.allen.websocket.TextWebSocketHandler;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] bytes) {
        try {
            RedisTemplate redisTemplate = SpringContextHolder.getBean(RedisTemplate.class);
            JSONMessage msg = (JSONMessage)redisTemplate.getDefaultSerializer().deserialize(message.getBody());
            TextWebSocketHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
