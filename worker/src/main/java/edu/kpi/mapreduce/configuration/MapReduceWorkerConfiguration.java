package edu.kpi.mapreduce.configuration;

import edu.kpi.mapreduce.interceptor.HeaderRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static edu.kpi.mapreduce.constant.WorkerConstants.WORKER_ID_HEADER_NAME;

@Configuration
public class MapReduceWorkerConfiguration {

    @Bean
    public RestTemplate restTemplate() {

        final var restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of(new HeaderRequestInterceptor(WORKER_ID_HEADER_NAME, UUID.randomUUID().toString())));

        return restTemplate;
    }
}
