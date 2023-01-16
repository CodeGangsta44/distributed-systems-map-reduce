package edu.kpi.mapreduce.service;

import edu.kpi.mapreduce.entity.Task;
import edu.kpi.mapreduce.entity.TaskState;
import edu.kpi.mapreduce.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TaskHealthService {

    private final long workerHealthThreshold;
    private final TaskRepository taskRepository;
    private final HealthService healthService;
    private final ExecutionQueueService executionQueueService;

    public TaskHealthService(@Value("${worker.health.threshold}") final long workerHealthThreshold,
                             final TaskRepository taskRepository, final HealthService healthService,
                             final ExecutionQueueService executionQueueService) {

        this.workerHealthThreshold = workerHealthThreshold;
        this.taskRepository = taskRepository;
        this.healthService = healthService;
        this.executionQueueService = executionQueueService;
    }

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    public void inspectTasks() {

        final List<Task> byState = taskRepository.findByState(TaskState.IN_PROGRESS);

        byState.stream()
                .collect(Collectors.groupingBy(Task::getWorkerGuid))
                .forEach(this::inspectTasks);
    }

    private void inspectTasks(final String workerGuid, final List<Task> tasks) {

        final boolean workerUnhealthy = healthService.getLastHealthyDate(workerGuid)
                .map(this::isDateOverdue)
                .orElse(Boolean.TRUE);

        System.out.println("Checked tasks for worker " + workerGuid + " - unhealthy: " + workerUnhealthy);

        if (workerUnhealthy) {

            tasks.forEach(task -> {
                task.setWorkerGuid(null);
                task.setState(TaskState.WAIT);
            });

            taskRepository.saveAll(tasks);

            tasks.forEach(executionQueueService::schedule);

            System.out.println("Rescheduled tasks: " + tasks);
        }
    }

    private boolean isDateOverdue(final Date healthyDate) {

        final long diffInMilliseconds = Math.abs(new Date().getTime() - healthyDate.getTime());
        final long diff = TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MINUTES);

        return diff > workerHealthThreshold;
    }
}
