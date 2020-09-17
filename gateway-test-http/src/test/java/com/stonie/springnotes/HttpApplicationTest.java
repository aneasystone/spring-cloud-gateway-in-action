package com.stonie.springnotes;

import org.springframework.web.client.RestTemplate;

public class HttpApplicationTest {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        String responseBody = restTemplate.getForObject("http://localhost:8080", String.class);
        System.out.println(responseBody);
    }
}
