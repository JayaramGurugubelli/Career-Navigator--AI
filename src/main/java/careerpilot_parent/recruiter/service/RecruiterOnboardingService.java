package careerpilot_parent.recruiter.service;

import careerpilot_parent.company.dto.request.CreateRecruiterProfileRequest;
import careerpilot_parent.company.dto.response.RecruiterProfileResponse;

public interface RecruiterOnboardingService {

    RecruiterProfileResponse createProfile(
            CreateRecruiterProfileRequest request
    );

    RecruiterProfileResponse getMyProfile();
}
