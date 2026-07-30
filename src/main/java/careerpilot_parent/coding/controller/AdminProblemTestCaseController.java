package careerpilot_parent.coding.controller;

import careerpilot_parent.coding.dto.request.ProblemTestCaseRequests.BatchCreate;
import careerpilot_parent.coding.dto.request.ProblemTestCaseRequests.BulkDelete;
import careerpilot_parent.coding.dto.request.ProblemTestCaseRequests.Create;
import careerpilot_parent.coding.dto.request.ProblemTestCaseRequests.Generate;
import careerpilot_parent.coding.dto.request.ProblemTestCaseRequests.Import;
import careerpilot_parent.coding.dto.request.ProblemTestCaseRequests.Update;
import careerpilot_parent.coding.dto.response.ProblemTestCaseResponses.AdminTestCase;
import careerpilot_parent.coding.dto.response.ProblemTestCaseResponses.BatchResult;
import careerpilot_parent.coding.dto.response.ProblemTestCaseResponses.DeleteResult;
import careerpilot_parent.coding.dto.response.ProblemTestCaseResponses.GenerationAccepted;
import careerpilot_parent.coding.dto.response.ProblemTestCaseResponses.GenerationJob;
import careerpilot_parent.coding.dto.response.ProblemTestCaseResponses.ImportResult;
import careerpilot_parent.coding.dto.response.ProblemTestCaseResponses.Summary;
import careerpilot_parent.coding.enums.TestCaseVisibility;
import careerpilot_parent.coding.service.ProblemTestCaseGenerationService;
import careerpilot_parent.coding.service.ProblemTestCaseService;
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
@RequestMapping(
        "/api/admin/coding/problems/{problemId:\\d+}/test-cases"
)
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class AdminProblemTestCaseController {

    private final ProblemTestCaseService testCaseService;

    private final ProblemTestCaseGenerationService generationService;

    @PostMapping
    public ResponseEntity<AdminTestCase> create(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero")
            Long problemId,

            @Valid
            @RequestBody
            Create request
    ) {

        AdminTestCase response =
                testCaseService.create(
                        problemId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<BatchResult> createBatch(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero")
            Long problemId,

            @Valid
            @RequestBody
            BatchCreate request
    ) {

        BatchResult response =
                testCaseService.createBatch(
                        problemId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/import")
    public ResponseEntity<ImportResult> importTestCases(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero")
            Long problemId,

            @Valid
            @RequestBody
            Import request
    ) {

        ImportResult response =
                testCaseService.importTestCases(
                        problemId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/generate")
    public ResponseEntity<GenerationAccepted> generate(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero")
            Long problemId,

            @Valid
            @RequestBody
            Generate request
    ) {

        GenerationAccepted response =
                generationService.startGeneration(
                        problemId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }

    @GetMapping("/generation-jobs/{jobId:\\d+}")
    public ResponseEntity<GenerationJob> getGenerationJob(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero")
            Long problemId,

            @PathVariable
            @Positive(message = "Generation job ID must be greater than zero")
            Long jobId
    ) {

        GenerationJob response =
                generationService.getJob(
                        problemId,
                        jobId
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<Summary> summary(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero")
            Long problemId
    ) {

        return ResponseEntity.ok(
                testCaseService.summary(problemId)
        );
    }

    @GetMapping
    public ResponseEntity<Page<AdminTestCase>> list(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero")
            Long problemId,

            @RequestParam(required = false)
            TestCaseVisibility visibility,

            @RequestParam(defaultValue = "false")
            Boolean includeInactive,

            @PageableDefault(
                    size = 50,
                    sort = "displayOrder"
            )
            Pageable pageable
    ) {

        Page<AdminTestCase> response =
                testCaseService.list(
                        problemId,
                        visibility,
                        Boolean.TRUE.equals(includeInactive),
                        pageable
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{testCaseId:\\d+}")
    public ResponseEntity<AdminTestCase> get(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero")
            Long problemId,

            @PathVariable
            @Positive(message = "Test-case ID must be greater than zero")
            Long testCaseId,

            @RequestParam(defaultValue = "false")
            Boolean includeInactive
    ) {

        AdminTestCase response =
                testCaseService.get(
                        problemId,
                        testCaseId,
                        Boolean.TRUE.equals(includeInactive)
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{testCaseId:\\d+}")
    public ResponseEntity<AdminTestCase> update(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero")
            Long problemId,

            @PathVariable
            @Positive(message = "Test-case ID must be greater than zero")
            Long testCaseId,

            @Valid
            @RequestBody
            Update request
    ) {

        AdminTestCase response =
                testCaseService.update(
                        problemId,
                        testCaseId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{testCaseId:\\d+}/restore")
    public ResponseEntity<AdminTestCase> restore(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero")
            Long problemId,

            @PathVariable
            @Positive(message = "Test-case ID must be greater than zero")
            Long testCaseId
    ) {

        AdminTestCase response =
                testCaseService.restore(
                        problemId,
                        testCaseId
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{testCaseId:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero")
            Long problemId,

            @PathVariable
            @Positive(message = "Test-case ID must be greater than zero")
            Long testCaseId
    ) {

        testCaseService.delete(
                problemId,
                testCaseId
        );

        return ResponseEntity
                .noContent()
                .build();
    }

    /*
     * Main bulk-delete endpoint.
     *
     * Some HTTP clients and proxies support DELETE request bodies,
     * while others may not handle them reliably.
     */
    @DeleteMapping
    public ResponseEntity<DeleteResult> deleteBatch(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero")
            Long problemId,

            @Valid
            @RequestBody
            BulkDelete request
    ) {

        DeleteResult response =
                testCaseService.deleteBulk(
                        problemId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    /*
     * Compatibility endpoint for clients, gateways, or proxies that
     * do not reliably support a request body with DELETE.
     */
    @PostMapping("/batch-delete")
    public ResponseEntity<DeleteResult> deleteBatchPost(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero")
            Long problemId,

            @Valid
            @RequestBody
            BulkDelete request
    ) {

        DeleteResult response =
                testCaseService.deleteBulk(
                        problemId,
                        request
                );

        return ResponseEntity.ok(response);
    }
}