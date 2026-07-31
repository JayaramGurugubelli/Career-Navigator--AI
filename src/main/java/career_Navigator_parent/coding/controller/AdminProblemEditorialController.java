package career_Navigator_parent.coding.controller;

import career_Navigator_parent.coding.dto.request.ProblemSolutionRequests.EditorialUpsert;
import career_Navigator_parent.coding.dto.response.CodingResponses.AdminSolution;
import career_Navigator_parent.coding.service.ProblemSolutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        "/api/admin/coding/problems/{problemId:\\d+}/editorial"
)
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProblemEditorialController {

    private final ProblemSolutionService solutionService;

    @PutMapping
    public ResponseEntity<AdminSolution> upsertEditorial(
            @PathVariable Long problemId,
            @Valid @RequestBody EditorialUpsert request
    ) {

        return ResponseEntity.ok(
                solutionService.upsertEditorial(
                        problemId,
                        request
                )
        );
    }

    @GetMapping
    public ResponseEntity<AdminSolution> getEditorial(
            @PathVariable Long problemId
    ) {

        return ResponseEntity.ok(
                solutionService.getEditorial(problemId)
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteEditorial(
            @PathVariable Long problemId
    ) {

        solutionService.deleteEditorial(problemId);

        return ResponseEntity.noContent().build();
    }
}