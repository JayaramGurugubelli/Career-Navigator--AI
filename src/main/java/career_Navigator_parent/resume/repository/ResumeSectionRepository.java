package career_Navigator_parent.resume.repository;

import career_Navigator_parent.resume.entity.ResumeSection;
import career_Navigator_parent.resume.enums.ResumeSectionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeSectionRepository extends JpaRepository<ResumeSection, Long> {

    List<ResumeSection> findByResumeIdOrderByDisplayOrderAsc(Long resumeId);

    List<ResumeSection> findByResumeIdAndSectionType(Long resumeId, ResumeSectionType sectionType);

    Optional<ResumeSection> findByResumeIdAndSectionTypeAndReferenceId(Long resumeId, ResumeSectionType sectionType, Long referenceId);

}