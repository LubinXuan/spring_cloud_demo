package me.robin.spring.cloud.utils.security;

import lombok.extern.slf4j.Slf4j;
import me.robin.spring.cloud.utils.UserResourceService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AbstractAclVoter;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collection;

/**
 * Created by Lubin.Xuan on 2017-09-25.
 * 用户API资源权限投票器
 */
@Slf4j
public class RestResAccessDecisionVoter extends AbstractAclVoter {

    private UserResourceService userResourceService;

    public RestResAccessDecisionVoter(UserResourceService userResourceService) {
        this.userResourceService = userResourceService;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute instanceof RequestMappingAbstractMethodSecurityMetadataSource.RestResAttribute;
    }

    @Override
    public int vote(Authentication authentication, MethodInvocation object, Collection<ConfigAttribute> attributes) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String bestMatchPath = (String) requestAttributes.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        String method = requestAttributes.getRequest().getMethod();
        boolean hasPermission = userResourceService.hasAccessPermission(authentication.getName(), method, bestMatchPath);
        if (hasPermission) {
            return ACCESS_GRANTED;
        } else {
            log.info("用户[{}]对资源[{} {}]没有访问权限", authentication.getName(), method, bestMatchPath);
            return ACCESS_DENIED;
        }
    }
}
