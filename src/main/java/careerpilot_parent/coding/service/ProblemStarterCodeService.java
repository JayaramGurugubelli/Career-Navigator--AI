package careerpilot_parent.coding.service;

import careerpilot_parent.coding.dto.request.ProblemRequests.Starter;
import careerpilot_parent.coding.dto.response.CodingResponses.AdminStarter;
import careerpilot_parent.coding.enums.ProgrammingLanguage;

import java.util.List;

public interface ProblemStarterCodeService {

    AdminStarter create(
            Long problemId,
            Starter request
    );

    AdminStarter update(
            Long problemId,
            ProgrammingLanguage language,
            Starter request
    );

    AdminStarter get(
            Long problemId,
            ProgrammingLanguage language
    );

    List<AdminStarter> list(
            Long problemId,
            Boolean includeInactive
    );

    void delete(
            Long problemId,
            ProgrammingLanguage language
    );
}