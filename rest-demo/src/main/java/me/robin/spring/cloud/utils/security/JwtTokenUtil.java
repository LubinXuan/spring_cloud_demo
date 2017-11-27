package me.robin.spring.cloud.utils.security;

import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

/**
 * Created by Lubin.Xuan on 2017-09-25.
 * {desc}
 */
@Component
public class JwtTokenUtil {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    public Jws<Claims> parse(String authToken) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(authToken);
    }

    public String getUsernameFromToken(Jws<Claims> jws) {
        return jws.getBody().getSubject();
    }

    public boolean validateToken(Jws<Claims> jws, UserDetails userDetails) {
        return true;
    }

    public TokenData generateToken(UserDetails details) {
        Calendar calendar = Calendar.getInstance();
        JwtBuilder builder = Jwts.builder();
        builder.claim("authorities", details.getAuthorities());
        builder.setIssuedAt(calendar.getTime());
        builder.setSubject(details.getUsername());
        calendar.add(Calendar.SECOND, expiration);
        Date expire = calendar.getTime();
        builder.setExpiration(expire)
                .signWith(SignatureAlgorithm.HS256, secret);
        String token = builder.compact();
        return new TokenData(token, expire);
    }

    public String getAuthToken(HttpServletRequest request) {
        String authHeader = request.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            return authHeader.substring(tokenHead.length());
        } else {
            if (null != request.getCookies()) {
                Optional<Cookie> cookie = Arrays.stream(request.getCookies()).filter(c -> StringUtils.equals("token", c.getName())).findAny();
                if (cookie.isPresent()) {
                    return cookie.get().getValue();
                }
            }
        }
        return null;
    }

    public void addAuthHeader(HttpServletResponse response, String token) {
        response.addHeader(tokenHeader, tokenHead + token);
    }

    @Getter
    @AllArgsConstructor
    public static class TokenData {
        private String token;
        private Date expire;
    }
}
