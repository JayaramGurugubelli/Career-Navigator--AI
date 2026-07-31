package career_Navigator_parent.student.controller;

import career_Navigator_parent.student.dto.request.CreateStudentSkillRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentSkillRequest;
import career_Navigator_parent.student.dto.response.StudentSkillResponse;
import career_Navigator_parent.student.service.StudentSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/skills")
@RequiredArgsConstructor
public class StudentSkillController {

    private final StudentSkillService studentSkillService;

    @PostMapping
    public ResponseEntity<StudentSkillResponse> addSkill(
            @Valid @RequestBody CreateStudentSkillRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(studentSkillService.addSkill(request));
    }

    @GetMapping
    public ResponseEntity<List<StudentSkillResponse>> getSkills() {

        return ResponseEntity.ok(
                studentSkillService.getSkills()
        );
    }

    @GetMapping("/{skillId}")
    public ResponseEntity<StudentSkillResponse> getSkillById(@PathVariable Long skillId) {

        return ResponseEntity.ok(
                studentSkillService.getSkillById(skillId)
        );
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<StudentSkillResponse> updateSkill(@PathVariable Long skillId, @Valid @RequestBody UpdateStudentSkillRequest request) {

        return ResponseEntity.ok(
                studentSkillService.updateSkill(
                        skillId,
                        request
                )
        );
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<String> deleteSkill(@PathVariable Long skillId) {

        studentSkillService.deleteSkill(skillId);

        return ResponseEntity.ok(
                "Skill deleted successfully."
        );
    }

}