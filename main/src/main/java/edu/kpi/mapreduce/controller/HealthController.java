package edu.kpi.mapreduce.controller;

import edu.kpi.mapreduce.service.HealthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static edu.kpi.mapreduce.constant.MapReduceConstants.WORKER_ID_HEADER_NAME;

@RestController
@RequestMapping("/health")
public class HealthController {

    private final HealthService healthService;

    public HealthController(final HealthService healthService) {

        this.healthService = healthService;
    }

    @PostMapping
    public void ping(@RequestHeader(WORKER_ID_HEADER_NAME) final String guid) {

        System.out.println("Received health ping from " + guid);
        healthService.ping(guid);
    }
}
