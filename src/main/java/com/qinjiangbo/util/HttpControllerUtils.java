package com.qinjiangbo.util;

import java.util.HashMap;
import java.util.Map;

public class HttpControllerUtils {

    /**
     * 公共的方法返回结果消息
     *
     * @param code 状态码
     * @param msg  消息
     * @return
     */
    public static Map<String, Object> createReturn(int code, String msg) {
        Map<String, Object> mv = new HashMap<>();
        mv.put("rtnCode", code);
        mv.put("rtnMsg", msg);
        return mv;
    }

}
