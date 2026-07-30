package careerpilot_parent.coding.service;

import careerpilot_parent.coding.dto.request.ProblemImportRequests;
import careerpilot_parent.coding.dto.response.ProblemImportResponses;

public interface ProblemImportService {

    ProblemImportResponses.ValidationResult validateImport(
            ProblemImportRequests.ImportProblems request
    );

    ProblemImportResponses.ImportResult importProblems(
            ProblemImportRequests.ImportProblems request
    );

    ProblemImportResponses.ImportStatus getImportStatus(
            Long importId
    );
}