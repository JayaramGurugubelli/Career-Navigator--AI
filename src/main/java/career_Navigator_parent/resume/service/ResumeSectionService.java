package career_Navigator_parent.resume.service;

import career_Navigator_parent.resume.dto.request.CreateResumeSectionRequest;
import career_Navigator_parent.resume.dto.request.UpdateResumeSectionRequest;
import career_Navigator_parent.resume.dto.response.ResumeSectionResponse;

import java.util.List;

public interface ResumeSectionService {

    ResumeSectionResponse createSection(Long resumeId, CreateResumeSectionRequest request);

    ResumeSectionResponse updateSection(Long resumeId, Long sectionId, UpdateResumeSectionRequest request);

    ResumeSectionResponse getSection(Long resumeId, Long sectionId);

    List<ResumeSectionResponse> getAllSections(Long resumeId);

    void deleteSection(Long resumeId, Long sectionId);

}