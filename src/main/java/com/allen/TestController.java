package com.allen;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/push")
public class TestController {

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/test")
    @ResponseBody
    public ResponseData test(@RequestParam("username") String username) throws Exception {
        JSONObject pushDTO = new JSONObject();
        pushDTO.put("username", username);
        JSONObject data = new JSONObject();
        data.put("orderId", "132574685486");
        data.put("status", 8);
        pushDTO.put("data", data);
        redisTemplate.convertAndSend("push-topic", pushDTO);
        return ResponseData.OK();
    }

}
