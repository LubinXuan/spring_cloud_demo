package me.robin.spring.cloud.controller;

import me.robin.spring.cloud.utils.SecuredMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by xuanlubin on 2017/4/7.
 */
@RestController
@SecuredMapping(path = {"/c"})
public class ComputeController {

    @SecuredMapping(value = "/add/{a}/{b}", method = RequestMethod.GET, secured = false)
    public Integer add(@PathVariable("a") Integer a, @PathVariable("b") Integer b) {
        return a + b;
    }

    @RequestMapping(value = "/add1/{a}/{b}", method = RequestMethod.GET)
    public Integer add1(@PathVariable("a") Integer a, @PathVariable("b") Integer b) {
        return a + b;
    }

    @RequestMapping(value = "/add2/{a}/{b}", method = RequestMethod.GET)
    public Integer add2(@PathVariable("a") Integer a, @PathVariable("b") Integer b) {
        return a + b;
    }

    @RequestMapping("/test6")
    public void sendEmail() {
        //return messageClient.sendEmail("LubinXuan_test_K9qkwf","lubin.xuan@qq.com","xuanlb2@grid.cn","系统管理员","邮件测试","邮件内容<a href=\"http://www.baidu.com\">点击</a>");
    }
}
