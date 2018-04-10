package com.allen.core;

import com.alibaba.fastjson.JSON;
import lombok.Data;

@Data
public class JSONMessage {

    private String username;

    private String msgId;

    private JSON data;

}
