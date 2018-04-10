package com.allen.redis;

import com.alibaba.fastjson.JSONObject;
import com.allen.websocket.TextWebSocketHandler;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

public class RedisMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] bytes) {
        byte[] body = message.getBody();
        try {
            String str = new String(body, "UTF-8");
            JSONObject jsonObject = JSONObject.parseObject(str);
            TextWebSocketHandler.sendMessage(jsonObject.getString("username"), jsonObject.get("data"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
