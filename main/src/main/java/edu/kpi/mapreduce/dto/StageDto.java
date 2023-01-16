package edu.kpi.mapreduce.dto;

import edu.kpi.mapreduce.entity.StageType;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageDto {

    @NotEmpty(message = "Function cannot be empty")
    private String function;
    private StageType type;
    @NotEmpty(message = "Reduce initial identity cannot be empty")
    private String reduceInitialIdentity;
}
