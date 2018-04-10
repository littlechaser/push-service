package com.allen.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allen.core.ExceptionStackTraceUtils;
import com.allen.core.JSONMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class FastJsonRedisConvertor implements RedisSerializer<JSONMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FastJsonRedisConvertor.class);

    @Override
    public byte[] serialize(JSONMessage data) throws SerializationException {
        if (data == null) {
            return new byte[0];
        }
        data.setMsgId(getMsgId());
        String jsonString = JSON.toJSONString(data);
        try {
            return jsonString.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(ExceptionStackTraceUtils.getStackTrace(e));
            return null;
        }
    }

    @Override
    public JSONMessage deserialize(byte[] bytes) throws SerializationException {
        try {
            final String msg = new String(bytes, "UTF-8");
            return JSON.parseObject(msg, JSONMessage.class);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(ExceptionStackTraceUtils.getStackTrace(e));
            return null;
        }
    }

    private String getMsgId() {
        return String.valueOf(System.currentTimeMillis()) + getRandomStr(7);
    }

    private String getRandomStr(int len) {
        if (len <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
