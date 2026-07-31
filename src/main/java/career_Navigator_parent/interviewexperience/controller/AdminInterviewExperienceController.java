package career_Navigator_parent.interviewexperience.controller;

import career_Navigator_parent.interviewexperience.dto.request.ModerateInterviewExperienceRequest;
import career_Navigator_parent.interviewexperience.dto.request.ReviewInterviewExperienceReportRequest;
import career_Navigator_parent.interviewexperience.dto.response.InterviewCommentResponse;
import career_Navigator_parent.interviewexperience.dto.response.InterviewExperienceDetailResponse;
import career_Navigator_parent.interviewexperience.dto.response.InterviewExperienceReportResponse;
import career_Navigator_parent.interviewexperience.dto.response.InterviewExperienceSummaryResponse;
import career_Navigator_parent.interviewexperience.dto.response.PageResponse;
import career_Navigator_parent.interviewexperience.enums.InterviewExperienceReportReason;
import career_Navigator_parent.interviewexperience.enums.InterviewExperienceStatus;
import career_Navigator_parent.interviewexperience.service.InterviewExperienceCommentService;
import career_Navigator_parent.interviewexperience.service.InterviewExperienceReportService;
import career_Navigator_parent.interviewexperience.service.InterviewExperienceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/admin/interview-experiences")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminInterviewExperienceController {

    private final InterviewExperienceService experienceService;
    private final InterviewExperienceCommentService commentService;
    private final InterviewExperienceReportService reportService;

    @GetMapping
    public ResponseEntity<
            PageResponse<InterviewExperienceSummaryResponse>
            > getExperiences(
            @RequestParam(required = false)
            InterviewExperienceStatus status,

            @RequestParam(required = false)
            String keyword,

            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = DESC
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                experienceService.getExperiencesForAdmin(
                        status,
                        keyword,
                        pageable
                )
        );
    }

    @PatchMapping("/{experienceId}/status")
    public ResponseEntity<InterviewExperienceDetailResponse>
    moderateExperience(
            @PathVariable
            Long experienceId,

            @Valid
            @RequestBody
            ModerateInterviewExperienceRequest request
    ) {

        return ResponseEntity.ok(
                experienceService.moderateExperience(
                        experienceId,
                        request
                )
        );
    }

    @PatchMapping(
            "/{experienceId}/comments/{commentId}/hide"
    )
    public ResponseEntity<InterviewCommentResponse>
    hideComment(
            @PathVariable
            Long experienceId,

            @PathVariable
            Long commentId
    ) {

        return ResponseEntity.ok(
                commentService.hideComment(
                        experienceId,
                        commentId
                )
        );
    }

    @PatchMapping(
            "/{experienceId}/comments/{commentId}/restore"
    )
    public ResponseEntity<InterviewCommentResponse>
    restoreComment(
            @PathVariable
            Long experienceId,

            @PathVariable
            Long commentId
    ) {

        return ResponseEntity.ok(
                commentService.restoreComment(
                        experienceId,
                        commentId
                )
        );
    }

    @GetMapping("/reports")
    public ResponseEntity<
            PageResponse<InterviewExperienceReportResponse>
            > getReports(
            @RequestParam(required = false)
            Boolean reviewed,

            @RequestParam(required = false)
            Boolean resolved,

            @RequestParam(required = false)
            InterviewExperienceReportReason reason,

            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = ASC
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                reportService.getReports(
                        reviewed,
                        resolved,
                        reason,
                        pageable
                )
        );
    }

    @GetMapping("/reports/{reportId}")
    public ResponseEntity<InterviewExperienceReportResponse>
    getReportById(
            @PathVariable
            Long reportId
    ) {

        return ResponseEntity.ok(
                reportService.getReportById(
                        reportId
                )
        );
    }

    @GetMapping("/{experienceId}/reports")
    public ResponseEntity<
            PageResponse<InterviewExperienceReportResponse>
            > getReportsForExperience(
            @PathVariable
            Long experienceId,

            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = DESC
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                reportService.getReportsForExperience(
                        experienceId,
                        pageable
                )
        );
    }

    @PatchMapping("/reports/{reportId}/review")
    public ResponseEntity<InterviewExperienceReportResponse>
    reviewReport(
            @PathVariable
            Long reportId,

            @Valid
            @RequestBody
            ReviewInterviewExperienceReportRequest request
    ) {

        return ResponseEntity.ok(
                reportService.reviewReport(
                        reportId,
                        request
                )
        );
    }

    @GetMapping("/reports/pending/count")
    public ResponseEntity<Map<String, Long>>
    getPendingReportCount() {

        return ResponseEntity.ok(
                Map.of(
                        "pendingReportCount",
                        reportService.getPendingReportCount()
                )
        );
    }

    @PatchMapping(
            "/{experienceId}/reports/recalculate-count"
    )
    public ResponseEntity<Map<String, Long>>
    recalculateReportCount(
            @PathVariable
            Long experienceId
    ) {

        long count =
                reportService
                        .recalculateExperienceReportCount(
                                experienceId
                        );

        return ResponseEntity.ok(
                Map.of(
                        "experienceId",
                        experienceId,
                        "reportCount",
                        count
                )
        );
    }
}