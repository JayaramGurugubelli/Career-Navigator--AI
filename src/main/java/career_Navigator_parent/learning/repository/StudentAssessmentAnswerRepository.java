package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.StudentAssessmentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentAssessmentAnswerRepository
        extends JpaRepository<StudentAssessmentAnswer, Long> {

    List<StudentAssessmentAnswer>
    findByAttemptId(
            Long attemptId
    );

    Optional<StudentAssessmentAnswer>
    findByAttemptIdAndQuestionId(
            Long attemptId,
            Long questionId
    );
}