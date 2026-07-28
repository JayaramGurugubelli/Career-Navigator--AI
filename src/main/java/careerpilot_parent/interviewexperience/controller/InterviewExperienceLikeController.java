package careerpilot_parent.interviewexperience.controller;

import careerpilot_parent.interviewexperience.dto.response.InterviewExperienceLikeResponse;
import careerpilot_parent.interviewexperience.service.InterviewExperienceLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview-experiences/{experienceId}/likes")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class InterviewExperienceLikeController {

    private final InterviewExperienceLikeService likeService;

    @PostMapping
    public ResponseEntity<InterviewExperienceLikeResponse>
    likeExperience(
            @PathVariable
            Long experienceId
    ) {

        return ResponseEntity.ok(
                likeService.likeExperience(
                        experienceId
                )
        );
    }

    @DeleteMapping
    public ResponseEntity<InterviewExperienceLikeResponse>
    unlikeExperience(
            @PathVariable
            Long experienceId
    ) {

        return ResponseEntity.ok(
                likeService.unlikeExperience(
                        experienceId
                )
        );
    }

    @GetMapping("/state")
    public ResponseEntity<InterviewExperienceLikeResponse>
    getLikeState(
            @PathVariable
            Long experienceId
    ) {

        return ResponseEntity.ok(
                likeService.getLikeState(
                        experienceId
                )
        );
    }
}