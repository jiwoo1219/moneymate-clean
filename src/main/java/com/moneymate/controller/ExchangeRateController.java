package com.moneymate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.moneymate.service.ExchangeRateService;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping("/rate/aud")
    public ResponseEntity<Map<String, Object>> getAudRate() {
        try {
            double rate = exchangeRateService.getAudToKrwRate();

            Map<String, Object> response = new HashMap<>();
            response.put("rate", rate);
            response.put("from", "AUD");
            response.put("to", "KRW");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "환율 정보를 가져올 수 없습니다.");
            return ResponseEntity.status(500).body(error);
        }
    }
}
