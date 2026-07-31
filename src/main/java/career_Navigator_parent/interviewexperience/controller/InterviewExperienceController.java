package career_Navigator_parent.interviewexperience.controller;

import career_Navigator_parent.interviewexperience.dto.request.CreateInterviewExperienceRequest;
import career_Navigator_parent.interviewexperience.dto.request.UpdateInterviewExperienceRequest;
import career_Navigator_parent.interviewexperience.dto.response.InterviewExperienceDetailResponse;
import career_Navigator_parent.interviewexperience.dto.response.InterviewExperienceSummaryResponse;
import career_Navigator_parent.interviewexperience.dto.response.PageResponse;
import career_Navigator_parent.interviewexperience.enums.InterviewExperienceStatus;
import career_Navigator_parent.interviewexperience.enums.InterviewQuestionCategory;
import career_Navigator_parent.interviewexperience.service.InterviewExperienceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/interview-experiences")
@RequiredArgsConstructor
public class InterviewExperienceController {

    private final InterviewExperienceService experienceService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InterviewExperienceDetailResponse>
    createExperience(
            @Valid
            @RequestBody
            CreateInterviewExperienceRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        experienceService.createExperience(
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<
            PageResponse<InterviewExperienceSummaryResponse>
            > getPublicExperiences(
            @RequestParam(required = false)
            Long companyId,

            @RequestParam(required = false)
            String companyName,

            @RequestParam(required = false)
            String jobRole,

            @RequestParam(required = false)
            String experienceLevel,

            @RequestParam(required = false)
            String location,

            @RequestParam(required = false)
            InterviewQuestionCategory category,

            @RequestParam(required = false)
            String topic,

            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = DESC
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                experienceService.getPublicExperiences(
                        companyId,
                        companyName,
                        jobRole,
                        experienceLevel,
                        location,
                        category,
                        topic,
                        pageable
                )
        );
    }

    /*
     * Keep /my before /{experienceId} conceptually.
     * Spring can distinguish them, but this structure is clearer.
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<
            PageResponse<InterviewExperienceSummaryResponse>
            > getMyExperiences(
            @RequestParam(required = false)
            InterviewExperienceStatus status,

            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = DESC
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                experienceService.getMyExperiences(
                        status,
                        pageable
                )
        );
    }

    @GetMapping("/{experienceId}")
    public ResponseEntity<InterviewExperienceDetailResponse>
    getExperienceById(
            @PathVariable
            Long experienceId
    ) {

        return ResponseEntity.ok(
                experienceService.getExperienceById(
                        experienceId
                )
        );
    }

    @PutMapping("/{experienceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InterviewExperienceDetailResponse>
    updateExperience(
            @PathVariable
            Long experienceId,

            @Valid
            @RequestBody
            UpdateInterviewExperienceRequest request
    ) {

        return ResponseEntity.ok(
                experienceService.updateExperience(
                        experienceId,
                        request
                )
        );
    }

    @PatchMapping("/{experienceId}/resubmit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InterviewExperienceDetailResponse>
    resubmitExperience(
            @PathVariable
            Long experienceId
    ) {

        return ResponseEntity.ok(
                experienceService.resubmitExperience(
                        experienceId
                )
        );
    }

    @DeleteMapping("/{experienceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteExperience(
            @PathVariable
            Long experienceId
    ) {

        experienceService.deleteExperience(
                experienceId
        );

        return ResponseEntity.noContent().build();
    }
}