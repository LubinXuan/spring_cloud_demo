package me.robin.spring.cloud.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Created by Lubin.Xuan on 2017-12-14.
 * {desc}
 */
public class HeaderFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes _attributes = (ServletRequestAttributes) attributes;
            String referer = _attributes.getRequest().getHeader("referer");
            if (StringUtils.isNotBlank(referer)) {
                RequestContext requestContext = RequestContext.getCurrentContext();
                requestContext.addZuulRequestHeader("referer", referer);
            }
        }
        return null;
    }
}
