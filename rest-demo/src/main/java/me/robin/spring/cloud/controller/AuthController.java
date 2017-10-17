package me.robin.spring.cloud.controller;

import com.worken.common.Rsp;
import me.robin.spring.cloud.utils.security.JwtTokenUtil;
import me.robin.spring.cloud.utils.security.SysUser;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Lubin.Xuan on 2017-09-25.
 * {desc}
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private SaltSource saltSource;

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public Rsp<JwtTokenUtil.TokenData> auth(@RequestBody SysUser sysUser, HttpServletResponse response) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(sysUser.getUsername());
        Object salt = saltSource.getSalt(userDetails);
        if (passwordEncoder.isPasswordValid(userDetails.getPassword(), sysUser.getPassword(), salt)) {
            JwtTokenUtil.TokenData tokenData = jwtTokenUtil.generateToken(userDetails);
            jwtTokenUtil.addAuthHeader(response, tokenData.getToken());
            return Rsp.success(tokenData);
        } else {
            return Rsp.error("fail");
        }
    }
}
