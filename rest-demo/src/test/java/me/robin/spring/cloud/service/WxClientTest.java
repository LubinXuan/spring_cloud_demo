package me.robin.spring.cloud.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import me.robin.spring.BaseTest;
import me.robin.spring.cloud.utils.security.JwtTokenUtil;
import me.robin.spring.cloud.utils.security.SysUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017-06-15.
 */
@Slf4j
public class WxClientTest extends BaseTest {

    @Resource
    WxClient wxClient;

    @Resource
    JwtTokenUtil jwtTokenUtil;

    @Test
    public void testApi() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 6);
        Date start = calendar.getTime();

        calendar.set(Calendar.MONTH, 10);
        Date end = calendar.getTime();


        System.out.println();
    }


    @Test
    private void testJwt() {
        SysUser sysUser = new SysUser();
        sysUser.setSysId("1");
        sysUser.setUsername("user");
        sysUser.setEncodedPassword(DigestUtils.md5Hex("aaaaa"));

        JwtTokenUtil.TokenData token = jwtTokenUtil.generateToken(sysUser);

        log.info("token:{}", token);

        Jws<Claims> jws = jwtTokenUtil.parse(token.getToken());

        log.info("user:{}",jwtTokenUtil.getUsernameFromToken(jws));

        log.info("valid:{}",jwtTokenUtil.validateToken(jws,sysUser));
    }
}