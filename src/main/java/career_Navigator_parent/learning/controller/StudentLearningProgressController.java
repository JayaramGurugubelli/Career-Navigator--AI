package career_Navigator_parent.learning.controller;

import career_Navigator_parent.learning.dto.request.StudentLearningRequests;
import career_Navigator_parent.learning.dto.response.LearningResponses;
import career_Navigator_parent.learning.service.StudentLearningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/learning")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentLearningProgressController {
    private final StudentLearningService service;
    @PostMapping("/paths/{id}/enroll") public ResponseEntity<LearningResponses.Enrollment> enroll(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enroll(id));}
    @GetMapping("/enrollments") public ResponseEntity<Page<LearningResponses.Enrollment>> enrollments(Pageable p){
        return ResponseEntity.ok(service.enrollments(p));}
    @PostMapping("/lessons/{id}/start") public ResponseEntity<LearningResponses.Lesson> start(@PathVariable Long id){
        return ResponseEntity.ok(service.startLesson(id));}
    @PatchMapping("/lessons/{id}/progress") public ResponseEntity<LearningResponses.Lesson> progress(
            @PathVariable Long id,@Valid @RequestBody StudentLearningRequests.LessonProgress r){
        return ResponseEntity.ok(service.updateLessonProgress(id,r));}
    @PostMapping("/lessons/{id}/complete") public ResponseEntity<LearningResponses.Lesson> complete(@PathVariable Long id){
        return ResponseEntity.ok(service.completeLesson(id));}
    @PutMapping("/weekly-goal") public ResponseEntity<LearningResponses.WeeklyGoal> goal(
            @Valid @RequestBody StudentLearningRequests.WeeklyGoal r){
        return ResponseEntity.ok(service.setWeeklyGoal(r));}
}
