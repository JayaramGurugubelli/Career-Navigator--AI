package career_Navigator_parent.student.controller;


import career_Navigator_parent.student.dto.request.CreateStudentAchievementRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentAchievementRequest;
import career_Navigator_parent.student.dto.response.StudentAchievementResponse;
import career_Navigator_parent.student.service.StudentAchievementService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/student/achievements")
@RequiredArgsConstructor
public class StudentAchievementController {


    private final StudentAchievementService achievementService;



    @PostMapping
    public ResponseEntity<StudentAchievementResponse> createAchievement(@Valid @RequestBody CreateStudentAchievementRequest request) {


        StudentAchievementResponse response = achievementService.createAchievement(request);


        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }




    @GetMapping
    public ResponseEntity<List<StudentAchievementResponse>> getAllAchievements() {


        return ResponseEntity.ok(
                achievementService.getAllAchievements()
        );
    }





    @GetMapping("/{achievementId}")
    public ResponseEntity<StudentAchievementResponse> getAchievementById(@PathVariable Long achievementId) {
        return ResponseEntity.ok(
                achievementService.getAchievementById(
                        achievementId
                )
        );
    }





    @PutMapping("/{achievementId}")
    public ResponseEntity<StudentAchievementResponse> updateAchievement(@PathVariable Long achievementId, @Valid @RequestBody UpdateStudentAchievementRequest request) {


        return ResponseEntity.ok(
                achievementService.updateAchievement(
                        achievementId,
                        request
                )
        );
    }





    @DeleteMapping("/{achievementId}")
    public ResponseEntity<Void> deleteAchievement(@PathVariable Long achievementId) {

        achievementService.deleteAchievement(achievementId);

        return ResponseEntity.noContent().build();
    }

}