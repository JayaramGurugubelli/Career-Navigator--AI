package career_Navigator_parent.resume.mapper;

import career_Navigator_parent.resume.dto.request.CreateResumeSectionRequest;
import career_Navigator_parent.resume.dto.request.UpdateResumeSectionRequest;
import career_Navigator_parent.resume.dto.response.ResumeSectionResponse;
import career_Navigator_parent.resume.entity.Resume;
import career_Navigator_parent.resume.entity.ResumeSection;
import org.springframework.stereotype.Component;

@Component
public class ResumeSectionMapper {

    /**
     * Convert Create Request to Entity
     */
    public ResumeSection toEntity(CreateResumeSectionRequest request, Resume resume) {

        return ResumeSection.builder()
                .resume(resume)
                .sectionType(request.getSectionType())
                .referenceId(request.getReferenceId())
                .displayOrder(request.getDisplayOrder())
                .visible(request.getVisible())
                .sectionHeading(request.getSectionHeading())
                .highlight(request.getHighlight() != null
                                ? request.getHighlight()
                                : Boolean.FALSE
                )
                .customDescription(request.getCustomDescription())
                .build();
    }

    /**
     * Convert Entity to Response
     */
    public ResumeSectionResponse toResponse(ResumeSection section) {

        return ResumeSectionResponse.builder()
                .id(section.getId())
                .resumeId(section.getResume().getId())
                .sectionType(section.getSectionType())
                .referenceId(section.getReferenceId())
                .displayOrder(section.getDisplayOrder())
                .visible(section.getVisible())
                .sectionHeading(section.getSectionHeading())
                .highlight(section.getHighlight())
                .customDescription(section.getCustomDescription())
                .build();
    }

    /**
     * Update Existing Entity
     */
    public void updateEntity(ResumeSection section, UpdateResumeSectionRequest request) {

        section.setDisplayOrder(request.getDisplayOrder());
        section.setVisible(request.getVisible());

        section.setSectionHeading(request.getSectionHeading());

        if (request.getHighlight() != null) {
            section.setHighlight(request.getHighlight());
        }

        section.setCustomDescription(request.getCustomDescription());
    }

}