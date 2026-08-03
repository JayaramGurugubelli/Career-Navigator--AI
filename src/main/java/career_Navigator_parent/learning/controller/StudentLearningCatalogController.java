package career_Navigator_parent.learning.controller;

import career_Navigator_parent.learning.dto.response.LearningResponses;
import career_Navigator_parent.learning.service.StudentLearningCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/student/learning")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentLearningCatalogController {
    private final StudentLearningCatalogService service;
    @GetMapping("/disciplines") public ResponseEntity<List<LearningResponses.Discipline>> disciplines(){
        return ResponseEntity.ok(service.disciplines());}
    @GetMapping("/career-roles") public ResponseEntity<Page<LearningResponses.Role>> roles(
            @RequestParam(required=false) Long disciplineId,Pageable p){
        return ResponseEntity.ok(service.careerRoles(disciplineId,p));}
    @GetMapping("/career-roles/{id}") public ResponseEntity<LearningResponses.Role> role(@PathVariable Long id){
        return ResponseEntity.ok(service.careerRole(id));}
    @GetMapping("/paths") public ResponseEntity<Page<LearningResponses.PathSummary>> paths(
            @RequestParam(required=false) Long disciplineId,@RequestParam(required=false) Long careerRoleId,Pageable p){
        return ResponseEntity.ok(service.paths(disciplineId,careerRoleId,p));}
    @GetMapping("/paths/{id}") public ResponseEntity<LearningResponses.PathDetail> path(@PathVariable Long id){
        return ResponseEntity.ok(service.path(id));}
    @GetMapping("/courses/{id}") public ResponseEntity<LearningResponses.CourseDetail> course(@PathVariable Long id){
        return ResponseEntity.ok(service.course(id));}
}
