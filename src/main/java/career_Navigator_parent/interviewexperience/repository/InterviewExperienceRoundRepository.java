package career_Navigator_parent.interviewexperience.repository;

import career_Navigator_parent.interviewexperience.entity.InterviewExperienceRound;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewExperienceRoundRepository
        extends JpaRepository<InterviewExperienceRound, Long> {

    @EntityGraph(
            attributePaths = {
                    "questions"
            }
    )
    @Query("""
            select distinct round
            from InterviewExperienceRound round
            where round.interviewExperience.id = :experienceId
            order by round.displayOrder asc
            """)
    List<InterviewExperienceRound>
    findDetailedRoundsByExperienceId(
            @Param("experienceId") Long experienceId
    );
}