package edu.kpi.mapreduce.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HealthIntegrationService {

    private final String healthServiceUrl;
    private final RestTemplate restTemplate;

    public HealthIntegrationService(@Value("${health.service.url}") final String healthServiceUrl,
                                    final RestTemplate restTemplate) {

        this.healthServiceUrl = healthServiceUrl;
        this.restTemplate = restTemplate;
    }

    public void pingMain() {

        restTemplate.postForLocation(healthServiceUrl, null);
    }
}
