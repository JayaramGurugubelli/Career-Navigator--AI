package career_Navigator_parent.recruiter.mapper;

import career_Navigator_parent.company.entity.RecruiterProfile;
import career_Navigator_parent.company.dto.response.RecruiterProfileResponse;
import org.springframework.stereotype.Component;

@Component
public class RecruiterProfileMapper {

    public RecruiterProfileResponse toResponse(
            RecruiterProfile recruiter
    ) {

        if (recruiter == null) {
            return null;
        }

        String recruiterName =
                recruiter.getUser().getFirstName()
                        + " "
                        + recruiter.getUser().getLastName();

        return RecruiterProfileResponse.builder()
                .id(recruiter.getId())
                .userId(recruiter.getUser().getId())
                .recruiterName(recruiterName.trim())
                .companyId(recruiter.getCompany().getId())
                .companyName(recruiter.getCompany().getName())
                .designation(recruiter.getDesignation())
                .officialEmail(recruiter.getOfficialEmail())
                .phoneNumber(recruiter.getPhoneNumber())
                .linkedinUrl(recruiter.getLinkedinUrl())
                .verified(recruiter.isVerified())
                .active(recruiter.isActive())
                .build();
    }
}