package careerpilot_parent.coding.repository;

import careerpilot_parent.coding.entity.CodeSubmission;
import careerpilot_parent.coding.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CodeSubmissionRepository
        extends JpaRepository<CodeSubmission, Long> {

    Page<CodeSubmission> findByStudentId(
            Long studentId,
            Pageable pageable
    );

    Page<CodeSubmission> findByStudentIdAndProblemId(
            Long studentId,
            Long problemId,
            Pageable pageable
    );

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(
            Long studentId,
            SubmissionStatus status
    );

    @Query("""
            select count(s)
            from CodeSubmission s
            where s.student.id = :studentId
              and s.submittedAt >= :fromDate
              and s.submittedAt < :toDate
            """)
    long countStudentSubmissionsBetween(
            @Param("studentId") Long studentId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("""
            select count(s)
            from CodeSubmission s
            where s.student.id = :studentId
              and s.status = :status
              and s.submittedAt >= :fromDate
              and s.submittedAt < :toDate
            """)
    long countStudentSubmissionsByStatusBetween(
            @Param("studentId") Long studentId,
            @Param("status") SubmissionStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
    
}