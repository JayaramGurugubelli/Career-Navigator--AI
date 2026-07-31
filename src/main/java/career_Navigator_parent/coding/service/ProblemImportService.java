package career_Navigator_parent.coding.service;

import career_Navigator_parent.coding.dto.request.ProblemImportRequests;
import career_Navigator_parent.coding.dto.response.ProblemImportResponses;

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