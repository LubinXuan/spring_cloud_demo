package me.robin.spring.cloud.service;

import me.robin.spring.cloud.utils.UserResourceService;
import org.springframework.stereotype.Service;

/**
 * Created by Lubin.Xuan on 2017-09-25.
 * API资源权限判断拦截
 */
@Service
public class UserResourceServiceImpl implements UserResourceService {
    @Override
    public boolean hasAccessPermission(String user, String resource) {
        return false;
    }

    @Override
    public boolean hasAccessPermission(String user, String method, String resource) {
        return false;
    }
}
