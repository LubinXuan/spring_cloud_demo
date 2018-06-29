package me.robin.cloud;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "ip-white-list")
public class WhiteIpListFilter implements ContainerRequestFilter {

    private List<String> ips = new ArrayList<>();

    private boolean enable = false;

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {

        if (!enable) {
            return containerRequest;
        }

        ServletRequest servletRequest = RequestFilter.getServletRequest();

        if (!ips.contains(servletRequest.getRemoteAddr())) {
            //throw new RuntimeException("无权访问");
            HttpServletResponse response = (HttpServletResponse) RequestFilter.getServletResponse();
            try {
                response.sendError(403, "无权访问该资源:" + containerRequest.getMethod() + " " + containerRequest.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return containerRequest;
    }

    public List<String> getIps() {
        return ips;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
