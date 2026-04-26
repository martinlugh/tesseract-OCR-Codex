package com.example.ocrcloud.monitor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class TraceLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TraceLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String traceId = Optional.ofNullable(request.getHeader("X-Trace-Id"))
                .filter(s -> !s.isBlank())
                .orElse(UUID.randomUUID().toString().replace("-", ""));
        MDC.put("traceId", traceId);
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long cost = System.currentTimeMillis() - start;
            response.setHeader("X-Trace-Id", traceId);
            log.info("请求完成 method={} uri={} status={} costMs={}",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), cost);
            MDC.remove("traceId");
        }
    }
}
