package me.robin.spring.cloud.utils.security;

import lombok.extern.slf4j.Slf4j;
import me.robin.spring.cloud.utils.SecuredMapping;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.AbstractFallbackMethodSecurityMetadataSource;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Lubin.Xuan on 2017-09-22.
 * {desc}
 */
@Slf4j
public class RequestMappingAbstractMethodSecurityMetadataSource extends AbstractFallbackMethodSecurityMetadataSource {

    private static final String[] EMPTY_PATH = new String[0];

    private final Set<String> requestMappingClassName = new HashSet<>();

    private MvcApplicationStartListener mvcApplicationStartListener;

    public RequestMappingAbstractMethodSecurityMetadataSource(MvcApplicationStartListener mvcApplicationStartListener) {
        this.mvcApplicationStartListener = mvcApplicationStartListener;
        requestMappingClassName.add(SecuredMapping.class.getName());
        requestMappingClassName.add(RequestMapping.class.getName());
        requestMappingClassName.add("org.springframework.web.bind.annotation.PostMapping");
        requestMappingClassName.add("org.springframework.web.bind.annotation.GetMapping");
        requestMappingClassName.add("org.springframework.web.bind.annotation.DeleteMapping");
        requestMappingClassName.add("org.springframework.web.bind.annotation.PutMapping");
        requestMappingClassName.add("org.springframework.web.bind.annotation.PatchMapping");
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Method method, Class<?> targetClass) {
        SecuredMapping clazzSecure = AnnotationUtils.findAnnotation(targetClass, SecuredMapping.class);
        SecuredMapping mapping = AnnotationUtils.findAnnotation(method, SecuredMapping.class);
        if (null != mapping && mapping.secured()) {
            Annotation parent = findInterestAnnotation(targetClass);
            return createSecureConfig(parent, mapping);
        } else if (null != clazzSecure && clazzSecure.secured()) {
            Annotation child = findInterestAnnotation(method);
            //method must be a rest resource
            if (null != child) {
                if (child instanceof SecuredMapping) {
                    if (((SecuredMapping) child).secured()) {
                        return createSecureConfig(clazzSecure, child);
                    }
                } else {
                    return createSecureConfig(clazzSecure, child);
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Class<?> clazz) {
        return Collections.emptyList();
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    private List<ConfigAttribute> createSecureConfig(Annotation parent, Annotation child) {
        List<ConfigAttribute> attributeList = new ArrayList<>();
        String parentPathArr[] = pathArr(parent);
        String childPathArr[] = pathArr(child);
        if (parentPathArr.length < 1 && childPathArr.length > 0) {
            for (String path : childPathArr) {
                attributeList.add(new RestResAttribute(joinPath("", path)));
            }
        } else if (parentPathArr.length > 0 && childPathArr.length < 1) {
            for (String path : parentPathArr) {
                attributeList.add(new RestResAttribute(joinPath("", path)));
            }
        } else if (parentPathArr.length > 0 && childPathArr.length > 0) {
            for (String parentPath : parentPathArr) {
                for (String path : childPathArr) {
                    attributeList.add(new RestResAttribute(joinPath(parentPath, path)));
                }
            }
        }
        if (!attributeList.isEmpty()) {
            mvcApplicationStartListener.addSecuredRes(attributeList);
        }
        return attributeList;
    }

    private String joinPath(String path, String childPath) {
        if (StringUtils.isNotBlank(childPath)) {
            path += (childPath.startsWith("/") ? childPath : ("/" + childPath));
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
        }
        return path;
    }


    private String[] pathArr(Annotation annotation) {
        if (null == annotation) {
            return EMPTY_PATH;
        }
        AnnotationAttributes attributes = AnnotationUtils.getAnnotationAttributes(annotation, false, true);
        String[] paths = attributes.getStringArray("path");
        if (null == paths) {
            return EMPTY_PATH;
        } else {
            return paths;
        }
    }

    private Annotation findInterestAnnotation(Class object) {
        Annotation annotation = AnnotationUtils.findAnnotation(object, SecuredMapping.class);
        if (null == annotation) {
            annotation = AnnotationUtils.findAnnotation(object, RequestMapping.class);
        }
        return annotation;
    }

    private Annotation findInterestAnnotation(Method method) {
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (requestMappingClassName.contains(annotation.annotationType().getName())) {
                return annotation;
            }
        }
        return null;
    }

    public static class RestResAttribute implements ConfigAttribute {

        private String restRes;

        RestResAttribute(String restRes) {
            this.restRes = restRes;
        }

        @Override
        public String getAttribute() {
            return restRes;
        }

        @Override
        public String toString() {
            return restRes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RestResAttribute that = (RestResAttribute) o;

            return restRes != null ? restRes.equals(that.restRes) : that.restRes == null;
        }

        @Override
        public int hashCode() {
            return restRes != null ? restRes.hashCode() : 0;
        }
    }
}
