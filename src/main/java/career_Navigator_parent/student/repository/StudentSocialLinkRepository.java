package career_Navigator_parent.student.repository;

import career_Navigator_parent.student.entity.StudentSocialLink;
import career_Navigator_parent.student.enums.SocialPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentSocialLinkRepository
        extends JpaRepository<StudentSocialLink, Long> {

    /**
     * Get all social links of a student
     * ordered by display order.
     */
    List<StudentSocialLink> findByStudentIdOrderByDisplayOrderAsc(Long studentId);


    /**
     * Find one social link.
     */
    Optional<StudentSocialLink> findByIdAndStudentId(Long socialLinkId, Long studentId);


    /**
     * Prevent duplicate platform.
     */
    boolean existsByStudentIdAndPlatform(Long studentId, SocialPlatform platform);

    List<StudentSocialLink> findByStudentId(Long studentId);
}