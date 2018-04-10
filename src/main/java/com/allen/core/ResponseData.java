package com.allen.core;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ResponseData<Data> {
    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;
    private static final int FAILURE = 417;
    private static final int ERROR = 500;

    private static final String OK_MSG = "成功";
    private static final String BAD_REQUEST_MSG = "错误的请求参数";
    private static final String FAILURE_MSG = "失败";
    private static final String ERROR_MSG = "系统错误";

    private static final JSONObject DEFAULT_BLANK_OBJECT = new JSONObject();

    private int code = 200;
    private String msg = "成功";
    private Data data;

    public ResponseData(int code, String msg, Data data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static ResponseData OK() {
        return new ResponseData<>(OK, OK_MSG, DEFAULT_BLANK_OBJECT);
    }

    public static ResponseData OK(Object data) {
        if (data == null) {
            return OK();
        }
        if (data instanceof String) {
            String tips = (String) data;
            Map<String, String> map = new HashMap<>();
            map.put("tips", tips);
            return new ResponseData<>(OK, OK_MSG, map);
        }
        return new ResponseData<>(OK, OK_MSG, data);
    }

    public static ResponseData OK(int code, String msg, Object data) {
        if (StringUtils.isBlank(msg)) {
            msg = OK_MSG;
        }
        if (data == null) {
            data = DEFAULT_BLANK_OBJECT;
        }
        if (data instanceof String) {
            String tips = (String) data;
            Map<String, String> map = new HashMap<>();
            map.put("tips", tips);
            return new ResponseData<>(OK, OK_MSG, map);
        }
        return new ResponseData<>(code, msg, data);
    }

    public static ResponseData BIZ_ERROR() {
        return new ResponseData<>(FAILURE, FAILURE_MSG, DEFAULT_BLANK_OBJECT);
    }

    public static ResponseData BIZ_ERROR(String msg) {
        return new ResponseData<>(FAILURE, msg, DEFAULT_BLANK_OBJECT);
    }

    public static ResponseData BIZ_ERROR(int code, String msg) {
        return new ResponseData<>(code, msg, DEFAULT_BLANK_OBJECT);
    }

    public static ResponseData SYSTEM_ERROR() {
        return new ResponseData<>(ERROR, ERROR_MSG, DEFAULT_BLANK_OBJECT);
    }


    public static ResponseData VALID_ERROR() {
        return new ResponseData<>(BAD_REQUEST, BAD_REQUEST_MSG, DEFAULT_BLANK_OBJECT);
    }

    public static ResponseData VALID_ERROR(String msg) {
        return new ResponseData<>(BAD_REQUEST, msg, DEFAULT_BLANK_OBJECT);
    }

    public static ResponseData VALID_ERROR(Map<String, String> map) {
        if (map == null) {
            return new ResponseData<>(BAD_REQUEST, BAD_REQUEST_MSG, DEFAULT_BLANK_OBJECT);
        }
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            return new ResponseData<>(BAD_REQUEST, entry.getValue(), DEFAULT_BLANK_OBJECT);
        }
        return new ResponseData<>(BAD_REQUEST, BAD_REQUEST_MSG, DEFAULT_BLANK_OBJECT);
    }

}
