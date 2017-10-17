package me.robin.spring.cloud.utils.security;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by Lubin.Xuan on 2017-09-26.
 * 登陆成功写出登录结果至服务端
 */
public class SysAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private JwtTokenUtil jwtTokenUtil;

    public SysAuthenticationSuccessHandler(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        JwtTokenUtil.TokenData tokenData = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
        String redirectUrl = request.getParameter("redirectUrl");
        if (StringUtils.isNotBlank(redirectUrl)) {
            String attach = "token=" + URLEncoder.encode(tokenData.getToken(), "utf-8") + "&expire=" + tokenData.getExpire().getTime();
            String targetUrl;
            if (StringUtils.contains(redirectUrl, "?")) {
                targetUrl = redirectUrl + "&" + attach;
            } else {
                targetUrl = redirectUrl + "?" + attach;
            }
            if (redirectUrl.startsWith("http://") || redirectUrl.startsWith("https://")) {
                if (StringUtils.contains(redirectUrl, "?")) {
                    response.sendRedirect(redirectUrl + "&" + attach);
                } else {
                    response.sendRedirect(redirectUrl + "?" + attach);
                }
            } else {
                RequestDispatcher dispatcher = request.getRequestDispatcher(targetUrl);
                dispatcher.forward(request, response);
            }
        } else {
            String callback = request.getParameter("callback");
            if (StringUtils.isNotBlank(callback)) {
                response.setContentType("application/javascript;charset=utf-8;");
                response.getWriter().write(StringEscapeUtils.escapeHtml4(callback) + "(" + JSON.toJSONString(tokenData) + ");");
            } else {
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.getWriter().write(JSON.toJSONString(tokenData));
            }
        }
    }
}
