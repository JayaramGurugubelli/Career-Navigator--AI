package career_Navigator_parent.resume.service.impl;

import career_Navigator_parent.resume.dto.request.CreateResumeSectionRequest;
import career_Navigator_parent.resume.dto.request.UpdateResumeSectionRequest;
import career_Navigator_parent.resume.dto.response.ResumeSectionResponse;
import career_Navigator_parent.resume.entity.Resume;
import career_Navigator_parent.resume.entity.ResumeSection;
import career_Navigator_parent.resume.mapper.ResumeSectionMapper;
import career_Navigator_parent.resume.repository.ResumeSectionRepository;
import career_Navigator_parent.resume.service.ResumeSectionService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ResumeSectionServiceImpl
        implements ResumeSectionService {

    private final ResumeSectionRepository sectionRepository;

    private final ResumeSectionMapper sectionMapper;

    private final ResumeReferenceValidationService validationService;

    @Override
    public ResumeSectionResponse createSection(
            Long resumeId,
            CreateResumeSectionRequest request
    ) {

        Resume resume =
                validationService.validateResumeOwnership(
                        resumeId
                );

        validationService.validateReferenceOwnership(
                request.getSectionType(),
                request.getReferenceId()
        );

        validationService.validateDuplicateSection(
                resumeId,
                request.getSectionType(),
                request.getReferenceId()
        );

        ResumeSection section =
                sectionMapper.toEntity(
                        request,
                        resume
                );

        ResumeSection savedSection =
                sectionRepository.save(section);

        return sectionMapper.toResponse(savedSection);
    }

    @Override
    public ResumeSectionResponse updateSection(
            Long resumeId,
            Long sectionId,
            UpdateResumeSectionRequest request
    ) {

        validationService.validateResumeOwnership(
                resumeId
        );

        ResumeSection section =
                validationService.validateResumeSection(
                        resumeId,
                        sectionId
                );

        sectionMapper.updateEntity(
                section,
                request
        );

        ResumeSection updatedSection =
                sectionRepository.save(section);

        return sectionMapper.toResponse(updatedSection);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeSectionResponse getSection(
            Long resumeId,
            Long sectionId
    ) {

        validationService.validateResumeOwnership(
                resumeId
        );

        ResumeSection section =
                validationService.validateResumeSection(
                        resumeId,
                        sectionId
                );

        return sectionMapper.toResponse(section);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeSectionResponse> getAllSections(
            Long resumeId
    ) {

        validationService.validateResumeOwnership(
                resumeId
        );

        return sectionRepository
                .findByResumeIdOrderByDisplayOrderAsc(
                        resumeId
                )
                .stream()
                .map(sectionMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteSection(
            Long resumeId,
            Long sectionId
    ) {

        validationService.validateResumeOwnership(
                resumeId
        );

        ResumeSection section =
                validationService.validateResumeSection(
                        resumeId,
                        sectionId
                );

        sectionRepository.delete(section);
    }

}