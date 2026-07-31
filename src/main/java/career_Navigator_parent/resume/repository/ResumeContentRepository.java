package career_Navigator_parent.resume.repository;




import career_Navigator_parent.resume.entity.ResumeContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ResumeContentRepository extends JpaRepository<ResumeContent, Long> {


    Optional<ResumeContent> findByResumeId(Long resumeId);

}