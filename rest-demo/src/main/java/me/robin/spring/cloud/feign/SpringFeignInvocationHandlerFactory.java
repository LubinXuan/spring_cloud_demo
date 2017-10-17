package me.robin.spring.cloud.feign;

import feign.InvocationHandlerFactory;
import feign.Target;
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Lubin.Xuan on 2017-08-23.
 * {desc}
 */
public class SpringFeignInvocationHandlerFactory implements InvocationHandlerFactory {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    private static final ThreadLocal<Map<String, SimpleDateFormat>> format_cache = ThreadLocal.withInitial(HashMap::new);

    private Map<Method, Map<Integer, String>> dateFormatCache = new ConcurrentHashMap<>();

    @Override
    public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
        return new SpringFeignInvocationHandler(target, dispatch);
    }

    private class SpringFeignInvocationHandler implements InvocationHandler {

        private final Target target;
        private final Map<Method, MethodHandler> dispatch;


        public SpringFeignInvocationHandler(Target target, Map<Method, MethodHandler> dispatch) {
            this.target = target;
            this.dispatch = dispatch;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("equals".equals(method.getName())) {
                try {
                    Object otherHandler = args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                    return equals(otherHandler);
                } catch (IllegalArgumentException e) {
                    return false;
                }
            } else if ("hashCode".equals(method.getName())) {
                return hashCode();
            } else if ("toString".equals(method.getName())) {
                return toString();
            }

            if (args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Date) {
                        String format = findArgFormat(method, i);
                        SimpleDateFormat sdf = format_cache.get().computeIfAbsent(format, SimpleDateFormat::new);
                        args[i] = sdf.format(args[i]);
                    }
                }
            }

            return dispatch.get(method).invoke(args);
        }

        private String findArgFormat(Method method, int argIdx) {
            return dateFormatCache.computeIfAbsent(method, m -> new ConcurrentHashMap<>()).computeIfAbsent(argIdx, i -> {
                Annotation[] annotations = method.getParameterAnnotations()[i];
                for (Annotation annotation : annotations) {
                    if (annotation instanceof DateTimeFormat) {
                        return ((DateTimeFormat) annotation).pattern();
                    }
                }
                return DEFAULT_DATE_FORMAT;
            });

        }


        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SpringFeignInvocationHandler) {
                SpringFeignInvocationHandler other = (SpringFeignInvocationHandler) obj;
                return target.equals(other.target);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public String toString() {
            return target.toString();
        }
    }
}
