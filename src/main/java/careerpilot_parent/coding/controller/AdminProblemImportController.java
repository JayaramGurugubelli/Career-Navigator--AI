package careerpilot_parent.coding.controller;

import careerpilot_parent.coding.dto.request.ProblemImportRequests;
import careerpilot_parent.coding.dto.response.ProblemImportResponses;
import careerpilot_parent.coding.service.ProblemImportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/coding")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProblemImportController {

    private final ProblemImportService problemImportService;

    /**
     * Validates the complete problem-import request without inserting
     * problems into the database.
     *
     * POST /api/admin/coding/problems/import/validate
     */
    @PostMapping("/problems/import/validate")
    public ResponseEntity<ProblemImportResponses.ValidationResult> validateImport(
            @Valid @RequestBody ProblemImportRequests.ImportProblems request
    ) {
        ProblemImportResponses.ValidationResult response =
                problemImportService.validateImport(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Starts an asynchronous problem-import job.
     *
     * POST /api/admin/coding/problems/import
     */
    @PostMapping("/problems/import")
    public ResponseEntity<ProblemImportResponses.ImportResult> importProblems(
            @Valid @RequestBody ProblemImportRequests.ImportProblems request
    ) {
        ProblemImportResponses.ImportResult response =
                problemImportService.importProblems(request);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }

    /**
     * Returns the latest status of an asynchronous problem-import job.
     *
     * Primary endpoint:
     * GET /api/admin/coding/imports/{importId}
     *
     * Compatibility aliases:
     * GET /api/admin/coding/problems/import/{importId}
     * GET /api/admin/coding/problems/import/status/{importId}
     */
    @GetMapping({
            "/imports/{importId}",
            "/problems/import/{importId}",
            "/problems/import/status/{importId}"
    })
    public ResponseEntity<ProblemImportResponses.ImportStatus> getImportStatus(
            @PathVariable Long importId
    ) {
        ProblemImportResponses.ImportStatus response =
                problemImportService.getImportStatus(importId);

        return ResponseEntity.ok(response);
    }
}