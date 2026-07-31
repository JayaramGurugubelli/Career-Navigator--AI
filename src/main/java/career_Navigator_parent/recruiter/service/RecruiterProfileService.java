package career_Navigator_parent.recruiter.service;

import career_Navigator_parent.company.dto.request.CreateRecruiterProfileRequest;
import career_Navigator_parent.company.dto.request.UpdateRecruiterProfileRequest;
import career_Navigator_parent.company.dto.response.RecruiterProfileResponse;

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