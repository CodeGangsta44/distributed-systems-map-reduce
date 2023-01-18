package edu.kpi.mapreduce.worker;

import edu.kpi.mapreduce.dto.Solution;
import edu.kpi.mapreduce.dto.Task;
import edu.kpi.mapreduce.dto.TaskType;
import edu.kpi.mapreduce.service.TaskIntegrationService;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class TaskWorker implements Runnable {

    private final TaskIntegrationService taskIntegrationService;
    private final Task task;
    private final Date taskStart;
    private final Map<TaskType, BiFunction<Task, Invocable, List<String>>> handlers = Map.ofEntries(
            Map.entry(TaskType.MAP, this::executeMap),
            Map.entry(TaskType.REDUCE, this::executeReduce)
    );

    public TaskWorker(final TaskIntegrationService taskIntegrationService, final Task task, final Date taskStart) {

        this.taskIntegrationService = taskIntegrationService;
        this.task = task;
        this.taskStart = taskStart;
    }

    @Override
    public void run() {

        final var manager = new ScriptEngineManager();
        final var engine = manager.getEngineByName("js");

        final long start = System.currentTimeMillis();
        createFunction(engine, task.getFunction());

        final var invocable = (Invocable) engine;

        final List<String> result = handlers.get(task.getType())
                .apply(task, invocable);

        final Solution solution = Solution.builder()
                .id(task.getId())
                .result(result)
                .computationDuration(System.currentTimeMillis() - start)
                .taskStart(taskStart)
                .build();

        System.out.println("Sending solution: " + solution.toString());
        taskIntegrationService.solveTask(solution);
    }

    private List<String> executeMap(final Task task, final Invocable function) {

        return task.getInput().stream()
                .map(input -> map(function, input))
                .collect(Collectors.toList());
    }

    private List<String> executeReduce(final Task task, final Invocable function) {

        return Collections.singletonList(task.getInput().stream()
                .reduce(task.getReduceInitialIdentity(), (acc, el) -> reduce(function, acc, el)));
    }

    private void createFunction(final ScriptEngine engine, final String function) {

        try {

            engine.eval(function);

        } catch (final ScriptException e) {

            e.printStackTrace();
        }
    }

    private String map(final Invocable mapFunction, final Object argument) {

        try {

            return mapFunction.invokeFunction("map", argument).toString();

        } catch (final ScriptException | NoSuchMethodException e) {

            e.printStackTrace();
            return null;
        }
    }

    private String reduce(final Invocable reduceFunction, final Object acc, final Object element) {

        try {

            return reduceFunction.invokeFunction("reduce", acc, element).toString();

        } catch (final ScriptException | NoSuchMethodException e) {

            e.printStackTrace();
            return null;
        }
    }
}
