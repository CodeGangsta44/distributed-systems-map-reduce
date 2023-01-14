package edu.kpi.mapreduce.controller;

import edu.kpi.mapreduce.dto.TaskDto;
import edu.kpi.mapreduce.dto.TaskSolutionDto;
import edu.kpi.mapreduce.entity.Task;
import edu.kpi.mapreduce.service.ExecutionQueueService;
import edu.kpi.mapreduce.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final ExecutionQueueService executionQueueService;
    private final SubmissionService submissionService;

    public TaskController(final ExecutionQueueService executionQueueService, final SubmissionService submissionService) {

        this.executionQueueService = executionQueueService;
        this.submissionService = submissionService;
    }

    @GetMapping
    public ResponseEntity<TaskDto> getTask() {

        try {

            return ResponseEntity.ok(convert(executionQueueService.getTask()));

        } catch (final NoSuchElementException e) {

            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping
    public void solve(@RequestBody final TaskSolutionDto solution) {

        submissionService.solveTask(solution);
    }

    private TaskDto convert(final Task task) {

        return TaskDto.builder()
                .id(task.getId())
                .type(task.getStage().getType())
                .input(task.getInput())
                .function(task.getStage().getImplementation())
                .reduceInitialIdentity(task.getStage().getReduceInitialIdentity())
                .build();
    }
}
