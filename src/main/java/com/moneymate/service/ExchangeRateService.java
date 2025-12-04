package com.moneymate.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExchangeRateService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public ExchangeRateService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    public double getAudToKrwRate() {
        try {
            // ExchangeRate-API (무료, 안정적)
            String url = "https://open.er-api.com/v6/latest/AUD";
            
            System.out.println("=== 환율 API 호출 ===");
            System.out.println("URL: " + url);
            
            String response = restTemplate.getForObject(url, String.class);
            
            // JSON 파싱
            JsonNode root = objectMapper.readTree(response);
            double rate = root.path("rates").path("KRW").asDouble();
            
            System.out.println("=== 환율 데이터 ===");
            System.out.println("AUD/KRW 환율: " + rate);
            
            return rate;
            
        } catch (Exception e) {
            System.err.println("=== 환율 조회 실패 ===");
            e.printStackTrace();
            return 900.0; // 실패시 기본값
        }
    }
}
