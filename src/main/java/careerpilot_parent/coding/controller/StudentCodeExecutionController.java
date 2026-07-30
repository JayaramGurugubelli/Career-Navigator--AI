package careerpilot_parent.coding.controller;

import careerpilot_parent.coding.dto.request.ExecutionRequests.ProblemRun;
import careerpilot_parent.coding.dto.request.ExecutionRequests.Run;
import careerpilot_parent.coding.dto.response.CodingResponses.Execution;
import careerpilot_parent.coding.execution.service.CodeExecutionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('STUDENT')")
public class StudentCodeExecutionController {

    private final CodeExecutionService codeExecutionService;

    /**
     * Canonical execution endpoint.
     *
     * Problem ID is supplied in the request body.
     *
     * POST /api/student/coding/executions/run
     */
    @PostMapping("/api/student/coding/executions/run")
    public ResponseEntity<Execution> runCode(
            @Valid @RequestBody Run request
    ) {
        Execution response =
                codeExecutionService.run(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Problem-scoped execution endpoint.
     *
     * Problem ID is supplied in the URL.
     *
     * POST /api/student/coding/problems/{problemId}/run
     */
    @PostMapping(
            "/api/student/coding/problems/{problemId:\\d+}/run"
    )
    public ResponseEntity<Execution> runCodeForProblem(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero.")
            Long problemId,

            @Valid @RequestBody ProblemRun request
    ) {
        Execution response =
                codeExecutionService.run(
                        problemId,
                        request
                );

        return ResponseEntity.ok(response);
    }
}