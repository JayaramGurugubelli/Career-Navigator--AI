package careerpilot_parent.coding.controller;

import careerpilot_parent.coding.dto.response.StudentCodingResponses.*;
import careerpilot_parent.coding.enums.ProblemAttemptStatus;
import careerpilot_parent.coding.enums.ProblemDifficulty;
import careerpilot_parent.coding.service.StudentCodingActivityService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/student/coding")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('STUDENT')")
public class StudentCodingActivityController {

    private final StudentCodingActivityService activityService;

    @GetMapping("/dashboard")
    public ResponseEntity<Dashboard> dashboard() {
        return ResponseEntity.ok(
                activityService.dashboard()
        );
    }

    @GetMapping("/progress")
    public ResponseEntity<OverallProgress> progress(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate
    ) {
        return ResponseEntity.ok(
                activityService.progress(
                        fromDate,
                        toDate
                )
        );
    }

    @GetMapping("/progress/topics")
    public ResponseEntity<List<TopicProgress>>
    topics() {
        return ResponseEntity.ok(
                activityService.topicProgress()
        );
    }

    @GetMapping("/progress/difficulties")
    public ResponseEntity<List<DifficultyProgress>>
    difficulties() {
        return ResponseEntity.ok(
                activityService.difficultyProgress()
        );
    }

    @GetMapping("/progress/calendar")
    public ResponseEntity<ActivityCalendar> calendar(
            @RequestParam(required = false)
            Integer year,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate
    ) {
        if (year != null) {
            fromDate = LocalDate.of(year, 1, 1);
            toDate = LocalDate.of(year, 12, 31);
        }

        return ResponseEntity.ok(
                activityService.activityCalendar(
                        fromDate,
                        toDate
                )
        );
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<Recommendation>>
    recommendations(
            @RequestParam(required = false)
            ProblemDifficulty difficulty,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(50)
            int limit
    ) {
        return ResponseEntity.ok(
                activityService.recommendations(
                        difficulty,
                        limit
                )
        );
    }

    @GetMapping("/attempts")
    public ResponseEntity<Page<Attempt>> attempts(
            @RequestParam(required = false)
            ProblemAttemptStatus status,

            @PageableDefault(
                    size = 10,
                    sort = "lastAttemptedAt"
            )
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                activityService.attempts(
                        status,
                        pageable
                )
        );
    }

    @PostMapping("/problems/{problemId}/bookmark")
    public ResponseEntity<Bookmark> bookmark(
            @PathVariable
            @Positive
            Long problemId
    ) {
        return ResponseEntity.ok(
                activityService.addBookmark(problemId)
        );
    }

    @DeleteMapping("/problems/{problemId}/bookmark")
    public ResponseEntity<Void> removeBookmark(
            @PathVariable
            @Positive
            Long problemId
    ) {
        activityService.removeBookmark(problemId);

        return ResponseEntity
                .noContent()
                .build();
    }
}