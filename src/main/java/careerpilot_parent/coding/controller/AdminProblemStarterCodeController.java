package careerpilot_parent.coding.controller;

import careerpilot_parent.coding.dto.request.ProblemRequests.Starter;
import careerpilot_parent.coding.dto.response.CodingResponses.AdminStarter;
import careerpilot_parent.coding.enums.ProgrammingLanguage;
import careerpilot_parent.coding.service.ProblemStarterCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/admin/coding/problems/{problemId:\\d+}/starter-codes"
)
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProblemStarterCodeController {

    private final ProblemStarterCodeService starterCodeService;

    @PostMapping
    public ResponseEntity<AdminStarter> create(
            @PathVariable Long problemId,
            @Valid @RequestBody Starter request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        starterCodeService.create(
                                problemId,
                                request
                        )
                );
    }

    @PutMapping("/{language}")
    public ResponseEntity<AdminStarter> update(
            @PathVariable Long problemId,
            @PathVariable ProgrammingLanguage language,
            @Valid @RequestBody Starter request
    ) {

        return ResponseEntity.ok(
                starterCodeService.update(
                        problemId,
                        language,
                        request
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<AdminStarter>> list(
            @PathVariable Long problemId,
            @RequestParam(defaultValue = "false")
            Boolean includeInactive
    ) {

        return ResponseEntity.ok(
                starterCodeService.list(
                        problemId,
                        includeInactive
                )
        );
    }

    @GetMapping("/{language}")
    public ResponseEntity<AdminStarter> get(
            @PathVariable Long problemId,
            @PathVariable ProgrammingLanguage language
    ) {

        return ResponseEntity.ok(
                starterCodeService.get(
                        problemId,
                        language
                )
        );
    }

    @DeleteMapping("/{language}")
    public ResponseEntity<Void> delete(
            @PathVariable Long problemId,
            @PathVariable ProgrammingLanguage language
    ) {

        starterCodeService.delete(
                problemId,
                language
        );

        return ResponseEntity.noContent().build();
    }
}