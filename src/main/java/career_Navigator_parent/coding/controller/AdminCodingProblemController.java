package career_Navigator_parent.coding.controller;

import career_Navigator_parent.coding.dto.request.ProblemRequests.Activation;
import career_Navigator_parent.coding.dto.request.ProblemRequests.Create;
import career_Navigator_parent.coding.dto.request.ProblemRequests.Status;
import career_Navigator_parent.coding.dto.request.ProblemRequests.Update;
import career_Navigator_parent.coding.dto.response.CodingResponses.Admin;
import career_Navigator_parent.coding.service.ProblemManagementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/coding/problems")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class AdminCodingProblemController {

    private final ProblemManagementService problemManagementService;

    /**
     * Creates a new coding problem.
     *
     * POST /api/admin/coding/problems
     */
    @PostMapping
    public ResponseEntity<Admin> createProblem(
            @Valid @RequestBody Create request
    ) {
        Admin response =
                problemManagementService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Updates an existing coding problem.
     *
     * PUT /api/admin/coding/problems/{problemId}
     */
    @PutMapping("/{problemId:\\d+}")
    public ResponseEntity<Admin> updateProblem(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero.")
            Long problemId,

            @Valid @RequestBody Update request
    ) {
        Admin response =
                problemManagementService.update(
                        problemId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Changes the workflow status of a coding problem.
     *
     * PATCH /api/admin/coding/problems/{problemId}/status
     */
    @PatchMapping("/{problemId:\\d+}/status")
    public ResponseEntity<Admin> updateProblemStatus(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero.")
            Long problemId,

            @Valid @RequestBody Status request
    ) {
        Admin response =
                problemManagementService.status(
                        problemId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Activates or deactivates a coding problem without changing its
     * workflow status unless the service applies a business rule.
     *
     * PATCH /api/admin/coding/problems/{problemId}/activation
     */
    @PatchMapping("/{problemId:\\d+}/activation")
    public ResponseEntity<Admin> updateProblemActivation(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero.")
            Long problemId,

            @Valid @RequestBody Activation request
    ) {
        Admin response =
                problemManagementService.updateActivation(
                        problemId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Returns one coding problem for administration.
     *
     * GET /api/admin/coding/problems/{problemId}
     */
    @GetMapping("/{problemId:\\d+}")
    public ResponseEntity<Admin> getProblem(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero.")
            Long problemId
    ) {
        Admin response =
                problemManagementService.get(problemId);

        return ResponseEntity.ok(response);
    }

    /**
     * Returns an administratively pageable problem list.
     *
     * GET /api/admin/coding/problems
     */
    @GetMapping
    public ResponseEntity<Page<Admin>> listProblems(
            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "createdAt"
            )
            Pageable pageable
    ) {
        Page<Admin> response =
                problemManagementService.list(pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * Performs a soft delete by archiving and deactivating the problem.
     *
     * DELETE /api/admin/coding/problems/{problemId}
     */
    @DeleteMapping("/{problemId:\\d+}")
    public ResponseEntity<Void> deleteProblem(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero.")
            Long problemId
    ) {
        problemManagementService.delete(problemId);

        return ResponseEntity.noContent().build();
    }
}