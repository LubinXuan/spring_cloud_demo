package me.robin.spring.cloud.utils.security;

import me.robin.spring.cloud.utils.UserResourceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * Created by Lubin.Xuan on 2017-09-22.
 * 全局SpringSecurity配置
 */
@EnableGlobalMethodSecurity
@Configuration
public class MyMethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Resource
    private UserResourceService userResourceService;

    @Resource
    private MvcApplicationStartListener mvcApplicationStartListener;

    /**
     * 自定义资源元数据初始化
     *
     * @return
     */
    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
        return new RequestMappingAbstractMethodSecurityMetadataSource(mvcApplicationStartListener);
    }

    /**
     * 设置自定义投票器
     *
     * @return
     */
    @Override
    protected AccessDecisionManager accessDecisionManager() {
        return new AffirmativeBased(Collections.singletonList(new RestResAccessDecisionVoter(userResourceService)));
    }


    @Bean
    public MvcApplicationStartListener mvcApplicationStartListener(){
        return new MvcApplicationStartListener();
    }
}
