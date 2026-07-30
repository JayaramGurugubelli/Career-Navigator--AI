package careerpilot_parent.coding.dto.response;

import careerpilot_parent.coding.enums.ProgrammingLanguage;

import java.time.LocalDateTime;

public final class ProblemStarterCodeResponses {

    private ProblemStarterCodeResponses() {
    }

    public record AdminStarterCode(

            Long id,

            Long problemId,

            ProgrammingLanguage language,

            String starterCode,

            String driverCode,

            String methodSignature,

            Boolean active,

            LocalDateTime createdAt,

            LocalDateTime updatedAt

    ) {
    }

    public record StudentStarterCode(

            Long id,

            ProgrammingLanguage language,

            String starterCode,

            String methodSignature

    ) {
    }
}