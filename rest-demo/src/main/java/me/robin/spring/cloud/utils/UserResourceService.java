package me.robin.spring.cloud.utils;

/**
 * Created by Lubin.Xuan on 2017-09-25.
 * API资源权限判断拦截
 */
public interface UserResourceService {
    boolean hasAccessPermission(String user, String resource);

    boolean hasAccessPermission(String user, String method, String resource);
}
