package careerpilot_parent.job.controller;



import careerpilot_parent.company.dto.request.CreateRecruiterProfileRequest;
import careerpilot_parent.company.dto.response.RecruiterProfileResponse;
import careerpilot_parent.recruiter.service.RecruiterOnboardingService;
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
public class RecruiterOnboardingController {

    private final RecruiterOnboardingService onboardingService;

    @PostMapping
    public ResponseEntity<RecruiterProfileResponse> createProfile(
            @Valid
            @RequestBody
            CreateRecruiterProfileRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        onboardingService
                                .createProfile(request)
                );
    }

    @GetMapping
    public ResponseEntity<RecruiterProfileResponse> getMyProfile() {

        return ResponseEntity.ok(
                onboardingService.getMyProfile()
        );
    }
}