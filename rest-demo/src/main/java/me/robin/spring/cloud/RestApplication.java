package me.robin.spring.cloud;

import com.worken.common.utils.spring.CustomWebMvcConfigurerAdapter;
import com.worken.common.utils.spring.CustomWebMvcRegistrationsAdapter;
import com.worken.common.utils.spring.WrapResponseBodyAdvice;
import feign.*;
import lombok.extern.slf4j.Slf4j;
import me.robin.spring.cloud.feign.SpringFeignInvocationHandlerFactory;
import me.robin.spring.cloud.utils.security.MyMethodSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebMvcRegistrationsAdapter;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static feign.Util.checkState;

/**
 * Created by xuanlubin on 2017/4/7.
 */
@SpringBootApplication
//@EnableDiscoveryClient
@EnableFeignClients
@EnableWebSocket
//@EnableGlobalMethodSecurity(securedEnabled = true)
@Import(MyMethodSecurityConfig.class)
@Slf4j
public class RestApplication implements WebSocketConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }


    @Bean
    public Contract feignContract() {
        return new SpringMvcContract() {
            @Override
            public List<MethodMetadata> parseAndValidatateMetadata(Class<?> targetType) {
                Map<String, MethodMetadata> result = new LinkedHashMap<String, MethodMetadata>();
                for (Method method : targetType.getMethods()) {
                    if (method.getDeclaringClass() == Object.class ||
                            (method.getModifiers() & Modifier.STATIC) != 0 ||
                            Util.isDefault(method)) {
                        continue;
                    }
                    MethodMetadata metadata = parseAndValidateMetadata(targetType, method);
                    checkState(!result.containsKey(metadata.configKey()), "Overrides unsupported: %s",
                            metadata.configKey());
                    result.put(metadata.configKey(), metadata);
                }
                return new ArrayList<MethodMetadata>(result.values());
            }
        };
    }

    @Bean
    public InvocationHandlerFactory invocationHandlerFactory() {
        return new SpringFeignInvocationHandlerFactory();
    }

    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder(InvocationHandlerFactory invocationHandlerFactory) {
        return Feign.builder().invocationHandlerFactory(invocationHandlerFactory).retryer(Retryer.NEVER_RETRY);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new FeignRequestInterceptor();
    }

    @Bean
    public WebMvcRegistrationsAdapter webMvcRegistrationsAdapter() {
        return new CustomWebMvcRegistrationsAdapter();
    }

    @Bean
    public CustomWebMvcConfigurerAdapter webMvcConfigurationSupport() {
        return new CustomWebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                super.addInterceptors(registry);
            }
        };
    }

    @Bean
    public ResponseBodyAdvice responseBodyAdvice() {
        return new WrapResponseBodyAdvice(null);
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return container -> {
            HttpStatus[] globalErrorStatuses = new HttpStatus[]{HttpStatus.UNAUTHORIZED, HttpStatus.INTERNAL_SERVER_ERROR};
            for (HttpStatus status : globalErrorStatuses) {
                container.addErrorPages(new ErrorPage(status, "/global/error/" + status.value()));
            }
        };
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new AbstractWebSocketHandler() {

            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                super.afterConnectionEstablished(session);
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                log.info("接收到消息: {} {}  {}", this, session.getPrincipal().getName(), message.getPayload());
                TextMessage _message = new TextMessage("Get Message " + message.getPayload());
                session.sendMessage(_message);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                super.afterConnectionClosed(session, status);
            }
        }, "/socket");
    }
}
