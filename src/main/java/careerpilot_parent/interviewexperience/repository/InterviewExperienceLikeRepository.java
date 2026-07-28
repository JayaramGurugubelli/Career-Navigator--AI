package careerpilot_parent.interviewexperience.repository;

import careerpilot_parent.interviewexperience.entity.InterviewExperienceLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterviewExperienceLikeRepository
        extends JpaRepository<InterviewExperienceLike, Long> {

    boolean existsByInterviewExperience_IdAndUser_Id(
            Long interviewExperienceId,
            Long userId
    );

    Optional<InterviewExperienceLike>
    findByInterviewExperience_IdAndUser_Id(
            Long interviewExperienceId,
            Long userId
    );

    long countByInterviewExperience_Id(
            Long interviewExperienceId
    );

    void deleteByInterviewExperience_IdAndUser_Id(
            Long interviewExperienceId,
            Long userId
    );

    void deleteAllByInterviewExperience_Id(
            Long interviewExperienceId
    );
}