package com.stonie.springnotes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class RequestLogFilter implements GlobalFilter, Ordered {

    private Logger log = LoggerFactory.getLogger(RequestLogFilter.class);

    private static final String START_TIME = "startTime";

    private static final String HTTP_SCHEME = "http";

    private static final String HTTPS_SCHEME = "https";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI requestURI = request.getURI();
        String scheme = requestURI.getScheme();
        /*
         * not http or https scheme
         */
        if ((!HTTP_SCHEME.equalsIgnoreCase(scheme) && !HTTPS_SCHEME.equals(scheme))) {
            return chain.filter(exchange);
        }
        logRequest(exchange);
        return chain.filter(exchange).then(Mono.fromRunnable(()->logResponse(exchange)));
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE+2;
    }

    /**
     * log request
     * @param exchange
     */
    private void logRequest(ServerWebExchange exchange){
        ServerHttpRequest request = exchange.getRequest();
        URI requestURI = request.getURI();
        String scheme = requestURI.getScheme();
        HttpHeaders headers = request.getHeaders();
        long startTime = System.currentTimeMillis();
        exchange.getAttributes().put(START_TIME, startTime);
        log.info("[RequestLogFilter](Request)Start Timestamp:{}",startTime);
        log.info("[RequestLogFilter](Request)Scheme:{},Path:{}",scheme,requestURI.getPath());
        log.info("[RequestLogFilter](Request)Method:{},IP:{},Host:{}",request.getMethod(), GatewayUtils.getIpAddress(request),requestURI.getHost());
        headers.forEach((key,value)-> log.debug("[RequestLogFilter](Request)Headers:Key->{},Value->{}",key,value));
        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        if(!queryParams.isEmpty()){
            queryParams.forEach((key,value)-> log.info("[RequestLogFilter](Request)Query Param :Key->({}),Value->({})",key,value));
        }
        MediaType contentType = headers.getContentType();
        long length = headers.getContentLength();
        log.info("[RequestLogFilter](Request)ContentType:{},Content Length:{}",contentType,length);
        if(length>0 && null != contentType && (contentType.includes(MediaType.APPLICATION_JSON)
                ||contentType.includes(MediaType.APPLICATION_JSON_UTF8))){
            log.info("[RequestLogFilter](Request)JsonBody:{}",gatewayContext.getRequestBody());
        }
        if(length>0 && null != contentType  && contentType.includes(MediaType.APPLICATION_FORM_URLENCODED)){
            log.info("[RequestLogFilter](Request)FormData:{}",gatewayContext.getFormData());
        }
    }

    /**
     * log response exclude response body
     * @param exchange
     */
    private Mono<Void> logResponse(ServerWebExchange exchange){
        Long startTime = exchange.getAttribute(START_TIME);
        Long executeTime = (System.currentTimeMillis() - startTime);
        ServerHttpResponse response = exchange.getResponse();
        log.info("[RequestLogFilter](Response)HttpStatus:{}",response.getStatusCode());
        HttpHeaders headers = response.getHeaders();
        headers.forEach((key,value)-> log.debug("[RequestLogFilter]Headers:Key->{},Value->{}",key,value));
        MediaType contentType = headers.getContentType();
        long length = headers.getContentLength();
        log.info("[RequestLogFilter](Response)ContentType:{},Content Length:{}",contentType,length);
        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);
        log.info("[RequestLogFilter](Response)Response Body:{}",gatewayContext.getResponseBody());
        log.info("[RequestLogFilter](Response)Original Path:{},Cost:{} ms", exchange.getRequest().getURI().getPath(),executeTime);
        return Mono.empty();
    }
}
