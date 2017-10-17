package me.robin.spring.cloud.utils.security;

import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Lubin.Xuan on 2017-09-25.
 * {desc}
 */
public class SysSaltSource implements SaltSource {
    @Override
    public Object getSalt(UserDetails user) {
        if (user instanceof SysUser) {
            return user.getUsername() + "#" + ((SysUser) user).getSysId() + "#";
        }
        return null;
    }
}
