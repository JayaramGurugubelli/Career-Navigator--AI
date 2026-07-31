package career_Navigator_parent.interviewexperience.repository;

import career_Navigator_parent.interviewexperience.entity.InterviewExperience;
import career_Navigator_parent.interviewexperience.enums.InterviewExperienceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterviewExperienceRepository
        extends JpaRepository<InterviewExperience, Long>,
        JpaSpecificationExecutor<InterviewExperience> {

    @EntityGraph(
            attributePaths = {
                    "submittedBy",
                    "company",
                    "rounds"
            }
    )
    @Query("""
        select distinct experience
        from InterviewExperience experience
        where experience.id = :experienceId
        """)
    Optional<InterviewExperience> findDetailedById(
            @Param("experienceId") Long experienceId
    );

    Page<InterviewExperience>
    findBySubmittedBy_IdOrderByCreatedAtDesc(
            Long submittedById,
            Pageable pageable
    );

    Page<InterviewExperience>
    findBySubmittedBy_IdAndStatusOrderByCreatedAtDesc(
            Long submittedById,
            InterviewExperienceStatus status,
            Pageable pageable
    );

    boolean existsByIdAndStatus(
            Long experienceId,
            InterviewExperienceStatus status
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update InterviewExperience experience
            set experience.likeCount =
                coalesce(experience.likeCount, 0) + 1
            where experience.id = :experienceId
            """)
    int incrementLikeCount(
            @Param("experienceId") Long experienceId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update InterviewExperience experience
            set experience.likeCount =
                case
                    when coalesce(experience.likeCount, 0) > 0
                    then experience.likeCount - 1
                    else 0
                end
            where experience.id = :experienceId
            """)
    int decrementLikeCount(
            @Param("experienceId") Long experienceId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update InterviewExperience experience
            set experience.commentCount =
                coalesce(experience.commentCount, 0) + 1
            where experience.id = :experienceId
            """)
    int incrementCommentCount(
            @Param("experienceId") Long experienceId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update InterviewExperience experience
            set experience.commentCount =
                case
                    when coalesce(experience.commentCount, 0) > 0
                    then experience.commentCount - 1
                    else 0
                end
            where experience.id = :experienceId
            """)
    int decrementCommentCount(
            @Param("experienceId") Long experienceId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update InterviewExperience experience
            set experience.reportCount =
                coalesce(experience.reportCount, 0) + 1
            where experience.id = :experienceId
            """)
    int incrementReportCount(
            @Param("experienceId") Long experienceId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update InterviewExperience experience
            set experience.reportCount = :reportCount
            where experience.id = :experienceId
            """)
    int updateReportCount(
            @Param("experienceId") Long experienceId,
            @Param("reportCount") Integer reportCount
    );

    @Query("""
            select coalesce(experience.likeCount, 0)
            from InterviewExperience experience
            where experience.id = :experienceId
            """)
    Optional<Integer> findLikeCount(
            @Param("experienceId") Long experienceId
    );

    @Query("""
            select coalesce(experience.commentCount, 0)
            from InterviewExperience experience
            where experience.id = :experienceId
            """)
    Optional<Integer> findCommentCount(
            @Param("experienceId") Long experienceId
    );

    @Query("""
            select coalesce(experience.reportCount, 0)
            from InterviewExperience experience
            where experience.id = :experienceId
            """)
    Optional<Integer> findReportCount(
            @Param("experienceId") Long experienceId
    );
}