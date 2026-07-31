package career_Navigator_parent.recruiter.controller;

import career_Navigator_parent.company.dto.request.CreateRecruiterProfileRequest;
import career_Navigator_parent.company.dto.request.UpdateRecruiterProfileRequest;
import career_Navigator_parent.company.dto.response.RecruiterProfileResponse;
import career_Navigator_parent.recruiter.service.RecruiterProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterProfileController {

    private final RecruiterProfileService recruiterProfileService;

    @PostMapping
    public ResponseEntity<RecruiterProfileResponse> createProfile(
            @Valid
            @RequestBody
            CreateRecruiterProfileRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        recruiterProfileService.createProfile(
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<RecruiterProfileResponse> getMyProfile() {

        return ResponseEntity.ok(
                recruiterProfileService.getMyProfile()
        );
    }

    @PutMapping
    public ResponseEntity<RecruiterProfileResponse> updateProfile(
            @Valid
            @RequestBody
            UpdateRecruiterProfileRequest request
    ) {

        return ResponseEntity.ok(
                recruiterProfileService.updateProfile(
                        request
                )
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> deactivateProfile() {

        recruiterProfileService.deactivateProfile();

        return ResponseEntity
                .noContent()
                .build();
    }
}