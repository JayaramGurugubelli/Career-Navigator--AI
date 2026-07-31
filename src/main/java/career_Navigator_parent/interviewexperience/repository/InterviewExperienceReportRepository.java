package career_Navigator_parent.interviewexperience.repository;

import career_Navigator_parent.interviewexperience.entity.InterviewExperienceReport;
import career_Navigator_parent.interviewexperience.enums.InterviewExperienceReportReason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterviewExperienceReportRepository
        extends JpaRepository<InterviewExperienceReport, Long>,
        JpaSpecificationExecutor<InterviewExperienceReport> {

    boolean existsByInterviewExperience_IdAndReportedBy_Id(
            Long interviewExperienceId,
            Long reportedByUserId
    );

    Optional<InterviewExperienceReport>
    findByInterviewExperience_IdAndReportedBy_Id(
            Long interviewExperienceId,
            Long reportedByUserId
    );

    long countByInterviewExperience_Id(
            Long interviewExperienceId
    );

    long countByInterviewExperience_IdAndReviewedFalse(
            Long interviewExperienceId
    );

    long countByInterviewExperience_IdAndReason(
            Long interviewExperienceId,
            InterviewExperienceReportReason reason
    );

    long countByReviewedFalse();

    long countByReviewedFalseAndResolvedFalse();

    @EntityGraph(
            attributePaths = {
                    "interviewExperience",
                    "reportedBy"
            }
    )
    Page<InterviewExperienceReport>
    findByReviewedFalseOrderByCreatedAtAsc(
            Pageable pageable
    );

    @EntityGraph(
            attributePaths = {
                    "interviewExperience",
                    "reportedBy",
                    "reviewedBy"
            }
    )
    Page<InterviewExperienceReport>
    findByReviewedTrueOrderByReviewedAtDesc(
            Pageable pageable
    );

    @EntityGraph(
            attributePaths = {
                    "interviewExperience",
                    "reportedBy",
                    "reviewedBy"
            }
    )
    Page<InterviewExperienceReport>
    findByInterviewExperience_IdOrderByCreatedAtDesc(
            Long interviewExperienceId,
            Pageable pageable
    );

    @EntityGraph(
            attributePaths = {
                    "interviewExperience",
                    "reportedBy",
                    "reviewedBy"
            }
    )
    @Query("""
            select r
            from InterviewExperienceReport r
            where r.id = :reportId
            """)
    Optional<InterviewExperienceReport> findDetailedById(
            @Param("reportId") Long reportId
    );

    void deleteAllByInterviewExperience_Id(
            Long interviewExperienceId
    );
}