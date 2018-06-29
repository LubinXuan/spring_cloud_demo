package me.robin.cloud;

import com.netflix.eureka.resources.ApplicationResource;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IpResourceFilterFactory implements ResourceFilterFactory {

    private static final Map<Class, Collection<String>> filterResources = new ConcurrentHashMap<>();

    static {
        filterResources.put(ApplicationResource.class, Collections.singletonList("addInstance"));
    }

    private List<ResourceFilter> resourceFilters = new ArrayList<>();

    @Resource
    private WhiteIpListFilter whiteIpListFilter;

    @PostConstruct
    private void init() {
        resourceFilters.add(new ResourceFilter() {
            @Override
            public ContainerRequestFilter getRequestFilter() {
                return whiteIpListFilter;
            }

            @Override
            public ContainerResponseFilter getResponseFilter() {
                return null;
            }
        });
    }

    @Override
    public List<ResourceFilter> create(AbstractMethod am) {
        boolean filterMethod = filterResources.getOrDefault(am.getResource().getResourceClass(), Collections.emptySet()).contains(am.getMethod().getName());
        if (filterMethod) {
            return Collections.unmodifiableList(resourceFilters);
        } else {
            return Collections.emptyList();
        }
    }
}
