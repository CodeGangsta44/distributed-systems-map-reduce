package edu.kpi.mapreduce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Task {

    private Long id;
    private TaskType type;
    private List<String> input;
    private String function;
    private String reduceInitialIdentity;
}
