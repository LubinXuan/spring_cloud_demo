package me.robin.cloud.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.context.encrypt.EncryptorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017-06-06.
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigApplication extends WebMvcConfigurerAdapter {

    @Resource
    private IpWhiteList ipWhiteList;

    @Resource
    private ConfigClientProperties clientProperties;

    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(new HandlerInterceptorAdapter() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

                String token = request.getHeader(ConfigClientProperties.TOKEN_HEADER);

                if (StringUtils.isNotBlank(token) && StringUtils.equals(token, clientProperties.getToken())) {
                    return true;
                }

                String ip = request.getRemoteAddr();
                if (ipWhiteList.access(ip)) {
                    return super.preHandle(request, response, handler);
                } else {
                    response.sendError(403, "无权访问资源");
                    return false;
                }
            }
        });
    }

}
