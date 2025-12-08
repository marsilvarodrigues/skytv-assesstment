package com.skytv.assetment.users.interception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.UUID;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String requestId = Optional.ofNullable(request.getRequestId())
                .orElse(UUID.randomUUID().toString());
        String url = request.getRequestURI();

        MDC.put("requestId", requestId);
        MDC.put("url", url);

        request.getParameterMap().forEach((k,v)-> MDC.put(k,v[0]));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.clear();
    }
}
