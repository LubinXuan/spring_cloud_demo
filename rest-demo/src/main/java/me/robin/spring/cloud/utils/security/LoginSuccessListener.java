package me.robin.spring.cloud.utils.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;

/**
 * Created by Lubin.Xuan on 2017-09-26.
 * {desc}
 */
//@Component
@Slf4j
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        if (attributes instanceof ServletRequestAttributes) {
            Authentication authentication = event.getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            JwtTokenUtil.TokenData token = jwtTokenUtil.generateToken(userDetails);
            jwtTokenUtil.addAuthHeader(((ServletRequestAttributes) attributes).getResponse(), token.getToken());
            log.info("Add Auth Header For User {} {}", userDetails.getUsername(), token);
        }
    }
}
