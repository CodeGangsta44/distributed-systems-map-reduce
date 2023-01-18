package edu.kpi.mapreduce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto {

    private boolean completed;
    private String result;
    private Long executionDuration;
    private Long computationDuration;
    private Long overheadDuration;
}
