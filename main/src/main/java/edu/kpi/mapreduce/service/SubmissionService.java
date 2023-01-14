package edu.kpi.mapreduce.service;

import edu.kpi.mapreduce.dto.ProblemDto;
import edu.kpi.mapreduce.dto.TaskSolutionDto;
import edu.kpi.mapreduce.entity.Problem;
import edu.kpi.mapreduce.entity.Stage;
import edu.kpi.mapreduce.entity.StageType;
import edu.kpi.mapreduce.entity.Task;
import edu.kpi.mapreduce.repository.ProblemRepository;
import edu.kpi.mapreduce.repository.StageRepository;
import edu.kpi.mapreduce.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class SubmissionService {


    private final ProblemRepository problemRepository;
    private final StageRepository stageRepository;
    private final TaskRepository taskRepository;
    private final ExecutionQueueService executionQueueService;
    private final int parallelism;

    public SubmissionService(final ProblemRepository problemRepository, final StageRepository stageRepository, final TaskRepository taskRepository,
                             final ExecutionQueueService executionQueueService, @Value("${parallelism.level}") final int parallelism) {
        this.problemRepository = problemRepository;
        this.stageRepository = stageRepository;
        this.taskRepository = taskRepository;
        this.executionQueueService = executionQueueService;
        this.parallelism = parallelism;
    }

    public Long submitProblem(final ProblemDto problemDto) {

        final var problem = new Problem();
        problemRepository.save(problem);

        final var stages = createStages(problem, problemDto);
        final var firstStage = stages.get(0);

        problem.setStages(stages);
        problemRepository.save(problem);

        scheduleTasks(firstStage, parseInput(problemDto.getInput()));

        return problem.getId();
    }

    private List<String> parseInput(final String input) {

        return Arrays.stream(input.split("\n"))
                .collect(Collectors.toList());
    }

    private int calculateQuantityOfChunks(final List<String> input) {

        return (int) Math.ceil((input.size() / ((double) parallelism)));
    }

    private void scheduleTasks(final Stage firstStage, final List<String> input) {

        createChunks(input, calculateQuantityOfChunks(input))
                .map(chunk -> createTask(firstStage, chunk))
                .forEach(executionQueueService::schedule);
    }

    private List<Stage> createStages(final Problem problem, final ProblemDto problemDto) {

        return Stream.concat(createMainStages(problem, problemDto), createFinalStage(problem, problemDto))
                .collect(Collectors.toList());
    }

    private Stream<Stage> createMainStages(final Problem problem, final ProblemDto problemDto) {

        return problemDto.getStages().stream()
                .map(stageDto ->
                        createStage(problem, stageDto.getType(), stageDto.getFunction(), stageDto.getReduceInitialIdentity()));
    }

    private Stream<Stage> createFinalStage(final Problem problem, final ProblemDto problemDto) {

        final var lastStage = problemDto.getStages().get(problemDto.getStages().size() - 1);

        return Stream.of(createStage(problem, StageType.REDUCE, lastStage.getFunction(), lastStage.getReduceInitialIdentity()));
    }

    private Stage createStage(final Problem problem, final StageType type, final String function, final String reduceInitialIdentity) {

        final var stage = new Stage();

        stage.setType(type);
        stage.setImplementation(function);
        stage.setProblem(problem);
        stage.setReduceInitialIdentity(reduceInitialIdentity);

        stageRepository.save(stage);

        return stage;
    }

    private Task createTask(final Stage stage, final List<String> input) {

        final var task = new Task();
        task.setInput(input);
        task.setStage(stage);
        taskRepository.save(task);

        return task;
    }

    private Stream<List<String>> createChunks(final List<String> source, final int length) {

        if (length <= 0)
            throw new IllegalArgumentException("length = " + length);

        return Optional.of(source.size())
                .filter(size -> size > 0)
                .map(size -> createChunks(source, length, size))
                .orElseGet(Stream::empty);
    }

    private Stream<List<String>> createChunks(final List<String> source, final int length, final int size) {

        final int fullChunks = (size - 1) / length;

        return IntStream.range(0, fullChunks + 1)
                .mapToObj(n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
    }

    public synchronized void solveTask(final TaskSolutionDto solution) {

        taskRepository.findById(solution.getId())
                .ifPresent(task -> solveTask(task, solution));
    }

    private void solveTask(final Task task, final TaskSolutionDto solution) {

        updateTask(task, solution.getResult());

        final var stage = task.getStage();
        final var problem = stage.getProblem();

        final int indexOfStage = problem.getStages().indexOf(stage);

        if (isLastStage(problem, indexOfStage)) {

            solveProblem(problem, solution.getResult());

        } else if (isLastMainStage(problem, indexOfStage)) {

            completeLastMainStage(problem, stage, indexOfStage);

        } else {

            completeTask(problem, solution.getResult(), indexOfStage);
        }
    }

    private void updateTask(final Task task, final List<String> output) {

        task.setSolved(Boolean.TRUE);
        task.setOutput(output);
        taskRepository.save(task);
    }

    private void solveProblem(final Problem problem, final List<String> output) {

        problem.setSolved(Boolean.TRUE);
        problem.setResult(output.get(0));
        problemRepository.save(problem);
    }

    private void completeLastMainStage(final Problem problem, final Stage stage, final int indexOfStage) {

        if (isAllTasksOfStageCompleted(stage)) {

            final var nextStage = getNext(problem.getStages(), indexOfStage);

            final List<String> input = stage.getTasks().stream()
                    .map(Task::getOutput)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            final var finalTask = new Task();
            finalTask.setStage(nextStage);
            finalTask.setInput(input);

            taskRepository.save(finalTask);
            executionQueueService.schedule(finalTask);
        }
    }

    private void completeTask(final Problem problem, final List<String> result, final int indexOfStage) {

        final var nextStage = getNext(problem.getStages(), indexOfStage);

        final var nextTask = new Task();
        nextTask.setStage(nextStage);
        nextTask.setInput(result);

        taskRepository.save(nextTask);

        executionQueueService.schedule(nextTask);
    }

    private boolean isLastStage(final Problem problem, final int indexOfStage) {

        return indexOfStage == problem.getStages().size() - 1;
    }

    private boolean isLastMainStage(final Problem problem, final int indexOfStage) {

        return indexOfStage == problem.getStages().size() - 2;
    }

    private boolean isAllTasksOfStageCompleted(final Stage stage) {

        return stage.getTasks()
                .stream()
                .allMatch(Task::isSolved);
    }

    private <T> T getNext(final List<T> input, final int index) {

        return input.get(index + 1);
    }

    public String getResult(final Long id) {

        return problemRepository.findById(id)
                .map(this::getResult)
                .orElse("There is bo problem with such id: " + id);
    }

    private String getResult(final Problem problem) {

        return Optional.of(problem)
                .filter(Problem::isSolved)
                .map(Problem::getResult)
                .orElse("In progress...");
    }
}
