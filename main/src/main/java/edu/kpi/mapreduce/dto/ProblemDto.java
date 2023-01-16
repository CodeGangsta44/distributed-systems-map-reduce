package edu.kpi.mapreduce.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemDto {

    private List<StageDto> stages;
    @NotEmpty(message = "Input cannot be empty")
    private String input;

}
