package me.robin.spring.cloud.utils.security;

import me.robin.spring.cloud.utils.filter.JwtTokenVerifyFilter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.authentication.UserDetailsServiceLdapAuthoritiesPopulator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * Created by Lubin.Xuan on 2017-09-25.
 * {desc}
 */
@Configuration
public class SysWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private AuthenticationProvider authenticationProvider;
    @Resource
    private AuthenticationProvider ldapAuthenticationProvider;
    @Resource
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Resource
    private JwtTokenUtil jwtTokenUtil;

    public SysWebSecurityConfigurerAdapter() {
        super(false);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
        //auth.authenticationProvider(ldapAuthenticationProvider);
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                ).permitAll()
                // 对于获取token的rest api要允许匿名访问
                .antMatchers("/auth/**","/global/error/**","/tasks/**").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();
        // 禁用缓存
        http.headers().cacheControl();
        http.formLogin().successHandler(authenticationSuccessHandler);//.and().httpBasic();
        http.addFilterBefore(jwtTokenVerifyFilter(userDetailsService, jwtTokenUtil), UsernamePasswordAuthenticationFilter.class);
    }

    //@Bean
    private JwtTokenVerifyFilter jwtTokenVerifyFilter(UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) throws Exception {
        JwtTokenVerifyFilter filter = new JwtTokenVerifyFilter(userDetailsService, jwtTokenUtil);
        //filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Bean("authenticationProvider")
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setSaltSource(saltSource());
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean("ldapAuthenticationProvider")
    public LdapAuthenticationProvider ldapAuthenticationProvider(LdapAuthenticator ldapAuthenticator, LdapAuthoritiesPopulator ldapAuthoritiesPopulator, UserDetailsContextMapper userDetailsContextMapper) {
        LdapAuthenticationProvider authenticationProvider = new LdapAuthenticationProvider(ldapAuthenticator, ldapAuthoritiesPopulator);
        authenticationProvider.setUserDetailsContextMapper(userDetailsContextMapper);
        return authenticationProvider;
    }

    @Bean
    public LdapAuthenticator ldapAuthenticator(BaseLdapPathContextSource ldapPathContextSource, LdapUserSearch ldapUserSearch) {
        BindAuthenticator authenticator = new BindAuthenticator(ldapPathContextSource);
        authenticator.setUserSearch(ldapUserSearch);
        return authenticator;
    }

    @Bean
    public LdapAuthoritiesPopulator ldapAuthoritiesPopulator() {
        return new UserDetailsServiceLdapAuthoritiesPopulator(userDetailsService);
    }

    @Bean
    public LdapUserSearch ldapUserSearch(BaseLdapPathContextSource ldapPathContextSource) {
        return new FilterBasedLdapUserSearch("dc=worken,dc=com", "(uid={0})", ldapPathContextSource);
    }

    @Bean
    public UserDetailsContextMapper userDetailsContextMapper() {
        return new LdapUserDetailsMapper();
    }

    @Bean
    public BaseLdapPathContextSource ldapPathContextSource() {
        String ldapUrl = "ldap://127.0.0.1:10389";
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(ldapUrl);
        contextSource.setUserDn("uid=manager,dc=worken,dc=com");
        contextSource.setPassword("123456");
        return contextSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encodePassword(String rawPass, Object salt) {
                return DigestUtils.md5Hex(salt + rawPass);
            }

            @Override
            public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
                return StringUtils.equals(encPass, DigestUtils.md5Hex(salt + rawPass));
            }
        };
    }

    @Bean
    public SaltSource saltSource() {
        return new SysSaltSource();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(JwtTokenUtil jwtTokenUtil) {
        return new SysAuthenticationSuccessHandler(jwtTokenUtil);
    }
}
