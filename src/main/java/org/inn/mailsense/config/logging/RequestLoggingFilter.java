package org.inn.mailsense.config.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID = "requestId";
    public static final String TRACE_ID = "traceId";
    public static final String USER_ID = "userId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String requestId = extractOrGenerate(request, "X-Request-ID");
        String traceId = extractOrGenerate(request, "X-B3-TraceId"); // optional header
        if (traceId == null) traceId = requestId;

        String userId = request.getHeader("X-User-Id");
        try {
            MDC.put(REQUEST_ID, requestId);
            MDC.put(TRACE_ID, traceId);
            if (userId != null) MDC.put(USER_ID, userId);
            // add to response header so clients can see the requestId
            response.setHeader("X-Request-ID", requestId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(REQUEST_ID);
            MDC.remove(TRACE_ID);
            MDC.remove(USER_ID);
        }

    }

    private String extractOrGenerate(HttpServletRequest request, String header) {
        String v = request.getHeader(header);
        if (v != null && !v.isBlank()) return v;
        return UUID.randomUUID().toString();
    }
}
