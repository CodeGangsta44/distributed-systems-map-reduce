package edu.kpi.mapreduce.service;

import edu.kpi.mapreduce.dto.Solution;
import edu.kpi.mapreduce.dto.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class TaskIntegrationService {

    private final String submissionServiceUrl;
    private final RestTemplate restTemplate;

    public TaskIntegrationService(@Value("${task.service.url}") final String submissionServiceUrl, final RestTemplate restTemplate) {

        this.submissionServiceUrl = submissionServiceUrl;
        this.restTemplate = restTemplate;
    }

    public Optional<Task> getTask() {

        final ResponseEntity<Task> entity = restTemplate.getForEntity(submissionServiceUrl, Task.class);

        return Optional.of(entity)
                .filter(this::isOk)
                .map(ResponseEntity::getBody);
    }

    public void solveTask(final Solution solution) {

        restTemplate.postForEntity(submissionServiceUrl, solution, Void.class);
    }

    private <T> boolean isOk(final ResponseEntity<T> response) {

        return 200 == response.getStatusCode().value();
    }
}
