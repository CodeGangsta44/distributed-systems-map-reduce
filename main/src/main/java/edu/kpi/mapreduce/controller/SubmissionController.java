package edu.kpi.mapreduce.controller;

import edu.kpi.mapreduce.dto.ProblemDto;
import edu.kpi.mapreduce.dto.ResultDto;
import edu.kpi.mapreduce.repository.ProblemRepository;
import edu.kpi.mapreduce.service.SubmissionService;
import edu.kpi.mapreduce.validator.ProblemStagesValidator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final ProblemStagesValidator problemStagesValidator;
    private final ProblemRepository problemRepository;

    public SubmissionController(final SubmissionService submissionService, final ProblemStagesValidator problemStagesValidator, final ProblemRepository problemRepository) {

        this.submissionService = submissionService;
        this.problemStagesValidator = problemStagesValidator;
        this.problemRepository = problemRepository;
    }

    @PostMapping
    public Long submit(@RequestBody final ProblemDto problemDto) {

        problemStagesValidator.validate(problemDto);

        return submissionService.submitProblem(problemDto);
    }

    @GetMapping("/{id}")
    public ResultDto getResult(@PathVariable("id") final Long id) {

        return submissionService.getResult(id);
    }

    @DeleteMapping("/{id}")
    public void deleteProblem(@PathVariable("id") final Long id) {

        problemRepository.deleteById(id);
    }
}
