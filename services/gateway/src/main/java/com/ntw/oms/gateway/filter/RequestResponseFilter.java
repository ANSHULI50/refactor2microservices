package com.ntw.oms.gateway.filter;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class RequestResponseFilter {

    public static final Logger logger = LoggerFactory.getLogger(RequestResponseFilter.class);

    @Bean("LoggingFilter")
    @Order(0)
    public GlobalFilter requestResponseFilter() {
        return (exchange, chain) -> {
            logger.info("Request filter: "+getRequestData(exchange.getRequest()));
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                logger.info("Response filter: "+getResponseData(exchange.getResponse(), exchange.getRequest()));
            }));
        };
    }

    private String getRequestData(ServerHttpRequest request) {
        Map<String, String> requestMap = new LinkedHashMap<>();
        Gson gson = new Gson();
        requestMap.put("URI", request.getURI().getPath());
        if (request.getURI().getQuery() != null && !request.getURI().getQuery().isEmpty()) {
            try {
                requestMap.put("Query", URLDecoder.decode(request.getURI().getQuery(), "UTF-8"));
            } catch(UnsupportedEncodingException e){};
        }
        Map<String, String> headerMap = request.getHeaders().toSingleValueMap();
        String authHeaderName = "Authorization";
        if (headerMap.get(authHeaderName) != null) {
            requestMap.put(authHeaderName, pruneAuthHeader(headerMap.get(authHeaderName)));
        }
        requestMap.put("Method", request.getMethod().toString());
        requestMap.put("Client", request.getRemoteAddress().toString());
        return gson.toJson(requestMap);
    }

    private String pruneAuthHeader(String authHeader) {
        if (authHeader == null || authHeader.isEmpty() || authHeader.length() < 50)
            return authHeader;
        return (new StringBuilder(authHeader.substring(0,20)).append("...")
                .append(authHeader.substring(authHeader.length()-10))).toString();
    }

    private String getResponseData(ServerHttpResponse response, ServerHttpRequest request) {
        Map<String, String> responseMap = new HashMap<>();
        Gson gson = new Gson();
        responseMap.put("Status Code", response.getStatusCode().toString());
        return gson.toJson(responseMap);
    }

}

