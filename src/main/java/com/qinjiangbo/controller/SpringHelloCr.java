package com.qinjiangbo.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Richard on 8/21/16.
 */
@RestController
@RequestMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SpringHelloCr {

    @RequestMapping("/{name}")
    public String hello(@PathVariable String name) {
        return "Hello, " + name;
    }

}
