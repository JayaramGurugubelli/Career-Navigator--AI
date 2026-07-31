package career_Navigator_parent.resume.service.impl;

import career_Navigator_parent.common.exception.BadRequestException;
import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.resume.entity.Resume;
import career_Navigator_parent.resume.entity.ResumeSection;
import career_Navigator_parent.resume.enums.ResumeSectionType;
import career_Navigator_parent.resume.repository.ResumeRepository;
import career_Navigator_parent.resume.repository.ResumeSectionRepository;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeReferenceValidationService {

    private final ResumeRepository resumeRepository;
    private final ResumeSectionRepository resumeSectionRepository;

    private final StudentRepository studentRepository;

    private final StudentEducationRepository educationRepository;
    private final StudentExperienceRepository experienceRepository;
    private final StudentProjectRepository projectRepository;
    private final StudentSkillRepository skillRepository;
    private final StudentCertificationRepository certificationRepository;
    private final StudentAchievementRepository achievementRepository;

    private final SecurityUtils securityUtils;

    /**
     * Validate that the resume belongs to the
     * currently authenticated student.
     */
//    public Resume validateResumeOwnership(Long resumeId) {
//
//        Long userId = securityUtils.getCurrentUserId();
//
//        Student student = studentRepository.findByUserId(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("Student not found."));
//
//        return resumeRepository.findByIdAndStudentId(resumeId, student.getId())
//                .orElseThrow(() -> new ResourceNotFoundException("Resume not found."));
//    }

    /**
     * Validate that the referenced entity
     * belongs to the logged-in student.
     */
    public void validateReferenceOwnership(
            ResumeSectionType sectionType,
            Long referenceId
    ) {

        Long userId = securityUtils.getCurrentUserId();

        Student student = studentRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Student not found."));

        Long studentId = student.getId();

        switch (sectionType) {

            case EDUCATION ->
                    educationRepository
                            .findByIdAndStudentId(referenceId, studentId)
                            .orElseThrow(() -> new ResourceNotFoundException("Education not found."));

            case EXPERIENCE ->
                    experienceRepository
                            .findByIdAndStudentId(referenceId, studentId)
                            .orElseThrow(() -> new ResourceNotFoundException("Experience not found."));

            case PROJECT ->
                    projectRepository
                            .findByIdAndStudentId(referenceId, studentId)
                            .orElseThrow(() -> new ResourceNotFoundException("Project not found."));

            case SKILL ->
                    skillRepository
                            .findByIdAndStudentId(referenceId, studentId)
                            .orElseThrow(() -> new ResourceNotFoundException("Skill not found."));

            case CERTIFICATION ->
                    certificationRepository
                            .findByIdAndStudentId(referenceId, studentId)
                            .orElseThrow(() -> new ResourceNotFoundException("Certification not found."));

            case ACHIEVEMENT ->
                    achievementRepository
                            .findByIdAndStudentId(referenceId, studentId)
                            .orElseThrow(() -> new ResourceNotFoundException("Achievement not found."));

            default ->
                    throw new BadRequestException("Unsupported section type.");
        }

    }

    /**
     * Prevent duplicate entries
     * inside the same resume.
     */
    public void validateDuplicateSection(Long resumeId, ResumeSectionType sectionType, Long referenceId) {

        resumeSectionRepository
                .findByResumeIdAndSectionTypeAndReferenceId(
                        resumeId,
                        sectionType,
                        referenceId
                )
                .ifPresent(section -> {
                    throw new BadRequestException("This section already exists in the resume.");
                });

    }

    /**
     * Validate that the section belongs
     * to the given resume.
     */
    public ResumeSection validateResumeSection(Long resumeId, Long sectionId) {

        ResumeSection section = resumeSectionRepository
                .findById(sectionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resume section not found."));

        if (!section.getResume().getId().equals(resumeId)) {
            throw new BadRequestException("Section does not belong to this resume.");
        }

        return section;
    }
    public Resume validateResumeOwnership(Long resumeId)  {

        Long userId = securityUtils.getCurrentUserId();
        System.out.println("Current User ID = " + userId);

        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found."));

        System.out.println("Current Student ID = " + student.getId());
        System.out.println("Requested Resume ID = " + resumeId);

        return resumeRepository
                .findByIdAndStudentId(resumeId, student.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resume not found."));
    }

}