package me.robin.cloud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "ip-white-list")
public class IpWhiteList {
    private List<String> ips;

    public void setIps(List<String> ips) {
        this.ips = ips;
    }

    public List<String> getIps() {
        return ips;
    }

    public boolean access(String ip) {
        return null != ips && ips.contains(ip);
    }
}
