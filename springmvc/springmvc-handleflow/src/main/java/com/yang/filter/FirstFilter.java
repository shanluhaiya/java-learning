package com.yang.filter;

import javax.servlet.*;
import java.io.IOException;

public class FirstFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println(this.getClass().getName() + ": init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println(this.getClass().getName() + ": doFilter");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println(this.getClass().getName() + ": destroy");
    }
}
