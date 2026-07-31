package career_Navigator_parent.coding.mapper;

import career_Navigator_parent.coding.dto.response.ProblemTestCaseResponses.AdminTestCase;
import career_Navigator_parent.coding.entity.ProblemTestCase;
import org.springframework.stereotype.Component;

@Component
public class ProblemTestCaseMapper {

    public AdminTestCase toAdminResponse(
            ProblemTestCase testCase
    ) {

        if (testCase == null) {
            return null;
        }

        return new AdminTestCase(
                testCase.getId(),
                testCase.getProblem().getId(),
                testCase.getInput(),
                testCase.getExpectedOutput(),
                testCase.getVisibility(),
                testCase.getDisplayOrder(),
                testCase.getScoreWeight(),
                testCase.getCustomTimeLimitSeconds(),
                testCase.getCustomMemoryLimitMegabytes(),
                testCase.getGeneratedCase(),
                testCase.getGeneratorSeed(),
                testCase.getActive(),
                testCase.getCreatedAt(),
                testCase.getUpdatedAt()
        );
    }
}