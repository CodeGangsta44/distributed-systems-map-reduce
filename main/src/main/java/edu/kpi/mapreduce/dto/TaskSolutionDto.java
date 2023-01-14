package edu.kpi.mapreduce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskSolutionDto {

    private Long id;
    private List<String> result;
}
