package career_Navigator_parent.learning.controller;

import career_Navigator_parent.learning.dto.response.LearningResponses;
import career_Navigator_parent.learning.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/student/learning")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentLearningDashboardController {
    private final LearningDashboardService dashboard;
    private final LearningRecommendationService recommendations;
    @GetMapping("/dashboard") public ResponseEntity<LearningResponses.Dashboard> dashboard(){
        return ResponseEntity.ok(dashboard.dashboard());}
    @GetMapping("/recommendations") public ResponseEntity<List<LearningResponses.Recommendation>> recommendations(){
        return ResponseEntity.ok(recommendations.recommendations());}
}
