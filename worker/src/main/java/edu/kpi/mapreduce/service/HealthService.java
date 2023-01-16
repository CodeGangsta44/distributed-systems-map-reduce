package edu.kpi.mapreduce.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class HealthService {

    private final HealthIntegrationService healthIntegrationService;

    public HealthService(final HealthIntegrationService healthIntegrationService) {

        this.healthIntegrationService = healthIntegrationService;
    }

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    public void pingMain() {

        healthIntegrationService.pingMain();
    }
}
