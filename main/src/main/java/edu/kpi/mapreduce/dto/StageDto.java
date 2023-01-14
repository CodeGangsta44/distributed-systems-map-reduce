package edu.kpi.mapreduce.dto;

import edu.kpi.mapreduce.entity.StageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageDto {

    private String function;
    private StageType type;
    private String reduceInitialIdentity;
}
