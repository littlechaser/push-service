package com.allen;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * @author yang_tao@<yangtao.letzgo.com.cn>
 * @version 1.0
 * @date 2018-04-09 14:51
 */
public class PushListener implements MessageListener {
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
