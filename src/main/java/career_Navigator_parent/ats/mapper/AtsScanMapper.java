package career_Navigator_parent.ats.mapper;


import career_Navigator_parent.ats.dto.response.AtsScanResponse;
import career_Navigator_parent.ats.entity.AtsScanResult;

import org.springframework.stereotype.Component;


@Component
public class AtsScanMapper {


    public AtsScanResponse toResponse(
            AtsScanResult atsScanResult
    ){

        return AtsScanResponse.builder()

                .id(
                        atsScanResult.getId()
                )

                .resumeId(
                        atsScanResult.getResume().getId()
                )

                .jobTitle(
                        atsScanResult.getJobTitle()
                )

                .companyName(
                        atsScanResult.getCompanyName()
                )

                .atsScore(
                        atsScanResult.getAtsScore()
                )

                .matchedSkills(
                        atsScanResult.getMatchedSkills()
                )

                .missingSkills(
                        atsScanResult.getMissingSkills()
                )

                .suggestions(
                        atsScanResult.getSuggestions()
                )

                .createdAt(
                        atsScanResult.getCreatedAt()
                )

                .build();

    }

}