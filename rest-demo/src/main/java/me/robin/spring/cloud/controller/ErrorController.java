package me.robin.spring.cloud.controller;

import com.worken.common.Rsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Lubin.Xuan on 2017-09-26.
 * {desc}
 */
@Controller
@RequestMapping("/global/error")
@Slf4j
public class ErrorController extends AbstractErrorController {
    public ErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping(value = "/401", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Rsp<String> error401(HttpServletRequest request) {
        log.info("ContentType:{}", request.getContentType());
        Map<String, Object> body = getErrorAttributes(request, false);
        return Rsp.unauthorized(null, (String) body.get("message"));
    }

    @RequestMapping(value = "/401")
    public String error401html(HttpServletRequest request) {
        log.info("ContentType:{}", request.getContentType());
        return "redirect:/login?error";
    }

    @RequestMapping(value = "/500", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Rsp<String> error500(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request, false);
        return Rsp.error((String) body.get("message"));
    }

    @RequestMapping(value = "/500")
    public String error500html(HttpServletRequest request,RedirectAttributes attributes) {
        log.info("ContentType:{}", request.getContentType());
        return "redirect:/login?error";
    }

    @Override
    public String getErrorPath() {
        return "/global/error";
    }
}
