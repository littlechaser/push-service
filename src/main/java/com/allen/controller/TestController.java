package com.allen.controller;

import com.alibaba.fastjson.JSONObject;
import com.allen.core.ResponseData;
import com.allen.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class TestController {

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/push")
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

}
