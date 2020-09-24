package com.stonie.springnotes;

import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class GatewayContext {

    public static final String CACHE_GATEWAY_CONTEXT = "cacheGatewayContext";

    /**
     * cache json body
     */
    protected String requestBody;
    /**
     * cache Response Body
     */
    protected Object responseBody;
    /**
     * request headers
     */
    protected HttpHeaders requestHeaders;
    /**
     * cache form data
     */
    protected MultiValueMap<String, String> formData;
    /**
     * cache all request data include:form data and query param
     */
    protected MultiValueMap<String, String> allRequestData = new LinkedMultiValueMap<>(0);

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    public HttpHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(HttpHeaders requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public MultiValueMap<String, String> getFormData() {
        return formData;
    }

    public void setFormData(MultiValueMap<String, String> formData) {
        this.formData = formData;
    }

    public MultiValueMap<String, String> getAllRequestData() {
        return allRequestData;
    }

    public void setAllRequestData(MultiValueMap<String, String> allRequestData) {
        this.allRequestData = allRequestData;
    }
}
