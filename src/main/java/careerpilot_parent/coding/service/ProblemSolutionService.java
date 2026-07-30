package careerpilot_parent.coding.service;

import careerpilot_parent.coding.dto.request.ProblemSolutionRequests.Create;
import careerpilot_parent.coding.dto.request.ProblemSolutionRequests.EditorialUpsert;
import careerpilot_parent.coding.dto.request.ProblemSolutionRequests.Update;
import careerpilot_parent.coding.dto.response.CodingResponses.AdminSolution;

import java.util.List;

public interface ProblemSolutionService {

    AdminSolution create(
            Long problemId,
            Create request
    );

    AdminSolution update(
            Long problemId,
            Long solutionId,
            Update request
    );

    AdminSolution get(
            Long problemId,
            Long solutionId,
            Boolean includeInactive
    );

    List<AdminSolution> list(
            Long problemId,
            Boolean includeInactive
    );

    void delete(
            Long problemId,
            Long solutionId
    );

    AdminSolution restore(
            Long problemId,
            Long solutionId
    );

    AdminSolution upsertEditorial(
            Long problemId,
            EditorialUpsert request
    );

    AdminSolution getEditorial(
            Long problemId
    );

    void deleteEditorial(
            Long problemId
    );
}