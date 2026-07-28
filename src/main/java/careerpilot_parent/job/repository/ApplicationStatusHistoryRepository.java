package careerpilot_parent.job.repository;

import careerpilot_parent.job.entity.ApplicationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationStatusHistoryRepository
        extends JpaRepository<ApplicationStatusHistory, Long> {

    List<ApplicationStatusHistory>
    findByApplication_IdOrderByCreatedAtAsc(
            Long applicationId
    );

    @Query("""
            SELECT h
            FROM ApplicationStatusHistory h
            WHERE h.application.id = :applicationId
              AND h.application.student.id = :studentId
            ORDER BY h.createdAt ASC, h.id ASC
            """)
    List<ApplicationStatusHistory> findStudentApplicationHistory(
            @Param("applicationId") Long applicationId,
            @Param("studentId") Long studentId
    );

    @Query("""
            SELECT h
            FROM ApplicationStatusHistory h
            WHERE h.application.id = :applicationId
              AND h.application.jobPosting.recruiter.id = :recruiterId
            ORDER BY h.createdAt ASC, h.id ASC
            """)
    List<ApplicationStatusHistory> findRecruiterApplicationHistory(
            @Param("applicationId") Long applicationId,
            @Param("recruiterId") Long recruiterId
    );
}
