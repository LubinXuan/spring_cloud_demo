package me.robin.cloud;

import javax.servlet.*;
import java.io.IOException;

public class RequestFilter implements Filter {

    private static final ThreadLocal<ServletRequest> REQUEST_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<ServletResponse> RESPONSE_THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            REQUEST_THREAD_LOCAL.set(request);
            RESPONSE_THREAD_LOCAL.set(response);
            chain.doFilter(request, response);
        } finally {
            REQUEST_THREAD_LOCAL.remove();
            RESPONSE_THREAD_LOCAL.remove();
        }
    }

    @Override
    public void destroy() {

    }

    public static ServletRequest getServletRequest() {
        return REQUEST_THREAD_LOCAL.get();
    }


    public static ServletResponse getServletResponse() {
        return RESPONSE_THREAD_LOCAL.get();
    }


}
