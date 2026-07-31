package career_Navigator_parent.coding.controller;

import career_Navigator_parent.coding.dto.request.ProblemSolutionRequests.Create;
import career_Navigator_parent.coding.dto.request.ProblemSolutionRequests.Update;
import career_Navigator_parent.coding.dto.response.CodingResponses.AdminSolution;
import career_Navigator_parent.coding.service.ProblemSolutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/admin/coding/problems/{problemId:\\d+}/solutions"
)
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProblemSolutionController {

    private final ProblemSolutionService solutionService;

    @PostMapping
    public ResponseEntity<AdminSolution> create(
            @PathVariable Long problemId,
            @Valid @RequestBody Create request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        solutionService.create(
                                problemId,
                                request
                        )
                );
    }

    @PutMapping("/{solutionId:\\d+}")
    public ResponseEntity<AdminSolution> update(
            @PathVariable Long problemId,
            @PathVariable Long solutionId,
            @Valid @RequestBody Update request
    ) {

        return ResponseEntity.ok(
                solutionService.update(
                        problemId,
                        solutionId,
                        request
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<AdminSolution>> list(
            @PathVariable Long problemId,
            @RequestParam(defaultValue = "false")
            Boolean includeInactive
    ) {

        return ResponseEntity.ok(
                solutionService.list(
                        problemId,
                        includeInactive
                )
        );
    }

    @GetMapping("/{solutionId:\\d+}")
    public ResponseEntity<AdminSolution> get(
            @PathVariable Long problemId,
            @PathVariable Long solutionId,
            @RequestParam(defaultValue = "false")
            Boolean includeInactive
    ) {

        return ResponseEntity.ok(
                solutionService.get(
                        problemId,
                        solutionId,
                        includeInactive
                )
        );
    }

    @PatchMapping("/{solutionId:\\d+}/restore")
    public ResponseEntity<AdminSolution> restore(
            @PathVariable Long problemId,
            @PathVariable Long solutionId
    ) {

        return ResponseEntity.ok(
                solutionService.restore(
                        problemId,
                        solutionId
                )
        );
    }

    @DeleteMapping("/{solutionId:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable Long problemId,
            @PathVariable Long solutionId
    ) {

        solutionService.delete(
                problemId,
                solutionId
        );

        return ResponseEntity.noContent().build();
    }
}