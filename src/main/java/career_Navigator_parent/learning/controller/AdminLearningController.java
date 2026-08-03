package career_Navigator_parent.learning.controller;

import career_Navigator_parent.learning.dto.request.AdminLearningRequests;
import career_Navigator_parent.learning.dto.request.LearningStatusRequests;
import career_Navigator_parent.learning.dto.response.LearningPublicationResponses;
import career_Navigator_parent.learning.dto.response.LearningResponses;
import career_Navigator_parent.learning.service.AdminLearningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/learning")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminLearningController {

    private final AdminLearningService adminLearningService;

    @PostMapping("/disciplines")
    public ResponseEntity<LearningResponses.Discipline> createDiscipline(
            @Valid
            @RequestBody
            AdminLearningRequests.DisciplineCreate request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        adminLearningService
                                .createDiscipline(request)
                );
    }

    @PostMapping("/career-domains")
    public ResponseEntity<LearningResponses.Domain> createDomain(
            @Valid
            @RequestBody
            AdminLearningRequests.DomainCreate request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        adminLearningService
                                .createDomain(request)
                );
    }

    @PostMapping("/career-roles")
    public ResponseEntity<LearningResponses.Role> createRole(
            @Valid
            @RequestBody
            AdminLearningRequests.RoleCreate request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        adminLearningService
                                .createRole(request)
                );
    }

    @PostMapping("/paths")
    public ResponseEntity<LearningResponses.PathSummary> createPath(
            @Valid
            @RequestBody
            AdminLearningRequests.PathCreate request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        adminLearningService
                                .createPath(request)
                );
    }

    @PostMapping("/paths/{pathId}/milestones")
    public ResponseEntity<LearningResponses.Milestone> addMilestone(
            @PathVariable Long pathId,
            @Valid
            @RequestBody
            AdminLearningRequests.MilestoneCreate request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        adminLearningService
                                .addMilestone(
                                        pathId,
                                        request
                                )
                );
    }

    @PostMapping("/courses")
    public ResponseEntity<LearningResponses.CourseSummary> createCourse(
            @Valid
            @RequestBody
            AdminLearningRequests.CourseCreate request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        adminLearningService
                                .createCourse(request)
                );
    }

    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<LearningResponses.Module> addModule(
            @PathVariable Long courseId,
            @Valid
            @RequestBody
            AdminLearningRequests.ModuleCreate request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        adminLearningService
                                .addModule(
                                        courseId,
                                        request
                                )
                );
    }

    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<LearningResponses.Lesson> addLesson(
            @PathVariable Long moduleId,
            @Valid
            @RequestBody
            AdminLearningRequests.LessonCreate request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        adminLearningService
                                .addLesson(
                                        moduleId,
                                        request
                                )
                );
    }

    @PostMapping("/paths/{pathId}/courses")
    public ResponseEntity<LearningResponses.PathCourse> attachCourse(
            @PathVariable Long pathId,
            @Valid
            @RequestBody
            AdminLearningRequests.PathCourseCreate request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        adminLearningService
                                .attachCourse(
                                        pathId,
                                        request
                                )
                );
    }

    @PatchMapping("/courses/{courseId}/status")
    public ResponseEntity<LearningPublicationResponses.CourseStatus>
    updateCourseStatus(
            @PathVariable Long courseId,
            @Valid
            @RequestBody
            LearningStatusRequests.CourseStatusUpdate request
    ) {
        return ResponseEntity.ok(
                adminLearningService.updateCourseStatus(
                        courseId,
                        request
                )
        );
    }

    @PatchMapping("/paths/{pathId}/status")
    public ResponseEntity<LearningResponses.PathSummary> updatePathStatus(
            @PathVariable Long pathId,
            @Valid
            @RequestBody
            AdminLearningRequests.PathStatusUpdate request
    ) {
        return ResponseEntity.ok(
                adminLearningService.updatePathStatus(
                        pathId,
                        request
                )
        );
    }
}