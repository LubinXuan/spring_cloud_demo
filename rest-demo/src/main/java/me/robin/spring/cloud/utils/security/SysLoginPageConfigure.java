package me.robin.spring.cloud.utils.security;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.DefaultLoginPageConfigurer;

/**
 * Created by Lubin.Xuan on 2017-09-27.
 * {desc}
 */
public class SysLoginPageConfigure<H extends HttpSecurityBuilder<H>> extends AbstractHttpConfigurer<DefaultLoginPageConfigurer<H>, H> {

}
