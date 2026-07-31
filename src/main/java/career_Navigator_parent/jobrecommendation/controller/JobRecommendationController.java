package career_Navigator_parent.jobrecommendation.controller;

import career_Navigator_parent.jobrecommendation.dto.response.JobRecommendationResponse;
import career_Navigator_parent.jobrecommendation.enums.RecommendationSource;
import career_Navigator_parent.jobrecommendation.service.JobRecommendationService;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PageableDefault;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/student/job-recommendations")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('STUDENT')")
public class JobRecommendationController {

    private final JobRecommendationService
            jobRecommendationService;

    @GetMapping
    public ResponseEntity<
            Page<JobRecommendationResponse>
            >
    getMyRecommendations(
            @RequestParam(required = false)
            @DecimalMin(
                    value = "0.0",
                    message = "Minimum score cannot be negative"
            )
            @DecimalMax(
                    value = "100.0",
                    message = "Minimum score cannot exceed 100"
            )
            Double minimumScore,

            @RequestParam(required = false)
            RecommendationSource source,

            @PageableDefault(
                    size = 20,
                    sort = {
                            "matchScore",
                            "generatedAt"
                    }
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                jobRecommendationService
                        .getMyRecommendations(
                                minimumScore,
                                source,
                                pageable
                        )
        );
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobRecommendationResponse>
    getRecommendationByJobId(
            @PathVariable
            @Positive(message = "Job ID must be positive")
            Long jobId
    ) {

        return ResponseEntity.ok(
                jobRecommendationService
                        .getRecommendationByJobId(jobId)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>>
    refreshMyRecommendations() {

        int generatedCount =
                jobRecommendationService
                        .refreshMyRecommendations();

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "message",
                "Job recommendations refreshed successfully."
        );

        response.put(
                "generatedRecommendations",
                generatedCount
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void>
    dismissRecommendation(
            @PathVariable
            @Positive(message = "Job ID must be positive")
            Long jobId
    ) {

        jobRecommendationService
                .dismissRecommendation(jobId);

        return ResponseEntity
                .noContent()
                .build();
    }
}