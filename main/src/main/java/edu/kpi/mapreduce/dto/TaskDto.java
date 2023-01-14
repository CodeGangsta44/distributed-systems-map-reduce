package edu.kpi.mapreduce.dto;

import edu.kpi.mapreduce.entity.StageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    private Long id;
    private StageType type;
    private List<String> input;
    private String function;
    private String reduceInitialIdentity;
}
