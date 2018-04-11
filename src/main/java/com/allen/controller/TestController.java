package com.allen.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.allen.core.JSONMessage;
import com.allen.core.ResponseData;
import com.allen.dto.UserDTO;
import com.allen.websocket.TextWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/push")
    public ResponseData test(@RequestParam("username") String username) throws Exception {
        JSONMessage message = new JSONMessage();
        message.setUsername(username);
        JSONObject data = new JSONObject();
        data.put("orderId", "132574685486");
        data.put("status", 8);
        message.setData(data);
        redisTemplate.convertAndSend("push-topic", message);
        return ResponseData.OK();
    }

    /**
     * JSR 303 - Bean Validation
     */
    @RequestMapping(value = "/jsr303",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseData jsr303(@RequestBody @Valid UserDTO userDTO) {
        return ResponseData.OK(userDTO);
    }

    @RequestMapping(value = "/info")
    public ResponseData info() throws Exception {
        JSONArray jsonArray = new JSONArray();
        Map<String, TextWebSocketHandler.Session> sessionContainer = TextWebSocketHandler.getSessionContainer();
        sessionContainer.forEach((String username, TextWebSocketHandler.Session session) -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            if (session != null && session.isOpen()) {
                jsonObject.put("connected", true);
            } else {
                jsonObject.put("connected", false);
            }
            jsonArray.add(jsonObject);
        });
        return ResponseData.OK(jsonArray);
    }

}
