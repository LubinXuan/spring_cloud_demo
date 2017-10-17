package me.robin.spring.cloud.service;

import me.robin.spring.cloud.utils.security.SysUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by Lubin.Xuan on 2017-09-25.
 * {desc}
 */
@Service("sysUserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = new SysUser();
        sysUser.setUsername(username);
        sysUser.setSysId(username);
        sysUser.setEncodedPassword(DigestUtils.md5Hex(sysUser.getUsername() + "#" + sysUser.getSysId() + "#" + 123456));
        return sysUser;
    }
}
