package edu.kpi.mapreduce.service;

import edu.kpi.mapreduce.entity.Task;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class ExecutionQueueService {

    private final Queue<Task> queue = new LinkedBlockingQueue<>();

    public void schedule(final Task task) {

        queue.offer(task);
    }

    public Task getTask() {

        return queue.remove();
    }
}
