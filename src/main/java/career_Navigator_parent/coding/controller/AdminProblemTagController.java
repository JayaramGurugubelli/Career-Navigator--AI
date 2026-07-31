package career_Navigator_parent.coding.controller;

import career_Navigator_parent.coding.dto.request.ProblemTagRequests;
import career_Navigator_parent.coding.dto.response.ProblemTagResponse;
import career_Navigator_parent.coding.service.ProblemTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/coding/tags")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProblemTagController {

    private final ProblemTagService tagService;

    @PostMapping
    public ResponseEntity<ProblemTagResponse> create(
            @Valid
            @RequestBody
            ProblemTagRequests.Create request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        tagService.create(request)
                );
    }

    @PutMapping("/{tagId}")
    public ResponseEntity<ProblemTagResponse> update(
            @PathVariable Long tagId,
            @Valid
            @RequestBody
            ProblemTagRequests.Update request
    ) {

        return ResponseEntity.ok(
                tagService.update(
                        tagId,
                        request
                )
        );
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<ProblemTagResponse> get(
            @PathVariable Long tagId
    ) {

        return ResponseEntity.ok(
                tagService.get(tagId)
        );
    }

    @GetMapping
    public ResponseEntity<List<ProblemTagResponse>> list(
            @RequestParam(
                    defaultValue = "false"
            )
            Boolean includeInactive
    ) {

        return ResponseEntity.ok(
                tagService.list(includeInactive)
        );
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long tagId
    ) {

        tagService.delete(tagId);

        return ResponseEntity.noContent().build();
    }
}