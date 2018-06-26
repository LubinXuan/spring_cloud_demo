package me.robin.cloud.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.servlet.ModelAndView;
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
@EnableConfigurationProperties(AccessTokenProperties.class)
public class ConfigApplication extends WebMvcConfigurerAdapter implements ApplicationListener<ContextRefreshedEvent> {

    private static final ThreadLocal<String> tokenLocal = new ThreadLocal<>();

    @Resource
    private IpWhiteList ipWhiteList;

    @Resource
    private ConfigClientProperties clientProperties;

    @Resource
    private AccessTokenProperties accessTokenProperties;

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
                    tokenLocal.set(token);
                    return true;
                } else {
                    tokenLocal.remove();
                }


                String ip = request.getRemoteAddr();
                if (ipWhiteList.access(ip)) {
                    return super.preHandle(request, response, handler);
                } else {
                    throw new RuntimeException("无权访问");
                }
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                tokenLocal.remove();
            }
        });
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        /*EnvironmentController environmentController = event.getApplicationContext().getBean(EnvironmentController.class);
        Field repositoryField = ReflectionUtils.findField(EnvironmentController.class, "repository");
        if (null != repositoryField) {
            repositoryField.setAccessible(true);
            EnvironmentRepository environmentRepository = (EnvironmentRepository) ReflectionUtils.getField(repositoryField, environmentController);
            EnvironmentRepository delegate = (application, profile, label) -> {
                String requireToken = accessTokenProperties.getToken(profile);
                if (StringUtils.equals(requireToken, tokenLocal.get())) {
                    return environmentRepository.findOne(application, profile, label);
                } else {
                    throw new RuntimeException("无权访问");
                }
            };
            ReflectionUtils.setField(repositoryField, environmentController, delegate);
            repositoryField.setAccessible(false);
        }*/
    }


}
