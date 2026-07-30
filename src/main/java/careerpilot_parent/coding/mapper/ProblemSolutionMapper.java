package careerpilot_parent.coding.mapper;

import careerpilot_parent.coding.dto.response.CodingResponses.AdminSolution;
import careerpilot_parent.coding.dto.response.CodingResponses.Solution;
import careerpilot_parent.coding.entity.ProblemSolution;
import org.springframework.stereotype.Component;

@Component
public class ProblemSolutionMapper {

    public AdminSolution toAdmin(
            ProblemSolution solution
    ) {

        return new AdminSolution(
                solution.getId(),
                solution.getProblem().getId(),
                solution.getProgrammingLanguage(),
                solution.getApproach(),
                solution.getTitle(),
                solution.getExplanation(),
                solution.getSourceCode(),
                solution.getTimeComplexity(),
                solution.getSpaceComplexity(),
                solution.getOfficial(),
                solution.getActive(),
                solution.getCreatedAt(),
                solution.getUpdatedAt()
        );
    }

    public Solution toStudent(
            ProblemSolution solution
    ) {

        return new Solution(
                solution.getId(),
                solution.getProgrammingLanguage(),
                solution.getApproach(),
                solution.getTitle(),
                solution.getExplanation(),
                solution.getSourceCode(),
                solution.getTimeComplexity(),
                solution.getSpaceComplexity(),
                solution.getOfficial()
        );
    }
}