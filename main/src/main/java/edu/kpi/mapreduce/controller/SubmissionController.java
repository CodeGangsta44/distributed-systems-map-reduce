package edu.kpi.mapreduce.controller;

import edu.kpi.mapreduce.dto.ProblemDto;
import edu.kpi.mapreduce.service.SubmissionService;
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

    public SubmissionController(final SubmissionService submissionService) {

        this.submissionService = submissionService;
    }

    @PostMapping
    public Long submit(@RequestBody final ProblemDto problemDto) {

        return submissionService.submitProblem(problemDto);
    }

    @GetMapping("/{id}")
    public String getResult(@PathVariable("id") final Long id) {

        return submissionService.getResult(id);
    }
}
