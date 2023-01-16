package edu.kpi.mapreduce.service;

import edu.kpi.mapreduce.entity.Task;
import edu.kpi.mapreduce.entity.TaskState;
import edu.kpi.mapreduce.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class ExecutionQueueService {

    private final Queue<Task> queue = new LinkedBlockingQueue<>();
    private final TaskRepository taskRepository;
    private final HealthService healthService;

    public ExecutionQueueService(final TaskRepository taskRepository, final HealthService healthService) {

        this.taskRepository = taskRepository;
        this.healthService = healthService;
    }

    public void schedule(final Task task) {

        queue.offer(task);
    }

    public Task getTask(final String workerGuid) {

        final var task = queue.remove();

        task.setWorkerGuid(workerGuid);
        task.setState(TaskState.IN_PROGRESS);

        taskRepository.save(task);

        healthService.ping(workerGuid);

        return task;
    }
}
