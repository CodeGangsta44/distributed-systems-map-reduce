package edu.kpi.mapreduce.validator;

import edu.kpi.mapreduce.dto.ProblemDto;
import edu.kpi.mapreduce.dto.StageDto;
import edu.kpi.mapreduce.entity.StageType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProblemStagesValidator {

    public void validate(final ProblemDto problem) {

        if (problem.getStages().isEmpty()) {

            throw new IllegalArgumentException("Problem should have at least one stage");
        }

        final List<StageDto> intermediateStages = problem.getStages().subList(0, problem.getStages().size() - 1);
        final StageDto lastStage = problem.getStages().get(problem.getStages().size() - 1);

        if (!isAllMapType(intermediateStages)) {

            throw new IllegalArgumentException("Problem cannot have intermediate REDUCE stages");
        }

        if (!StageType.REDUCE.equals(lastStage.getType())) {

            throw new IllegalArgumentException("Problem should have REDUCE last stage");
        }
    }

    private boolean isAllMapType(final List<StageDto> stages) {

        return stages.stream()
                .map(StageDto::getType)
                .allMatch(StageType.MAP::equals);
    }
}
