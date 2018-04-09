package com.allen;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.UnsupportedEncodingException;

/**
 * @author yang_tao@<yangtao.letzgo.com.cn>
 * @version 1.0
 * @date 2018-04-09 16:55
 */
public class FastJsonRedisConvertor implements RedisSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FastJsonRedisConvertor.class);

    @Override
    public byte[] serialize(Object data) throws SerializationException {
        if (data == null) {
            return new byte[0];
        }
        String jsonString = JSON.toJSONString(data);
        try {
            return jsonString.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(ExceptionStackTraceUtils.getStackTrace(e));
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        try {
            final String msg = new String(bytes, "UTF-8");
            return JSON.parse(msg);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(ExceptionStackTraceUtils.getStackTrace(e));
            return null;
        }
    }
}
