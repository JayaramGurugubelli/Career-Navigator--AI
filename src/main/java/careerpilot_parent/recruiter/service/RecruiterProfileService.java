package careerpilot_parent.recruiter.service;

import careerpilot_parent.company.dto.request.CreateRecruiterProfileRequest;
import careerpilot_parent.company.dto.request.UpdateRecruiterProfileRequest;
import careerpilot_parent.company.dto.response.RecruiterProfileResponse;

public interface RecruiterProfileService {

    RecruiterProfileResponse createProfile(
            CreateRecruiterProfileRequest request
    );

    RecruiterProfileResponse getMyProfile();

    RecruiterProfileResponse updateProfile(
            UpdateRecruiterProfileRequest request
    );

    void deactivateProfile();
}