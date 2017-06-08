package me.robin.spring.cloud.controller;

import com.worken.url.shorter.vo.ComplexVO;
import me.robin.spring.cloud.service.MessageClient;
import me.robin.spring.cloud.service.UrlShortClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * Created by xuanlubin on 2017/4/7.
 */
@RestController
@RequestMapping("/compute")
public class ComputeController {

    @Resource
    private UrlShortClient urlShortClient;

    @Resource
    private MessageClient messageClient;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public Integer add(@RequestParam Integer a, @RequestParam Integer b) {
        return a + b;
    }

    @RequestMapping("/test")
    public String test() {
        return urlShortClient.shortIt("https://www.sendcloud.net/login.html?auth=1");
    }

    @RequestMapping("/test1")
    @ResponseBody
    public List<ComplexVO> test1() {
        return urlShortClient.listComplexVO();
    }

    @RequestMapping("/test2")
    @ResponseBody
    public ComplexVO test2() {
        return urlShortClient.complexVO();
    }

    @RequestMapping("/test3")
    @ResponseBody
    public ComplexVO test3() {
        ComplexVO vo = urlShortClient.complexVO();
        urlShortClient.showComplexVO(vo);
        return vo;
    }


    @RequestMapping("/test4")
    @ResponseBody
    public List<ComplexVO> test4() {
        List<ComplexVO> vos = urlShortClient.listComplexVO();
        urlShortClient.showComplexVOs(vos);
        return vos;
    }

    @RequestMapping("/test5")
    @ResponseBody
    public ComplexVO test5() {
        ComplexVO vo = urlShortClient.complexVO();
        urlShortClient.complexVO(vo.getName(), vo.getAge());
        return vo;
    }

    @RequestMapping("/test6")
    public long sendEmail(){
        return messageClient.sendEmail("LubinXuan_test_K9qkwf","lubin.xuan@qq.com","xuanlb2@grid.cn","系统管理员","邮件测试","邮件内容<a href=\"http://www.baidu.com\">点击</a>");
    }
}
