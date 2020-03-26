package it.cnr.si.web;

import it.cnr.si.web.PrintThreadLocal;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

//@Component
public class PrintSpooleURLFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        String userName = httpRequest.getHeader("ds-utente");
        try {
            PrintThreadLocal.set(userName);
            filterChain.doFilter(servletRequest, servletResponse);
        }finally {
            PrintThreadLocal.unset();
        }


    }

}
