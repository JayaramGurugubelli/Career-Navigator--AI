package careerpilot_parent.coding.controller;

import careerpilot_parent.coding.dto.response.CodingResponses.Detail;
import careerpilot_parent.coding.dto.response.CodingResponses.Solution;
import careerpilot_parent.coding.dto.response.CodingResponses.Starter;
import careerpilot_parent.coding.dto.response.CodingResponses.Summary;
import careerpilot_parent.coding.enums.ProblemDifficulty;
import careerpilot_parent.coding.enums.ProgrammingLanguage;
import careerpilot_parent.coding.service.CodingProblemQueryService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coding/problems")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
public class CodingProblemController {

    private final CodingProblemQueryService codingProblemQueryService;

    /**
     * Search and browse published coding problems.
     *
     * GET /api/coding/problems
     */
    @GetMapping
    public ResponseEntity<Page<Summary>> searchProblems(
            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            ProblemDifficulty difficulty,

            @RequestParam(required = false)
            String tag,

            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "createdAt"
            )
            Pageable pageable
    ) {
        Page<Summary> response =
                codingProblemQueryService.search(
                        keyword,
                        difficulty,
                        tag,
                        pageable
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Returns starter code for a problem and programming language.
     *
     * GET /api/coding/problems/{problemId}/starter-code?language=JAVA
     */
    @GetMapping("/{problemId:\\d+}/starter-code")
    public ResponseEntity<Starter> getStarterCode(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero.")
            Long problemId,

            @RequestParam
            ProgrammingLanguage language
    ) {
        Starter response =
                codingProblemQueryService.starterCode(
                        problemId,
                        language
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Returns student-visible official solutions.
     *
     * GET /api/coding/problems/{problemId}/solutions
     */
    @GetMapping("/{problemId:\\d+}/solutions")
    public ResponseEntity<List<Solution>> getSolutions(
            @PathVariable
            @Positive(message = "Problem ID must be greater than zero.")
            Long problemId
    ) {
        List<Solution> response =
                codingProblemQueryService.solutions(problemId);

        return ResponseEntity.ok(response);
    }

    /**
     * Returns a published problem using its slug.
     *
     * This mapping is intentionally placed after numeric mappings.
     *
     * GET /api/coding/problems/{slug}
     */
    @GetMapping("/{slug:[a-zA-Z][a-zA-Z0-9-]*}")
    public ResponseEntity<Detail> getProblemBySlug(
            @PathVariable String slug
    ) {
        Detail response =
                codingProblemQueryService.get(slug);

        return ResponseEntity.ok(response);
    }
}