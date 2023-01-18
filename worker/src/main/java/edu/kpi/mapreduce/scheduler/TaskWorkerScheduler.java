package edu.kpi.mapreduce.scheduler;

import edu.kpi.mapreduce.dto.Task;
import edu.kpi.mapreduce.service.TaskIntegrationService;
import edu.kpi.mapreduce.worker.TaskWorker;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TaskWorkerScheduler {

    private final ExecutorService executorService = Executors.newWorkStealingPool();
    private final TaskIntegrationService taskIntegrationService;

    public TaskWorkerScheduler(final TaskIntegrationService taskIntegrationService) {

        this.taskIntegrationService = taskIntegrationService;
    }

    @Scheduled(fixedDelay = 1)
    public void scheduleTask() {

        final Date taskStart = new Date();
        taskIntegrationService.getTask()
                .ifPresent(task -> this.scheduleTask(task, taskStart));
    }

    @SneakyThrows
    private void scheduleTask(final Task task, final Date taskStart) {

        System.out.println("Starting work on task: " + task.toString());
        executorService.submit(new TaskWorker(taskIntegrationService, task, taskStart)).get();
    }
}
